package com.sugarspoon.sentinel

import android.Manifest
import android.accessibilityservice.AccessibilityServiceInfo
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.display.DisplayManager
import android.location.Location
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.CancellationSignal
import android.provider.Settings
import android.util.Log
import android.view.accessibility.AccessibilityManager
import androidx.annotation.RequiresPermission
import androidx.core.content.ContextCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.File
import kotlin.coroutines.resume

/**
 * Interface para receber os resultados de detecção de fraude.
 */
interface FraudMetricListener {
    fun onMetricsGenerated(result: DetectionResult, deviceId: String?)
}

@SuppressLint("StaticFieldLeak")
object Sentinel {

    private lateinit var context: Context
    private lateinit var environment: Environment
    private var listener: FraudMetricListener? = null

    private const val TAG = "Sentinel"
    private val scope = CoroutineScope(Dispatchers.Default)
    private var isInitialized = false
    private var enableDebugLogs = false

    private val _detectionResult = MutableStateFlow(DetectionResult())
    val detectionResult = _detectionResult.asStateFlow()

    fun initialize(
        context: Context,
        environment: Environment = Environment.PROD,
        enableDebugLogs: Boolean = false,
    ) {
        this.enableDebugLogs = enableDebugLogs

        if (isInitialized) {
            Log.w(TAG, "Sentinel is already initialized.")
            return
        }

        synchronized(this) {
            if(isInitialized) return
            this.context = context.applicationContext
            this.environment = environment
            startMonitoring()
            isInitialized = true
        }
    }

    fun setListener(listener: FraudMetricListener) {
        this.listener = listener
    }

    private fun startMonitoring(intervalMillis: Long = 60000) {
        log("Starting continuous monitoring...")
        scope.launch {
            while (true) {
                val result = runAllChecks()
                _detectionResult.value = result
                val deviceId = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
                listener?.onMetricsGenerated(result, deviceId)
                delay(intervalMillis)
            }
        }
    }

    private fun log(message: String) {
        if (environment.name == Environment.STAGE.name && enableDebugLogs.or(false)) {
            Log.d(TAG, message)
        }
    }

    private suspend fun runAllChecks(): DetectionResult {
        if (!isInitialized) {
            throw IllegalStateException("Sentinel must be initialized before use.")
        }
        log("---- Running detection cycle ----")
        val hasFineLocation = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val hasCoarseLocation = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

        val location = if (hasFineLocation || hasCoarseLocation) {
            try {
                getFreshLocation()
            } catch (e: SecurityException) {
                log("Permission check passed but execute failed: ${e.message}")
                null
            }
        } else {
            log("Location permissions not granted. Skipping location check.")
            null
        }
        
        val rawResult = DetectionResult(
            isDebuggingEnabled = checkDebugging(),
            isEmulated = checkEmulator(),
            isRooted = checkRoot(),
            isProxyEnabled = checkProxy(),
            isDeviceMasked = checkDeviceMasking(),
            isAppTampered = checkAppTampering(),
            isHookingDetected = checkHooking(),
            isAutoClickerDetected = checkAutoClicker(),
            isAppCloned = checkAppCloning(),
            isGpsSpoofing = checkGpsSpoofing(),
            isScreenSharing = checkScreenSharing(),
            isVpnActive = checkVpn(),
            isVirtualOS = checkVirtualOS(),
            isSuspiciousReset = checkSuspiciousReset(),
            latitude = location?.first,
            longitude = location?.second
        )
        
        val finalResult = rawResult.copy(deviceScore = calculateDeviceScore(rawResult))
        log("Cycle results: $finalResult")
        return finalResult
    }

