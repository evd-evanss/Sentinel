package com.sugarspoon.sentinel.ui.checkout.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sugarspoon.sentinel.DetectionResult
import com.sugarspoon.sentinel.ui.checkout.screen.CheckoutPreviewData
import com.sugarspoon.sentinel.ui.checkout.screen.FraudDecision

@Composable
fun ChallengeDialog(
    result: DetectionResult,
    decision: FraudDecision,
    answer: String,
    onAnswerChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Desafio de identidade") },
        text = {
            Column {
                Text(
                    text = "Antes de processar o pagamento, o Sentinel encontrou sinais reais de fraude neste dispositivo."
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Device Score: ${result.deviceScore}/100",
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
                Spacer(modifier = Modifier.height(8.dp))
                decision.reasons.forEach { reason ->
                    Text(text = "- $reason")
                }
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = answer,
                    onValueChange = onAnswerChange,
                    label = { Text("Digite 1234 para validar") },
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                enabled = answer == "1234"
            ) {
                Text("Validar e continuar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar compra")
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun ChallengeDialogPreview() {
    ChallengeDialog(
        result = CheckoutPreviewData.riskyResult,
        decision = CheckoutPreviewData.riskyDecision,
        answer = "1234",
        onAnswerChange = {},
        onDismiss = {},
        onConfirm = {}
    )
}
