package com.sugarspoon.sentinel

data class SentinelConfig(
    val scoreWeights: DeviceScoreWeights = DeviceScoreWeights(),
)
