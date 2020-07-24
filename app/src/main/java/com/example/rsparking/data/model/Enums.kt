package com.example.rsparking.data.model

import com.example.rsparking.R

enum class PayPlan(val code: String, price: Double, freeWash: Int, icon: Int) {
    MONTH6("MONTH6", 1200.0, 2, R.drawable.ic_month6),
    MONTH1_FULL("MONTH1_FULL", 500.0, 4, R.drawable.ic_month1_full),
    MONTH1_BASIC("MONTH1_BASIC", 380.0, 0, R.drawable.ic_month1_basic),
    MONTH_MIN10("MONTHMIN10", 260.0, 0, R.drawable.ic_minus10),
    MONTH_MIN20("MONTHMIN20", 130.0, 0, R.drawable.ic_minus20),
    DAILY("DAILY", 1.0, 0, R.drawable.ic_daily)
}

enum class ServiceType(val code: String, val price: Double) {
    FULL("FULL", 40.0),
    EXTERIOR("EXTERIOR", 20.0),
    NONE("NONE", 0.0)
}
