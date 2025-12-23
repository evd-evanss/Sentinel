package com.sugarspoon.sentinel

data class DetectionResult(
    // --- DEVICE STATUS ---
    val isDebuggingEnabled: Boolean = false,
    val isEmulated: Boolean = false,
    val isRooted: Boolean = false,
    val isProxyEnabled: Boolean = false,
    val isDeviceMasked: Boolean = false,

    // --- TAMPERING & HOOKS ---
    val isAppTampered: Boolean = false,
    val isHookingDetected: Boolean = false,

    // --- SUSPICIOUS APPS & BEHAVIOR ---
    val isAutoClickerDetected: Boolean = false,
    val isAppCloned: Boolean = false,
    val isGpsSpoofing: Boolean = false,
    val isScreenSharing: Boolean = false,
    val isVpnActive: Boolean = false,
    val isVirtualOS: Boolean = false,
    val isSuspiciousReset: Boolean = false,

    // --- GEO ---
    val latitude: Double? = null,
    val longitude: Double? = null,

    // --- OVERALL SCORE ---
    val deviceScore: Int = 100,
    val userId: String? = null,
)
