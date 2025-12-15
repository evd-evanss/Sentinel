package com.sugarspoon.sentinel.ui.details

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sugarspoon.sentinel.app.R
import com.sugarspoon.sentinel.ui.fraud.IndicatorInfo
import com.sugarspoon.sentinel.ui.fraud.RiskLevel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IndicatorDetailScreen(
    indicator: IndicatorInfo,
    onBackPress: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = indicator.title), color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBackPress) {
                        Icon(Icons.Default.ArrowBack, contentDescription = stringResource(id = R.string.back), tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Black)
            )
        },
        containerColor = Color.Black
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text(
                text = "${stringResource(id = R.string.risk_level)}: ${indicator.riskLevel.name}", 
                color = indicator.riskLevel.toColor(), 
                fontWeight = FontWeight.Bold, 
                fontSize = 18.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(id = indicator.description), 
                color = Color.LightGray, 
                fontSize = 16.sp, 
                lineHeight = 24.sp
            )
        }
    }
}

private fun RiskLevel.toColor(): Color {
    return when (this) {
        RiskLevel.CRITICAL -> Color.Red
        RiskLevel.HIGH -> Color(0xFFFF8000)
        RiskLevel.MEDIUM -> Color(0xFFFFC107)
        RiskLevel.LOW -> Color.Yellow
        RiskLevel.NONE -> Color.Green
    }
}

@Preview(showBackground = true)
@Composable
fun IndicatorDetailScreenPreview() {
    IndicatorDetailScreen(
        indicator = IndicatorInfo(
            id = "isRooted",
            title = R.string.indicator_root_title,
            isDetected = true,
            riskLevel = RiskLevel.CRITICAL,
            description = R.string.indicator_root_desc,
        ),
        onBackPress = {}
    )
}


