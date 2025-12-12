package com.sugarspoon.sentinel.ui.fraud

import androidx.annotation.StringRes
import com.sugarspoon.sentinel.DetectionResult
import com.sugarspoon.sentinel.app.R

enum class RiskLevel {
    CRITICAL, HIGH, MEDIUM, LOW, NONE
}

data class IndicatorInfo(
    val id: String,
    @StringRes val title: Int,
    val isDetected: Boolean,
    val riskLevel: RiskLevel,
    @StringRes val description: Int
)

fun mapResultsToInfo(result: DetectionResult): List<IndicatorInfo> {
    return listOf(
        IndicatorInfo(
            id = "isRooted",
            title = R.string.indicator_root_title,
            isDetected = result.isRooted,
            riskLevel = RiskLevel.CRITICAL,
            description = R.string.indicator_root_desc
        ),
        IndicatorInfo(
            id = "isHookingDetected",
            title = R.string.indicator_hooking_title,
            isDetected = result.isHookingDetected,
            riskLevel = RiskLevel.CRITICAL,
            description = R.string.indicator_hooking_desc
        ),
        IndicatorInfo(
            id = "isAppTampered",
            title = R.string.indicator_tampering_title,
            isDetected = result.isAppTampered,
            riskLevel = RiskLevel.CRITICAL,
            description = R.string.indicator_tampering_desc
        ),
        IndicatorInfo(
            id = "isDeviceMasked",
            title = R.string.indicator_masking_title,
            isDetected = result.isDeviceMasked,
            riskLevel = RiskLevel.HIGH,
            description = R.string.indicator_masking_desc
        ),
        IndicatorInfo(
            id = "isEmulated",
            title = R.string.indicator_emulator_title,
            isDetected = result.isEmulated,
            riskLevel = RiskLevel.HIGH,
            description = R.string.indicator_emulator_desc
        ),
        IndicatorInfo(
            id = "isVirtualOS",
            title = R.string.indicator_virtual_os_title,
            isDetected = result.isVirtualOS,
            riskLevel = RiskLevel.HIGH,
            description = R.string.indicator_virtual_os_desc
        ),
        IndicatorInfo(
            id = "isAppCloned",
            title = R.string.indicator_cloned_apps_title,
            isDetected = result.isAppCloned,
            riskLevel = RiskLevel.MEDIUM,
            description = R.string.indicator_cloned_apps_desc
        ),
        IndicatorInfo(
            id = "isSuspiciousReset",
            title = R.string.indicator_suspicious_reset_title,
            isDetected = result.isSuspiciousReset,
            riskLevel = RiskLevel.MEDIUM,
            description = R.string.indicator_suspicious_reset_desc
        ),
        IndicatorInfo(
            id = "isGpsSpoofing",
            title = R.string.indicator_gps_spoofing_title,
            isDetected = result.isGpsSpoofing,
            riskLevel = RiskLevel.MEDIUM,
            description = R.string.indicator_gps_spoofing_desc
        ),
        IndicatorInfo(
            id = "isAutoClickerDetected",
            title = R.string.indicator_autoclicker_title,
            isDetected = result.isAutoClickerDetected,
            riskLevel = RiskLevel.MEDIUM,
            description = R.string.indicator_autoclicker_desc
        ),
        IndicatorInfo(
            id = "isScreenSharing",
            title = R.string.indicator_screen_sharing_title,
            isDetected = result.isScreenSharing,
            riskLevel = RiskLevel.LOW,
            description = R.string.indicator_screen_sharing_desc
        ),
        IndicatorInfo(
            id = "isDebuggingEnabled",
            title = R.string.indicator_debugging_title,
            isDetected = result.isDebuggingEnabled,
            riskLevel = RiskLevel.LOW,
            description = R.string.indicator_debugging_desc
        ),
        IndicatorInfo(
            id = "isVpnActive",
            title = R.string.indicator_vpn_title,
            isDetected = result.isVpnActive,
            riskLevel = RiskLevel.LOW,
            description = R.string.indicator_vpn_desc
        ),
        IndicatorInfo(
            id = "isProxyEnabled",
            title = R.string.indicator_proxy_title,
            isDetected = result.isProxyEnabled,
            riskLevel = RiskLevel.LOW,
            description = R.string.indicator_proxy_desc
        )
    )
}
