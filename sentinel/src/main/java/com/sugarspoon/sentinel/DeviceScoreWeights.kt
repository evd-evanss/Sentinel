package com.sugarspoon.sentinel

data class DeviceScoreWeights(
    val rooted: Int = 20,
    val hookingDetected: Int = 20,
    val deviceMasked: Int = 15,
    val emulated: Int = 15,
    val virtualOS: Int = 15,
    val appCloned: Int = 10,
    val suspiciousReset: Int = 10,
    val gpsSpoofing: Int = 10,
    val autoClickerDetected: Int = 10,
    val screenSharing: Int = 5,
    val debuggingEnabled: Int = 5,
    val vpnActive: Int = 5,
    val proxyEnabled: Int = 5,
) {
    init {
        require(
            listOf(
                rooted,
                hookingDetected,
                deviceMasked,
                emulated,
                virtualOS,
                appCloned,
                suspiciousReset,
                gpsSpoofing,
                autoClickerDetected,
                screenSharing,
                debuggingEnabled,
                vpnActive,
                proxyEnabled,
            ).all { it >= 0 }
        ) { "Device score weights must be zero or positive." }
    }
}
