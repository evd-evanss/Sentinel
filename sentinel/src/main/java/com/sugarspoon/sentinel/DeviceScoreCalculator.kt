package com.sugarspoon.sentinel

object DeviceScoreCalculator {
    fun calculate(
        result: DetectionResult,
        weights: DeviceScoreWeights = DeviceScoreWeights(),
    ): DeviceScore {
        val reasons = buildList {
            addReason(result.isRooted, SentinelIndicator.ROOT, weights.rooted)
            addReason(result.isHookingDetected, SentinelIndicator.HOOKING, weights.hookingDetected)
            addReason(result.isDeviceMasked, SentinelIndicator.DEVICE_MASKING, weights.deviceMasked)
            addReason(result.isEmulated, SentinelIndicator.EMULATOR, weights.emulated)
            addReason(result.isVirtualOS, SentinelIndicator.VIRTUAL_OS, weights.virtualOS)
            addReason(result.isAppCloned, SentinelIndicator.APP_CLONING, weights.appCloned)
            addReason(result.isSuspiciousReset, SentinelIndicator.SUSPICIOUS_RESET, weights.suspiciousReset)
            addReason(result.isGpsSpoofing, SentinelIndicator.GPS_SPOOFING, weights.gpsSpoofing)
            addReason(result.isAutoClickerDetected, SentinelIndicator.AUTO_CLICKER, weights.autoClickerDetected)
            addReason(result.isScreenSharing, SentinelIndicator.SCREEN_SHARING, weights.screenSharing)
            addReason(result.isDebuggingEnabled, SentinelIndicator.DEBUGGING, weights.debuggingEnabled)
            addReason(result.isVpnActive, SentinelIndicator.VPN, weights.vpnActive)
            addReason(result.isProxyEnabled, SentinelIndicator.PROXY, weights.proxyEnabled)
        }

        val score = (100 - reasons.sumOf { it.penalty }).coerceAtLeast(0)
        return DeviceScore(
            value = score,
            riskLevel = score.toDeviceRiskLevel(),
            reasons = reasons,
        )
    }

    private fun Int.toDeviceRiskLevel(): DeviceRiskLevel {
        return when {
            this <= 40 -> DeviceRiskLevel.HIGH
            this <= 75 -> DeviceRiskLevel.MEDIUM
            else -> DeviceRiskLevel.LOW
        }
    }

    private fun MutableList<DeviceScoreReason>.addReason(
        isDetected: Boolean,
        indicator: SentinelIndicator,
        penalty: Int,
    ) {
        if (isDetected && penalty > 0) {
            add(DeviceScoreReason(indicator, penalty))
        }
    }
}
