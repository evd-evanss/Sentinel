package com.sugarspoon.sentinel.app

import android.app.Application
import android.util.Log
import com.sugarspoon.sentinel.DetectionResult
import com.sugarspoon.sentinel.DeviceRiskLevel
import com.sugarspoon.sentinel.Environment
import com.sugarspoon.sentinel.FraudMetricListener
import com.sugarspoon.sentinel.Sentinel
import io.sentry.Sentry
import io.sentry.SentryEvent
import io.sentry.SentryLevel
import io.sentry.protocol.Message

class SentinelApplication : Application(), FraudMetricListener {

    override fun onCreate() {
        super.onCreate()
        Sentinel.initialize(this, Environment.STAGE, enableLogs = true)
        Sentinel.setListener(this)
    }

    /**
     * This callback is triggered on every detection cycle.
     * It sends the collected metrics to Sentry.
     */
    override fun onMetricsGenerated(result: DetectionResult, deviceId: String?) {
        logDetectionResult(result, deviceId)
        sendToSentry(result, deviceId)
    }

    private fun logDetectionResult(result: DetectionResult, deviceId: String?) {
        val reasons = result.scoreReasons.joinToString { reason ->
            "${reason.indicator.name}(-${reason.penalty})"
        }.ifBlank { "none" }

        Log.d(
            "FraudDashboard",
            "Detection metrics: deviceId=$deviceId, " +
                "deviceScore=${result.deviceScore}, " +
                "deviceRiskLevel=${result.deviceRiskLevel}, " +
                "scoreReasons=$reasons"
        )
    }

    private fun sendToSentry(result: DetectionResult, deviceId: String?) {
        val event = SentryEvent()
        event.message = Message().apply {
            message = "Fraud Check Result"
        }
        val score = result.deviceScore
        event.level = when {
            result.deviceRiskLevel == DeviceRiskLevel.HIGH -> SentryLevel.ERROR
            result.deviceRiskLevel == DeviceRiskLevel.MEDIUM -> SentryLevel.WARNING
            else -> SentryLevel.INFO
        }

        deviceId?.let {
            event.setTag("device_id", it)
        }
        
        event.setTag("device_score", score.toString())
        event.setTag("device_risk_level", result.deviceRiskLevel.name.lowercase())

        val extras = mutableMapOf<String, Any>()

        extras["is_debugging"] = result.isDebuggingEnabled
        extras["is_emulated"] = result.isEmulated
        extras["is_rooted"] = result.isRooted
        event.setTag("is_rooted", result.isRooted.toString())
        extras["is_proxy_enabled"] = result.isProxyEnabled
        extras["is_device_masked"] = result.isDeviceMasked
        extras["is_hooking_detected"] = result.isHookingDetected
        extras["is_auto_clicker_detected"] = result.isAutoClickerDetected
        extras["is_app_cloned"] = result.isAppCloned
        extras["is_gps_spoofing"] = result.isGpsSpoofing
        extras["is_screen_sharing"] = result.isScreenSharing
        extras["is_vpn_active"] = result.isVpnActive
        extras["is_virtual_os"] = result.isVirtualOS
        extras["is_suspicious_reset"] = result.isSuspiciousReset
        extras["score_reasons"] = result.scoreReasons.joinToString(",") { reason ->
            "${reason.indicator.name}:${reason.penalty}"
        }
        
        result.latitude?.let {
            extras["latitude"] = it
            event.setTag("latitude", it.toString())
         }
        result.longitude?.let { 
            extras["longitude"] = it
            event.setTag("longitude", it.toString())
        }

        event.setExtras(extras)

        Sentry.captureEvent(event)
        Log.d("FraudDashboard", "Event sent to Sentry: level ${event.level}, score ${score}")
    }
}
