package com.example.data.db

import androidx.room.TypeConverter
import com.example.data.model.NetworkType
import com.example.data.model.TransactionType

class Converters {
    @TypeConverter
    fun fromTransactionType(value: TransactionType?): String? {
        return value?.name
    }

    @TypeConverter
    fun toTransactionType(value: String?): TransactionType? {
        return value?.let { TransactionType.valueOf(it) }
    }

    @TypeConverter
    fun fromNetworkType(value: NetworkType?): String? {
        return value?.name
    }

    @TypeConverter
    fun toNetworkType(value: String?): NetworkType? {
        return value?.let { NetworkType.valueOf(it) }
    }
}
