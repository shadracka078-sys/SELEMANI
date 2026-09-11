package com.example.data.model

import androidx.compose.ui.graphics.Color

enum class TransactionType(
    val title: String,
    val swahiliTitle: String,
    val description: String,
    val colorHex: Long
) {
    DEPOSIT(
        title = "Deposit",
        swahiliTitle = "Kuweka Pesa",
        description = "Mteja anaweka pesa (Cash in drawer + | Float -)",
        colorHex = 0xFF0D8A58 // Green
    ),
    WITHDRAWAL(
        title = "Withdrawal",
        swahiliTitle = "Kutoa Pesa",
        description = "Mteja anatoa pesa (Cash out drawer - | Float +)",
        colorHex = 0xFFD32F2F // Red
    ),
    BUY_FLOAT(
        title = "Buy Float",
        swahiliTitle = "Kununua Float",
        description = "Kununua float benki/super-agent (Cash - | Float +)",
        colorHex = 0xFF1976D2 // Blue
    ),
    SELL_FLOAT(
        title = "Float to Cash",
        swahiliTitle = "Kubadili Float",
        description = "Kubadilisha float kuwa taslimu (Cash + | Float -)",
        colorHex = 0xFFF57C00 // Orange
    ),
    EXPENSE(
        title = "Expense",
        swahiliTitle = "Matumizi",
        description = "Gharama za ofisi/kibanda (Cash -)",
        colorHex = 0xFF7B1FA2 // Purple
    ),
    COMMISSION(
        title = "Commission",
        swahiliTitle = "Kamisheni",
        description = "Faida/kamisheni iliyoingizwa kwenye float (+Float)",
        colorHex = 0xFF00796B // Teal
    );

    val color: Color get() = Color(colorHex)
}
