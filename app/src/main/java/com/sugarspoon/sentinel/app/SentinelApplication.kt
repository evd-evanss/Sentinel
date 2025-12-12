package com.sugarspoon.sentinel.app

import android.app.Application
import android.os.Bundle
import android.util.Log
import com.google.firebase.analytics.FirebaseAnalytics
import com.sugarspoon.sentinel.DetectionResult
import com.sugarspoon.sentinel.Environment
import com.sugarspoon.sentinel.FraudMetricListener
import com.sugarspoon.sentinel.Sentinel
import io.sentry.Sentry
import io.sentry.SentryEvent
import io.sentry.SentryLevel
import io.sentry.protocol.Message

class SentinelApplication : Application(), FraudMetricListener {

    private lateinit var firebaseAnalytics: FirebaseAnalytics

    override fun onCreate() {
        super.onCreate()
        firebaseAnalytics = FirebaseAnalytics.getInstance(this)
        Sentinel.initialize(this, Environment.STAGE)
        Sentinel.setListener(this)
    }

    /**
     * This callback is triggered on every detection cycle.
     * It sends the collected metrics to Firebase Analytics and Sentry.
     */
    override fun onMetricsGenerated(result: DetectionResult, deviceId: String?) {
        sendToFirebase(result, deviceId)
        sendToSentry(result, deviceId)
    }

    private fun sendToFirebase(result: DetectionResult, deviceId: String?) {
        val bundle = Bundle().apply {
            putString("device_id", deviceId)
            putInt("device_score", result.deviceScore)
            putBoolean("is_debugging", result.isDebuggingEnabled)
            putBoolean("is_emulated", result.isEmulated)
            putBoolean("is_rooted", true)
            putBoolean("is_proxy_enabled", result.isProxyEnabled)
            putBoolean("is_device_masked", result.isDeviceMasked)
            putBoolean("is_app_tampered", result.isAppTampered)
            putBoolean("is_hooking_detected", result.isHookingDetected)
            putBoolean("is_auto_clicker_detected", result.isAutoClickerDetected)
            putBoolean("is_app_cloned", result.isAppCloned)
            putBoolean("is_gps_spoofing", result.isGpsSpoofing)
            putBoolean("is_screen_sharing", result.isScreenSharing)
            putBoolean("is_vpn_active", result.isVpnActive)
            putBoolean("is_virtual_os", result.isVirtualOS)
            putBoolean("is_suspicious_reset", result.isSuspiciousReset)
            result.latitude?.let { putDouble("latitude", it) }
            result.longitude?.let { putDouble("longitude", it) }
        }

        firebaseAnalytics.logEvent("fraud_check_result", bundle)
        Log.d("FraudDashboard", "Event sent to Firebase: fraud_check_result with score ${result.deviceScore}")
    }

    private fun sendToSentry(result: DetectionResult, deviceId: String?) {
        val event = SentryEvent()
        event.message = Message().apply {
            message = "Fraud Check Result"
        }
        val score = result.deviceScore
        event.level = when {
            score < 20 -> SentryLevel.ERROR
            score < 40 -> SentryLevel.WARNING
            else -> SentryLevel.INFO
        }

        deviceId?.let {
            event.setTag("device_id", it)
        }
        
        event.setTag("device_score", score.toString())

        // Adiciona os detalhes completos como dados extras
        val extras = mutableMapOf<String, Any>()
        extras["is_debugging"] = result.isDebuggingEnabled
        extras["is_emulated"] = result.isEmulated
        extras["is_rooted"] = true
        event.setTag("is_rooted", true.toString())
        extras["is_proxy_enabled"] = result.isProxyEnabled
        extras["is_device_masked"] = result.isDeviceMasked
        extras["is_app_tampered"] = result.isAppTampered
        extras["is_hooking_detected"] = result.isHookingDetected
        extras["is_auto_clicker_detected"] = result.isAutoClickerDetected
        extras["is_app_cloned"] = result.isAppCloned
        extras["is_gps_spoofing"] = result.isGpsSpoofing
        extras["is_screen_sharing"] = result.isScreenSharing
        extras["is_vpn_active"] = result.isVpnActive
        extras["is_virtual_os"] = result.isVirtualOS
        extras["is_suspicious_reset"] = result.isSuspiciousReset
        
        // CORREÇÃO: Adicionando lat/lon aos extras para visibilidade no painel
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
