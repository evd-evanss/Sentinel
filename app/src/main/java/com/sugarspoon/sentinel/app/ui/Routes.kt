package com.sugarspoon.sentinel.app.ui

object Routes {
    const val FRAUD_LIST = "fraudList"
    const val INDICATOR_DETAIL = "indicatorDetail/{indicatorId}"

    fun indicatorDetail(indicatorId: String) = "indicatorDetail/$indicatorId"
}
