package com.example.ui.util

import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object CurrencyFormatter {
    private val tzsFormat = NumberFormat.getNumberInstance(Locale("en", "TZ")).apply {
        maximumFractionDigits = 0
        minimumFractionDigits = 0
    }

    fun formatTZS(amount: Double): String {
        return "TZS ${tzsFormat.format(amount)}"
    }

    fun formatNumber(amount: Double): String {
        return tzsFormat.format(amount)
    }

    fun formatSignedTZS(amount: Double): String {
        val sign = if (amount > 0) "+" else if (amount < 0) "-" else ""
        return "$sign${formatTZS(kotlin.math.abs(amount))}"
    }

    fun formatTime(timestamp: Long): String {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }

    fun formatDate(timestamp: Long): String {
        val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }

    fun formatDateOnly(timestamp: Long): String {
        val sdf = SimpleDateFormat("EEE, dd MMM yyyy", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }
}
