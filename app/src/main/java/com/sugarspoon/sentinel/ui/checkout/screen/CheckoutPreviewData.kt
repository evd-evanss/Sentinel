package com.sugarspoon.sentinel.ui.checkout.screen

import com.sugarspoon.sentinel.DetectionResult
import com.sugarspoon.sentinel.DeviceRiskLevel

internal object CheckoutPreviewData {
    val safeDecision = FraudDecision(
        shouldChallenge = false,
        reasons = emptyList()
    )

    val riskyDecision = FraudDecision(
        shouldChallenge = true,
        reasons = listOf(
            "Acesso root detectado (-30)",
            "Auto-clicker ativo (-25)",
            "Ambiente emulado detectado (-20)"
        )
    )

    val safeResult = DetectionResult(
        deviceScore = 95,
        deviceRiskLevel = DeviceRiskLevel.LOW
    )

    val riskyResult = DetectionResult(
        isRooted = true,
        isAutoClickerDetected = true,
        isEmulated = true,
        deviceScore = 15,
        deviceRiskLevel = DeviceRiskLevel.HIGH
    )

    val safeUiState = CheckoutUiState(
        detectionResult = safeResult,
        decision = safeDecision
    )

    val challengeUiState = CheckoutUiState(
        checkoutState = CheckoutState.CHALLENGE_REQUIRED,
        detectionResult = riskyResult,
        decision = riskyDecision
    )

    val approvedUiState = CheckoutUiState(
        checkoutState = CheckoutState.APPROVED,
        detectionResult = safeResult,
        decision = safeDecision
    )
}