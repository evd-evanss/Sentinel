package com.sugarspoon.sentinel.ui.checkout

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun CheckoutScreenEntryPoint() {
    val viewModel: CheckoutViewModel = viewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    CheckoutScreen(
        uiState = uiState,
        onPaymentMethodSelected = viewModel::onPaymentMethodSelected,
        onPayClicked = viewModel::onPayClicked,
        onResetCheckout = viewModel::onResetCheckout,
        onChallengeAnswerChanged = viewModel::onChallengeAnswerChanged,
        onChallengeDismissed = viewModel::onChallengeDismissed,
        onChallengeConfirmed = viewModel::onChallengeConfirmed,
    )
}
