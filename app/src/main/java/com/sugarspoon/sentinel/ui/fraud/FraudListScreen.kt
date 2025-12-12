package com.sugarspoon.sentinel.ui.fraud

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sugarspoon.sentinel.Sentinel
import com.sugarspoon.sentinel.app.R
import com.sugarspoon.sentinel.app.ui.Routes
import kotlinx.coroutines.launch
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FraudListScreen(
    onNavigate: (String) -> Unit,
) {
    val detectionResult by Sentinel.detectionResult.collectAsState()
    val indicators = mapResultsToInfo(detectionResult)
    val context = LocalContext.current
    
    var showMenu by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf(false) }
    var showPermissionDeniedDialog by remember { mutableStateOf(false) }

    val requestPermissions = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissionsMap ->
        val isGranted = permissionsMap.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) ||
                permissionsMap.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false)

        if (!isGranted) {
            showPermissionDeniedDialog = true
        }
    }

    LaunchedEffect(key1 = Unit) {
        requestPermissions.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    if (showPermissionDeniedDialog) {
        PermissionDialog(
            onConfirm = { 
                showPermissionDeniedDialog = false
                context.openAppSettings()
            },
            onDismiss = { showPermissionDeniedDialog = false }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.app_name), color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Black),
                actions = {
                    Box {
                        IconButton(onClick = { showMenu = true }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "Menu", tint = Color.White)
                        }
                        DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.show_security_score)) },
                                onClick = { 
                                    scope.launch { sheetState.show() }.invokeOnCompletion { showBottomSheet = true } 
                                    showMenu = false
                                }
                            )
                            Divider()
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.language_english)) },
                                onClick = { 
                                    changeLanguage(context, "en")
                                    showMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.language_portuguese)) },
                                onClick = { 
                                    changeLanguage(context, "pt")
                                    showMenu = false
                                }
                            )
                        }
                    }
                }
            )
        },
        containerColor = Color.Black
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 16.dp)
        ) {
            items(
                items = indicators,
                key = { it.id }
            ) { indicator ->
                IndicatorListItem(indicator = indicator) {
                    onNavigate(Routes.indicatorDetail(indicator.id))
                }
                Divider(color = Color.DarkGray, thickness = 0.5.dp)
            }
        }

        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = { showBottomSheet = false },
                sheetState = sheetState,
                containerColor = Color.DarkGray
            ) {
                DeviceScoreDisplay(result = detectionResult, allIndicators = indicators)
            }
        }
    }
}

@Composable
private fun PermissionDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = stringResource(id = R.string.permission_required_title)) },
        text = { Text(text = stringResource(id = R.string.permission_required_text)) },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text(stringResource(id = R.string.open_settings))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(id = R.string.cancel))
            }
        }
    )
}

private fun Context.openAppSettings() {
    val intent = Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.fromParts("package", packageName, null)
    )
    startActivity(intent)
}

private fun changeLanguage(context: Context, language: String) {
    val locale = Locale(language)
    Locale.setDefault(locale)
    val resources = context.resources
    val config = resources.configuration
    config.setLocale(locale)
    resources.updateConfiguration(config, resources.displayMetrics)
    (context as? Activity)?.recreate()
}
