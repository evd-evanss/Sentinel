package com.sugarspoon.sentinel

enum class SentinelIndicator {
    ROOT,
    HOOKING,
    DEVICE_MASKING,
    EMULATOR,
    VIRTUAL_OS,
    APP_CLONING,
    SUSPICIOUS_RESET,
    GPS_SPOOFING,
    AUTO_CLICKER,
    SCREEN_SHARING,
    DEBUGGING,
    VPN,
    PROXY
}

data class DeviceScoreReason(
    val indicator: SentinelIndicator,
    val penalty: Int,
)
