package com.sugarspoon.sentinel.ui.checkout.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sugarspoon.sentinel.ui.checkout.screen.PaymentMethod
import com.sugarspoon.sentinel.ui.shared.SelectableOptionButton

@Composable
fun PaymentMethodSelector(
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
            SelectableOptionButton(
                label = "Pix",
                isSelected = selected == PaymentMethod.PIX,
                modifier = Modifier.weight(1f),
                onClick = { onSelected(PaymentMethod.PIX) }
            )
            SelectableOptionButton(
                label = "Cartao",
                isSelected = selected == PaymentMethod.CARD,
                modifier = Modifier.weight(1f),
                onClick = { onSelected(PaymentMethod.CARD) }
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun PaymentMethodSelectorPreview() {
    PaymentMethodSelector(
        selected = PaymentMethod.PIX,
        onSelected = {}
    )
}
