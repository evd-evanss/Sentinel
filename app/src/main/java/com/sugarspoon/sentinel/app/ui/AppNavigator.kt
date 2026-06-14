package com.sugarspoon.sentinel.app.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sugarspoon.sentinel.ui.checkout.CheckoutScreen
import com.sugarspoon.sentinel.ui.checkout.CheckoutViewModel

@Composable
fun AppNavigator() {
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
