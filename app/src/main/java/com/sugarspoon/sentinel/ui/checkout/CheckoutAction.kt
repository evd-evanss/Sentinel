package com.sugarspoon.sentinel.ui.checkout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CheckoutAction(
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
