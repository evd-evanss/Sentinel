package com.sugarspoon.sentinel.ui.fraud

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sugarspoon.sentinel.app.R

@Composable
fun IndicatorListItem(indicator: IndicatorInfo, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(stringResource(id = indicator.title), color = Color.LightGray, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (indicator.isDetected) stringResource(id = R.string.status_risk_detected) else stringResource(id = R.string.status_secure),
                color = if (indicator.isDetected) Color.Red else Color.Green,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = stringResource(id = indicator.title), // Melhor para acessibilidade
            tint = Color.Gray
        )
    }
}
