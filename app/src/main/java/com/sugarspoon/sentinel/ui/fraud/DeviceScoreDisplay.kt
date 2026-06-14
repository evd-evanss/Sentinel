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
import com.sugarspoon.sentinel.DeviceRiskLevel
import com.sugarspoon.sentinel.DeviceScoreReason
import com.sugarspoon.sentinel.SentinelIndicator
import com.sugarspoon.sentinel.app.R

@Composable
fun DeviceScoreDisplay(result: DetectionResult, allIndicators: List<IndicatorInfo>) {
    val score = result.deviceScore
    val scoreColor = when (result.deviceRiskLevel) {
        DeviceRiskLevel.LOW -> Color.Green
        DeviceRiskLevel.MEDIUM -> Color(0xFFFFC107) // Âmbar
        DeviceRiskLevel.HIGH -> Color.Red
    }

    val summaryTextRes = when (result.deviceRiskLevel) {
        DeviceRiskLevel.LOW -> R.string.score_summary_secure
        DeviceRiskLevel.MEDIUM -> R.string.score_summary_warning
        DeviceRiskLevel.HIGH -> R.string.score_summary_danger
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
            .verticalScroll(rememberScrollState())
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
            imageVector = if (result.deviceRiskLevel == DeviceRiskLevel.HIGH) Icons.Default.Warning else Icons.Default.CheckCircle,
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

        Spacer(modifier = Modifier.height(24.dp))
        ScoreDebugDetails(
            result = result,
            scoreColor = scoreColor
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

@Composable
private fun ScoreDebugDetails(
    result: DetectionResult,
    scoreColor: Color,
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = stringResource(id = R.string.score_debug_title),
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )
        Spacer(modifier = Modifier.height(12.dp))
        ScoreParameterRow(
            name = "deviceScore",
            value = result.deviceScore.toString(),
            valueColor = scoreColor
        )
        ScoreParameterRow(
            name = "deviceRiskLevel",
            value = result.deviceRiskLevel.name,
            valueColor = scoreColor
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "scoreReasons",
            color = Color.White,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        if (result.scoreReasons.isEmpty()) {
            Text(
                text = stringResource(id = R.string.score_reasons_empty),
                color = Color.Gray,
                fontSize = 14.sp,
                fontFamily = FontFamily.Monospace
            )
        } else {
            result.scoreReasons.forEach { reason ->
                ScoreReasonRow(reason = reason)
            }
        }
    }
}

@Composable
private fun ScoreParameterRow(
    name: String,
    value: String,
    valueColor: Color,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "$name:",
            color = Color.Gray,
            fontFamily = FontFamily.Monospace,
            fontSize = 14.sp,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            color = valueColor,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
        )
    }
}

@Composable
private fun ScoreReasonRow(reason: DeviceScoreReason) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(id = reason.indicator.titleRes()),
            color = Color.LightGray,
            fontSize = 14.sp,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = "-${reason.penalty}",
            color = Color.Red,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
        )
    }
}

private fun SentinelIndicator.titleRes(): Int {
    return when (this) {
        SentinelIndicator.ROOT -> R.string.indicator_root_title
        SentinelIndicator.HOOKING -> R.string.indicator_hooking_title
        SentinelIndicator.DEVICE_MASKING -> R.string.indicator_masking_title
        SentinelIndicator.EMULATOR -> R.string.indicator_emulator_title
        SentinelIndicator.VIRTUAL_OS -> R.string.indicator_virtual_os_title
        SentinelIndicator.APP_CLONING -> R.string.indicator_cloned_apps_title
        SentinelIndicator.SUSPICIOUS_RESET -> R.string.indicator_suspicious_reset_title
        SentinelIndicator.GPS_SPOOFING -> R.string.indicator_gps_spoofing_title
        SentinelIndicator.AUTO_CLICKER -> R.string.indicator_autoclicker_title
        SentinelIndicator.SCREEN_SHARING -> R.string.indicator_screen_sharing_title
        SentinelIndicator.DEBUGGING -> R.string.indicator_debugging_title
        SentinelIndicator.VPN -> R.string.indicator_vpn_title
        SentinelIndicator.PROXY -> R.string.indicator_proxy_title
    }
}
