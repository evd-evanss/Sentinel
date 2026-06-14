package com.sugarspoon.sentinel

data class DeviceScore(
    val value: Int,
    val riskLevel: DeviceRiskLevel,
    val reasons: List<DeviceScoreReason>,
)
