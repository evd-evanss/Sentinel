package com.sugarspoon.sentinel.ui.checkout

import androidx.compose.ui.graphics.Color

fun Int.toRiskColor(): Color {
    return when {
        this <= 40 -> Color.Red
        this <= 75 -> Color(0xFFFFC107)
        else -> Color(0xFF00E676)
    }
}
