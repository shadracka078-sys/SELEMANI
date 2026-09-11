package com.example.data.model

import androidx.compose.ui.graphics.Color

enum class NetworkType(
    val displayName: String,
    val brandName: String,
    val brandColorHex: Long,
    val tagColorHex: Long,
    val prefix: String
) {
    M_PESA("M-Pesa", "Vodacom", 0xFFE60000, 0xFFFFEBEE, "VODA"),
    AIRTEL_MONEY("Airtel Money", "Airtel", 0xFFE50000, 0xFFFFF3F0, "AIRT"),
    TIGO_PESA("Tigo Pesa", "Mixx / Yas", 0xFF00377B, 0xFFE8F0FE, "TIGO"),
    HALOPESA("HaloPesa", "Halotel", 0xFFFF6F00, 0xFFFFF3E0, "HALO");

    val brandColor: Color get() = Color(brandColorHex)
    val tagColor: Color get() = Color(tagColorHex)

    companion object {
        fun fromString(name: String?): NetworkType {
            return entries.find { it.name.equals(name, ignoreCase = true) } ?: M_PESA
        }
    }
}
