package com.sugarspoon.sentinel.app.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.sugarspoon.sentinel.Sentinel
import com.sugarspoon.sentinel.ui.details.IndicatorDetailScreen
import com.sugarspoon.sentinel.ui.fraud.FraudListScreen
import com.sugarspoon.sentinel.ui.fraud.mapResultsToInfo

@Composable
fun AppNavigator() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Routes.FRAUD_LIST) {
        composable(Routes.FRAUD_LIST) {
            FraudListScreen(
                onNavigate = { route -> navController.navigate(route) }
            )
        }
        composable(Routes.INDICATOR_DETAIL) { backStackEntry ->
            val indicatorId = backStackEntry.arguments?.getString("indicatorId")
            val detectionResult by Sentinel.detectionResult.collectAsState()
            val indicators = mapResultsToInfo(detectionResult)
            val indicator = indicators.find { it.id == indicatorId }
            if (indicator != null) {
                IndicatorDetailScreen(
                    indicator = indicator,
                    onBackPress = { navController.popBackStack() }
                )
            }
        }
    }
}
