package com.sugarspoon.sentinel.app

enum class AppRoutes(val destination: String) {
    FRAUD_LIST("fraudList"),
    INDICATOR_DETAIL("indicatorDetail/{indicatorId}")
}