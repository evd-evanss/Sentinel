package com.sugarspoon.sentinel.ui.shared

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun SelectableOptionButton(
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

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun SelectableOptionButtonPreview() {
    Column {
        SelectableOptionButton(
            label = "Selecionado",
            isSelected = true,
            modifier = Modifier.width(180.dp),
            onClick = {}
        )
        Spacer(modifier = Modifier.height(12.dp))
        SelectableOptionButton(
            label = "Disponivel",
            isSelected = false,
            modifier = Modifier.width(180.dp),
            onClick = {}
        )
    }
}
