package com.sugarspoon.sentinel

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DeviceScoreCalculatorTest {
    @Test
    fun cleanDeviceKeepsMaximumScore() {
        val score = DeviceScoreCalculator.calculate(DetectionResult())

        assertEquals(100, score.value)
        assertEquals(DeviceRiskLevel.LOW, score.riskLevel)
        assertTrue(score.reasons.isEmpty())
    }

    @Test
    fun detectedIndicatorsApplyDefaultPenaltiesAndReasons() {
        val result = DetectionResult(
            isRooted = true,
            isHookingDetected = true,
            isVpnActive = true,
        )

        val score = DeviceScoreCalculator.calculate(result)

        assertEquals(55, score.value)
        assertEquals(DeviceRiskLevel.MEDIUM, score.riskLevel)
        assertEquals(
            listOf(
                DeviceScoreReason(SentinelIndicator.ROOT, 20),
                DeviceScoreReason(SentinelIndicator.HOOKING, 20),
                DeviceScoreReason(SentinelIndicator.VPN, 5),
            ),
            score.reasons,
        )
    }

    @Test
    fun scoreIsClampedAtZero() {
        val result = DetectionResult(
            isRooted = true,
            isHookingDetected = true,
            isDeviceMasked = true,
            isEmulated = true,
            isVirtualOS = true,
            isAppCloned = true,
            isSuspiciousReset = true,
            isGpsSpoofing = true,
            isAutoClickerDetected = true,
            isScreenSharing = true,
            isDebuggingEnabled = true,
            isVpnActive = true,
            isProxyEnabled = true,
        )

        val score = DeviceScoreCalculator.calculate(result)

        assertEquals(0, score.value)
        assertEquals(DeviceRiskLevel.HIGH, score.riskLevel)
    }

    @Test
    fun customWeightsCanTuneRiskModel() {
        val result = DetectionResult(
            isRooted = true,
            isVpnActive = true,
        )
        val weights = DeviceScoreWeights(
            rooted = 50,
            vpnActive = 0,
        )

        val score = DeviceScoreCalculator.calculate(result, weights)

        assertEquals(50, score.value)
        assertEquals(DeviceRiskLevel.MEDIUM, score.riskLevel)
        assertEquals(
            listOf(
                DeviceScoreReason(SentinelIndicator.ROOT, 50),
            ),
            score.reasons,
        )
    }
}