    private fun calculateDeviceScore(result: DetectionResult): Int {
        var score = 100
        if (result.isRooted) score -= 20
        if (result.isHookingDetected) score -= 20
        if (result.isAppTampered) score -= 20
        if (result.isDeviceMasked) score -= 15
        if (result.isEmulated) score -= 15
        if (result.isVirtualOS) score -= 15
        if (result.isAppCloned) score -= 10
        if (result.isSuspiciousReset) score -= 10
        if (result.isGpsSpoofing) score -= 10
        if (result.isAutoClickerDetected) score -= 10
        if (result.isScreenSharing) score -= 5
        if (result.isDebuggingEnabled) score -= 5
        if (result.isVpnActive) score -= 5
        if (result.isProxyEnabled) score -= 5
        
        val finalScore = score.coerceAtLeast(0)
        log("Calculated device score: $finalScore")
        return finalScore
    }
    
    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION])
    private suspend fun getFreshLocation(): Pair<Double, Double>? = suspendCancellableCoroutine {
        continuation ->
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val provider = LocationManager.FUSED_PROVIDER
            val cancellationSignal = CancellationSignal()
            continuation.invokeOnCancellation { cancellationSignal.cancel() }
            
            log("Requesting current location with Fused Provider...")
            locationManager.getCurrentLocation(
                provider,
                cancellationSignal,
                context.mainExecutor
            ) { location ->
                if (location != null) {
                    log("Fused Provider success: lat=${location.latitude}, lon=${location.longitude}")
                    continuation.resume(Pair(location.latitude, location.longitude))
                } else {
                    log("Fused Provider returned null. Trying last known location as fallback.")
                    continuation.resume(getLastKnownLocationLegacy())
                }
            }
        } else {
            continuation.resume(getLastKnownLocationLegacy())
        }
    }

    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION])
    private fun getLastKnownLocationLegacy(): Pair<Double, Double>? {
        return try {
            val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val providers = locationManager.getProviders(true)
            var bestLocation: Location? = null
            log("Legacy Check: Enabled providers: $providers")
            for (provider in providers) {
                val location = locationManager.getLastKnownLocation(provider)
                log("Legacy Check: Provider '$provider' location: $location")
                if (location != null && (bestLocation == null || location.accuracy < bestLocation.accuracy)) {
                    bestLocation = location
                }
            }
            log("Legacy Check: Best found location: $bestLocation")
            bestLocation?.let { Pair(it.latitude, it.longitude) }
        } catch (e: Exception) {
            log("Error getting legacy location: ${e.message}")
            null
        }
    }

    // ... (O resto das suas funções check continuam aqui, sem alterações)
    private fun checkRoot(): Boolean {
        val paths = arrayOf(
            "/system/app/Superuser.apk", "/sbin/su", "/system/bin/su", "/system/xbin/su",
            "/data/local/xbin/su", "/data/local/bin/su", "/system/sd/xbin/su",
            "/system/bin/failsafe/su", "/data/local/su", "/su/bin/su"
        )
        var isRooted = false
        for (path in paths) {
            if (File(path).exists()) {
                log("Root detected at path: $path")
                isRooted = true
                break
            }
        }
        log("checkRoot: $isRooted")
        return isRooted
    }

    private fun checkEmulator(): Boolean {
        val isEmulator = (
                Build.FINGERPRINT.startsWith("generic")
                        || Build.FINGERPRINT.startsWith("unknown")
                        || Build.FINGERPRINT.contains("emulator") // Check for the word 'emulator'
                        || Build.FINGERPRINT.contains("sdk") // Check for 'sdk' in fingerprint
                        || Build.MODEL.contains("google_sdk")
                        || Build.MODEL.contains("Emulator")
                        || Build.MODEL.contains("Android SDK built for x86")
                        || Build.MANUFACTURER.contains("Genymotion")
                        || (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
                        || "google_sdk" == Build.PRODUCT
                        || Build.HARDWARE.contains("goldfish") // Emulator-specific hardware
                        || Build.HARDWARE.contains("ranchu") // Another emulator-specific hardware
                )
        log("checkEmulator: $isEmulator")
        return isEmulator
    }

    private fun checkDebugging(): Boolean {
        val isDebugging = try {
            Settings.Global.getInt(context.contentResolver, Settings.Global.ADB_ENABLED, 0) == 1
        } catch (e: Exception) {
            log("checkDebugging error: ${e.message}")
            false
        }
        log("checkDebugging: $isDebugging")
        return isDebugging
    }

    @RequiresPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    private fun checkGpsSpoofing(): Boolean {
        var isSpoofing = false
        try {
            isSpoofing = Settings.Secure.getInt(context.contentResolver, Settings.Secure.ALLOW_MOCK_LOCATION, 0) != 0
            if (isSpoofing) log("GPS spoofing detected via ALLOW_MOCK_LOCATION.")
        } catch (e: Exception) {
            log("checkGpsSpoofing (settings) error: ${e.message}")
        }

        if (!isSpoofing && Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            try {
                val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
                for (provider in locationManager.allProviders) {
                    val location: Location? = locationManager.getLastKnownLocation(provider)
                    if (location != null && location.isFromMockProvider) {
                        log("GPS spoofing detected via mock provider.")
                        isSpoofing = true
                        break
                    }
                }
            } catch (e: SecurityException) {
                log("checkGpsSpoofing (location) security error: ${e.message}")
            }
        }
        log("checkGpsSpoofing: $isSpoofing")
        return isSpoofing
    }

    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    private fun checkVpn(): Boolean {
        var isVpn = false
        try {
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetwork = cm.activeNetwork
            if (activeNetwork != null) {
                val caps = cm.getNetworkCapabilities(activeNetwork)
                if (caps != null && caps.hasTransport(NetworkCapabilities.TRANSPORT_VPN)) {
                    isVpn = true
                }
            }
        } catch(e: Exception) {
            log("checkVpn error: ${e.message}")
        }
        log("checkVpn: $isVpn")
        return isVpn
    }

    private fun checkProxy(): Boolean {
        val proxyHost = System.getProperty("http.proxyHost")
        val proxyPort = System.getProperty("http.proxyPort")
        val isProxy = proxyHost != null || proxyPort != null
        if (isProxy) log("Proxy detected: $proxyHost:$proxyPort")
        log("checkProxy: $isProxy")
        return isProxy
    }

    private fun checkDeviceMasking(): Boolean {
        val suspiciousHardware = setOf("goldfish", "ranchu", "generic", "sdk", "emulator")
        val realManufacturers = setOf("samsung", "google", "xiaomi", "oneplus", "oppo", "vivo", "realme", "motorola", "lg", "sony", "huawei")

        val hardware = Build.HARDWARE.lowercase()
        val fingerprint = Build.FINGERPRINT.lowercase()
        val manufacturer = Build.MANUFACTURER.lowercase()
        val brand = Build.BRAND.lowercase()

        val hasEmulatorHardware = suspiciousHardware.any { hardware.contains(it) || fingerprint.contains(it) }
        val claimsRealManufacturer = realManufacturers.any { manufacturer == it || brand == it }

        var isMasked = claimsRealManufacturer && hasEmulatorHardware
        if (isMasked) {
            log("Device masking detected: Claims to be '$manufacturer' but has suspicious hardware/fingerprint '$hardware'/'$fingerprint'.")
        }

        if (!isMasked && checkHooking()) {
            log("Device masking suspected: Hooking framework detected, device properties are untrustworthy.")
            isMasked = true
        }

        log("checkDeviceMasking: $isMasked")
        return isMasked
    }

    private fun checkAppTampering(): Boolean {
        var isSignatureTampered = false
        var isInstallerTampered = false

        try {
            val packageName = context.packageName
            val packageManager = context.packageManager

            // Check 1: Installer verification (using modern API)
            val installer = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                packageManager.getInstallSourceInfo(packageName).installingPackageName
            } else {
                @Suppress("DEPRECATION")
                packageManager.getInstallerPackageName(packageName)
            }

            if ("com.android.vending" != installer) {
                log("App tampering: App not installed from Play Store. Installer: $installer")
                isInstallerTampered = true
            }

            // Check 2: Signature verification
            val originalSignature = ""

            val signatures = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val signingInfo = packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNING_CERTIFICATES).signingInfo
                signingInfo?.apkContentsSigners
            } else {
                @Suppress("DEPRECATION")
                packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES).signatures
            }

            if (signatures.isNullOrEmpty()) {
                log("App tampering: No signatures found.")
                isSignatureTampered = true
            } else {
                isSignatureTampered = signatures.none { it.toCharsString() == originalSignature }
                if (isSignatureTampered) log("App tampering: Signature mismatch.")
            }

        } catch (e: Exception) {
            log("checkAppTampering error: ${e.message}")
            return true
        }

        val isTampered = isSignatureTampered || isInstallerTampered
        log("checkAppTampering: $isTampered (Signature: $isSignatureTampered, Installer: $isInstallerTampered)")
        return isTampered
    }

    private fun checkHooking(): Boolean {
        var isHooking = false

        try {
            throw Exception("HookDetection")
        } catch (e: Exception) {
            val suspiciousKeywords = setOf("xposed", "substrate", "frida", "hook")
            e.stackTrace.forEach { element ->
                suspiciousKeywords.forEach { keyword ->
                    if (element.className.lowercase().contains(keyword)) {
                        log("Hooking detected in stack trace: ${element.className}")
                        isHooking = true
                        return@forEach
                    }
                }
                if (isHooking) return@forEach
            }
        }

        if (isHooking) {
            log("checkHooking: true (from stacktrace)")
            return true
        }

        val suspiciousPackages = setOf(
            "de.robv.android.xposed.installer",
            "io.va.exposed",
            "com.saurik.substrate",
            "com.topjohnwu.magisk"
        )
        val packageManager = context.packageManager
        for (pkg in suspiciousPackages) {
            try {
                packageManager.getPackageInfo(pkg, 0)
                log("Hooking/Root framework package detected: $pkg")
                isHooking = true
                break
            } catch (e: PackageManager.NameNotFoundException) {
            }
        }

        log("checkHooking: $isHooking")
        return isHooking
    }

    private fun checkAutoClicker(): Boolean {
        var isAutoClicker = false
        try {
            val am = context.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
            val services = am.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_GENERIC)
            for (service in services) {
                val id = service.id.lowercase()
                if (id.contains("autoclick") || id.contains("clicker")) {
                    log("Auto-clicker detected: ${service.id}")
                    isAutoClicker = true
                    break
                }
            }
        } catch (e: Exception) {
            log("checkAutoClicker error: ${e.message}")
        }
        log("checkAutoClicker: $isAutoClicker")
        return isAutoClicker
    }

    private fun checkAppCloning(): Boolean {
        val path = context.filesDir.path
        val isCloned = path.contains("clone") || path.contains("virtual")
        if (isCloned) log("App cloning detected in path: $path")
        log("checkAppCloning: $isCloned")
        return isCloned
    }

    private fun checkScreenSharing(): Boolean {
        var isSharing = false
        try {
            val dm = context.getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
            if (dm.displays.size > 1) {
                log("Multiple displays detected. Count: ${dm.displays.size}")
                isSharing = true
            }
        } catch (e: Exception) {
             log("checkScreenSharing error: ${e.message}")
        }
        log("checkScreenSharing: $isSharing")
        return isSharing
    }

    private fun checkVirtualOS(): Boolean {
        val isVirtual = Build.FINGERPRINT.contains("vmos") || Build.MODEL.contains("virtual")
        if (isVirtual) {
            log("Virtual OS (third-party) detected. Fingerprint: ${Build.FINGERPRINT}, Model: ${Build.MODEL}")
        }
        log("checkVirtualOS: $isVirtual")
        return isVirtual
    }

    private fun checkSuspiciousReset(): Boolean {
        try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            val firstInstallTime = packageInfo.firstInstallTime
            val currentTime = System.currentTimeMillis()

            val suspiciousDuration = 60 * 1000
            val isSuspicious = (currentTime - firstInstallTime) < suspiciousDuration

            if (isSuspicious) {
                log("Suspicious reset: App installed in the last minute.")
            }
            log("checkSuspiciousReset: $isSuspicious (Install time: $firstInstallTime)")
            return isSuspicious

        } catch (e: PackageManager.NameNotFoundException) {
            log("checkSuspiciousReset error: ${e.message}")
            return false
        }
    }
}