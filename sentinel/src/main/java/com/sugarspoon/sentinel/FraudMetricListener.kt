package com.sugarspoon.sentinel

/**
 * Interface para receber os resultados de detecção de fraude.
 */
interface FraudMetricListener {
    fun onMetricsGenerated(result: DetectionResult, deviceId: String?)
}