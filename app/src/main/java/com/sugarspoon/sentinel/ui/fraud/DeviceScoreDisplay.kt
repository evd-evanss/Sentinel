package com.sugarspoon.sentinel.ui.fraud

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sugarspoon.sentinel.DetectionResult
import com.sugarspoon.sentinel.app.R

@Composable
fun DeviceScoreDisplay(result: DetectionResult, allIndicators: List<IndicatorInfo>) {
    val score = result.deviceScore
    val scoreColor = when {
        score > 75 -> Color.Green
        score > 40 -> Color(0xFFFFC107) // Âmbar
        else -> Color.Red
    }

    val summaryTextRes = when {
        score > 75 -> R.string.score_summary_secure
        score > 40 -> R.string.score_summary_warning
        else -> R.string.score_summary_danger
    }

    val detectedRisks = allIndicators.filter { it.isDetected }
    val risksCount = detectedRisks.size

    val animatedProgress by animateFloatAsState(
        targetValue = score / 100f,
        animationSpec = tween(durationMillis = 1000),
        label = "ScoreAnimation"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()) // <-- CORREÇÃO APLICADA AQUI
    ) {
        Text(
            text = stringResource(id = R.string.device_score),
            color = Color.White,
            fontFamily = FontFamily.Monospace,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(24.dp))
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(180.dp)
        ) {
            CircularProgressIndicator(
                progress = { 1f },
                modifier = Modifier.fillMaxSize(),
                color = Color.DarkGray,
                strokeWidth = 16.dp,
                trackColor = Color.Transparent,
                strokeCap = StrokeCap.Round
            )
            CircularProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier.fillMaxSize(),
                color = scoreColor,
                strokeWidth = 16.dp,
                trackColor = Color.Transparent,
                strokeCap = StrokeCap.Round
            )
            Text(
                text = "$score%",
                color = scoreColor,
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
        }
        Spacer(modifier = Modifier.height(24.dp))

        Icon(
            imageVector = if (score > 40) Icons.Default.CheckCircle else Icons.Default.Warning,
            contentDescription = null,
            tint = scoreColor,
            modifier = Modifier.size(32.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))

        if (risksCount > 0) {
            Text(
                text = stringResource(id = R.string.risks_detected_summary, risksCount),
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
        }

        Text(
            text = stringResource(id = summaryTextRes),
            color = Color.LightGray,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        if (detectedRisks.isNotEmpty()) {
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = stringResource(id = R.string.detected_risks_title),
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
            detectedRisks.forEach { indicator ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Warning,
                            contentDescription = null,
                            tint = Color.Red,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.padding(start = 8.dp))
                        Text(
                            text = stringResource(id = indicator.title),
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Text(
                        text = stringResource(id = indicator.description),
                        color = Color.Gray,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(start = 24.dp, top = 4.dp)
                    )
                }
                Divider(color = Color.DarkGray, thickness = 0.5.dp)
            }
        }
    }
}
