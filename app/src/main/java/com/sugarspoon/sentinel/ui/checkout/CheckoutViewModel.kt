package com.sugarspoon.sentinel.ui.checkout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sugarspoon.sentinel.DetectionResult
import com.sugarspoon.sentinel.DeviceRiskLevel
import com.sugarspoon.sentinel.DeviceScoreReason
import com.sugarspoon.sentinel.Sentinel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

enum class PaymentMethod(val label: String) {
    PIX("Pix"),
    CARD("Cartao")
}

enum class CheckoutState {
    READY,
    CHALLENGE_REQUIRED,
    APPROVED
}

data class FraudDecision(
    val shouldChallenge: Boolean,
    val reasons: List<String>,
)

data class CheckoutUiState(
    val paymentMethod: PaymentMethod = PaymentMethod.PIX,
    val checkoutState: CheckoutState = CheckoutState.READY,
    val challengeAnswer: String = "",
    val detectionResult: DetectionResult = DetectionResult(),
    val decision: FraudDecision = DetectionResult().toFraudDecision(),
)

class CheckoutViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(CheckoutUiState())
    val uiState: StateFlow<CheckoutUiState> = _uiState.asStateFlow()

    init {
        Sentinel.detectionResult
            .onEach { detectionResult ->
                _uiState.update { current ->
                    val decision = detectionResult.toFraudDecision()
                    current.copy(
                        detectionResult = detectionResult,
                        decision = decision,
                        checkoutState = if (decision.shouldChallenge && current.checkoutState == CheckoutState.APPROVED) {
                            CheckoutState.CHALLENGE_REQUIRED
                        } else {
                            current.checkoutState
                        },
                        challengeAnswer = if (decision.shouldChallenge && current.checkoutState == CheckoutState.APPROVED) {
                            ""
                        } else {
                            current.challengeAnswer
                        },
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    fun onPaymentMethodSelected(paymentMethod: PaymentMethod) {
        _uiState.update { current ->
            current.copy(paymentMethod = paymentMethod)
        }
    }

    fun onPayClicked() {
        _uiState.update { current ->
            current.copy(
                checkoutState = if (current.decision.shouldChallenge) {
                    CheckoutState.CHALLENGE_REQUIRED
                } else {
                    CheckoutState.APPROVED
                }
            )
        }
    }

    fun onChallengeAnswerChanged(answer: String) {
        _uiState.update { current ->
            current.copy(challengeAnswer = answer)
        }
    }

    fun onChallengeDismissed() {
        _uiState.update { current ->
            current.copy(
                checkoutState = CheckoutState.READY,
                challengeAnswer = "",
            )
        }
    }

    fun onChallengeConfirmed() {
        _uiState.update { current ->
            current.copy(checkoutState = CheckoutState.APPROVED)
        }
    }

    fun onResetCheckout() {
        _uiState.update { current ->
            current.copy(
                checkoutState = CheckoutState.READY,
                challengeAnswer = "",
            )
        }
    }
}

private fun DetectionResult.toFraudDecision(): FraudDecision {
    val riskReasons = scoreReasons.toReasonLabels()
    val fallbackReasons = buildList {
        if (isRooted) add("Acesso root detectado")
        if (isHookingDetected) add("Framework de hooking ativo")
        if (isAutoClickerDetected) add("Auto-clicker ativo")
        if (isEmulated) add("Ambiente emulado detectado")
        if (isDeviceMasked) add("Mascaramento de dispositivo detectado")
        if (isAppCloned) add("App rodando em ambiente clonado")
        if (isGpsSpoofing) add("GPS spoofing detectado")
    }

    val shouldChallenge = deviceScore <= 40 ||
        deviceRiskLevel == DeviceRiskLevel.HIGH ||
        isRooted ||
        isHookingDetected ||
        isAutoClickerDetected ||
        (isEmulated && isDeviceMasked)

    return FraudDecision(
        shouldChallenge = shouldChallenge,
        reasons = riskReasons.ifEmpty { fallbackReasons }
    )
}

private fun List<DeviceScoreReason>.toReasonLabels(): List<String> {
    return map { reason ->
        "${reason.indicator.reasonLabel} (-${reason.penalty})"
    }
}
