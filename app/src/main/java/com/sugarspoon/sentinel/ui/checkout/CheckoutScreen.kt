package com.sugarspoon.sentinel.ui.checkout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sugarspoon.sentinel.DetectionResult

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

@Composable
private fun ProductSummary() {
    Card(
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF161616)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Checkout",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Fone Sentinel Pro",
                color = Color.LightGray,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Pedido #SNT-2048",
                color = Color.Gray,
                fontFamily = FontFamily.Monospace,
                fontSize = 13.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = Color.DarkGray)
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Total", color = Color.LightGray, fontSize = 16.sp)
                Text(
                    text = "R$ 289,90",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun SecurityPreview(
    result: DetectionResult,
    decision: FraudDecision,
    riskColor: Color,
) {
    Card(
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF101820)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Analise Sentinel",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${result.deviceScore}/100",
                    color = riskColor,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = if (decision.shouldChallenge) {
                    "Desafio exigido antes do pagamento."
                } else {
                    "Nenhum desafio necessario para este pagamento."
                },
                color = if (decision.shouldChallenge) Color(0xFFFFC107) else Color(0xFF00E676),
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(12.dp))
            if (decision.reasons.isEmpty()) {
                Text(
                    text = "Sinais observados: nenhum",
                    color = Color.Gray,
                    fontSize = 14.sp
                )
            } else {
                decision.reasons.forEach { reason ->
                    Text(
                        text = "- $reason",
                        color = Color.LightGray,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun PaymentMethodSelector(
    selected: PaymentMethod,
    onSelected: (PaymentMethod) -> Unit,
) {
    Column {
        Text(
            text = "Forma de pagamento",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            PaymentOptionButton(
                label = "Pix",
                isSelected = selected == PaymentMethod.PIX,
                modifier = Modifier.weight(1f),
                onClick = { onSelected(PaymentMethod.PIX) }
            )
            PaymentOptionButton(
                label = "Cartao",
                isSelected = selected == PaymentMethod.CARD,
                modifier = Modifier.weight(1f),
                onClick = { onSelected(PaymentMethod.CARD) }
            )
        }
    }
}

@Composable
private fun PaymentOptionButton(
    label: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val colors = if (isSelected) {
        ButtonDefaults.buttonColors(containerColor = Color(0xFF2962FF), contentColor = Color.White)
    } else {
        ButtonDefaults.outlinedButtonColors(contentColor = Color.LightGray)
    }

    OutlinedButton(
        onClick = onClick,
        colors = colors,
        modifier = modifier.height(52.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(text = label, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun CheckoutAction(
    checkoutState: CheckoutState,
    paymentMethod: PaymentMethod,
    decision: FraudDecision,
    onPay: () -> Unit,
    onReset: () -> Unit,
) {
    when (checkoutState) {
        CheckoutState.READY,
        CheckoutState.CHALLENGE_REQUIRED -> {
            Button(
                onClick = onPay,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00C853)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "Pagar com ${paymentMethod.label}",
                    color = Color.Black,
                    fontWeight = FontWeight.Bold
                )
            }
            if (decision.shouldChallenge) {
                Text(
                    text = "O pagamento sera pausado para desafio porque o Sentinel encontrou risco real neste dispositivo.",
                    color = Color.Gray,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        CheckoutState.APPROVED -> {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF102A18), RoundedCornerShape(8.dp))
                    .padding(16.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Pagamento aprovado",
                        color = Color(0xFF00E676),
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "A compra foi liberada depois da avaliacao Sentinel.",
                        color = Color.LightGray,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    TextButton(onClick = onReset) {
                        Text("Simular nova compra")
                    }
                }
            }
        }
    }
}

@Composable
private fun ChallengeDialog(
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

private fun Int.toRiskColor(): Color {
    return when {
        this <= 40 -> Color.Red
        this <= 75 -> Color(0xFFFFC107)
        else -> Color(0xFF00E676)
    }
}
