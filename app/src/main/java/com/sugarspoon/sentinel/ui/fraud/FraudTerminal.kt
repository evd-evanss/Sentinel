package com.sugarspoon.sentinel.ui.fraud

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sugarspoon.sentinel.Sentinel
import com.sugarspoon.sentinel.ui.theme.SentinelTheme

@Composable
fun FraudTerminal() {
    val detectionResult by Sentinel.detectionResult.collectAsState()
    val indicators = mapResultsToInfo(detectionResult)

    val requestPermissions = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { }

    LaunchedEffect(key1 = Unit) {
        requestPermissions.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        DeviceScoreDisplay(result = detectionResult, allIndicators = indicators)
        Spacer(modifier = Modifier.height(24.dp))

        SectionHeader("STATUS DO DISPOSITIVO")
        CheckRow("debugging", detectionResult.isDebuggingEnabled)
        CheckRow("is_emulated", detectionResult.isEmulated)
        CheckRow("is_jailbroken", detectionResult.isRooted)
        CheckRow("is_proxy", detectionResult.isProxyEnabled)
        CheckRow("is_device_masked", detectionResult.isDeviceMasked)

        Spacer(modifier = Modifier.height(16.dp))

        SectionHeader("ADULTERAÇÃO & GANCHOS")
        CheckRow("app_tampering", detectionResult.isAppTampered)
        CheckRow("hooking", detectionResult.isHookingDetected)

        Spacer(modifier = Modifier.height(16.dp))

        SectionHeader("APLICATIVOS E COMPORTAMENTOS SUSPEITOS")
        CheckRow("auto_clicker", detectionResult.isAutoClickerDetected)
        CheckRow("clone_apps", detectionResult.isAppCloned)
        CheckRow("gps_spoofers", detectionResult.isGpsSpoofing)
        CheckRow("screen_sharing", detectionResult.isScreenSharing)
        CheckRow("vpn_spoofers", detectionResult.isVpnActive)
        CheckRow("virtual_os", detectionResult.isVirtualOS)
        CheckRow("suspicious_reset", detectionResult.isSuspiciousReset)
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = "--- $title ---",
        color = Color.Green,
        fontFamily = FontFamily.Monospace,
        modifier = Modifier.padding(vertical = 4.dp)
    )
}

@Composable
private fun CheckRow(label: String, isDetected: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "$label:",
            color = Color.Green,
            fontFamily = FontFamily.Monospace
        )
        Text(
            text = isDetected.toString(),
            color = if (isDetected) Color.Red else Color.Green,
            fontFamily = FontFamily.Monospace
        )
    }
}

@Preview(showBackground = true)
@Composable
fun FraudTerminalPreview() {
    SentinelTheme {
        FraudTerminal()
    }
}
