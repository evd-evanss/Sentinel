package com.sugarspoon.sentinel.ui.checkout.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sugarspoon.sentinel.ui.checkout.components.ChallengeDialog
import com.sugarspoon.sentinel.ui.checkout.components.CheckoutAction
import com.sugarspoon.sentinel.ui.checkout.components.PaymentMethodSelector
import com.sugarspoon.sentinel.ui.checkout.components.ProductSummary
import com.sugarspoon.sentinel.ui.checkout.components.SecurityPreview
import com.sugarspoon.sentinel.ui.checkout.components.toRiskColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    uiState: CheckoutUiState,
    onPaymentMethodSelected: (PaymentMethod) -> Unit,
    onPayClicked: () -> Unit,
    onResetCheckout: () -> Unit,
    onChallengeAnswerChanged: (String) -> Unit,
    onChallengeDismissed: () -> Unit,
    onChallengeConfirmed: () -> Unit,
) {
    val riskColor = uiState.detectionResult.deviceScore.toRiskColor()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Sentinel Store", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Black)
            )
        },
        containerColor = Color.Black
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ProductSummary()
            SecurityPreview(
                result = uiState.detectionResult,
                decision = uiState.decision,
                riskColor = riskColor,
            )
            PaymentMethodSelector(
                selected = uiState.paymentMethod,
                onSelected = onPaymentMethodSelected
            )
            CheckoutAction(
                checkoutState = uiState.checkoutState,
                paymentMethod = uiState.paymentMethod,
                decision = uiState.decision,
                onPay = onPayClicked,
                onReset = onResetCheckout
            )
        }

        if (uiState.checkoutState == CheckoutState.CHALLENGE_REQUIRED) {
            ChallengeDialog(
                result = uiState.detectionResult,
                decision = uiState.decision,
                answer = uiState.challengeAnswer,
                onAnswerChange = onChallengeAnswerChanged,
                onDismiss = onChallengeDismissed,
                onConfirm = onChallengeConfirmed
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun CheckoutScreenReadyPreview() {
    CheckoutScreen(
        uiState = CheckoutPreviewData.safeUiState,
        onPaymentMethodSelected = {},
        onPayClicked = {},
        onResetCheckout = {},
        onChallengeAnswerChanged = {},
        onChallengeDismissed = {},
        onChallengeConfirmed = {}
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun CheckoutScreenChallengePreview() {
    CheckoutScreen(
        uiState = CheckoutPreviewData.challengeUiState,
        onPaymentMethodSelected = {},
        onPayClicked = {},
        onResetCheckout = {},
        onChallengeAnswerChanged = {},
        onChallengeDismissed = {},
        onChallengeConfirmed = {}
    )
}
