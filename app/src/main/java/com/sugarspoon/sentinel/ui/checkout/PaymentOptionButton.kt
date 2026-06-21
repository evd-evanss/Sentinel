package com.sugarspoon.sentinel.ui.checkout

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun PaymentOptionButton(
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
