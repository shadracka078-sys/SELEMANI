package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.data.model.CashDrawerEntity
import com.example.data.model.DailyClosingEntity
import com.example.data.model.FloatBalanceEntity
import com.example.data.model.TransactionEntity

@Database(
    entities = [
        TransactionEntity::class,
        FloatBalanceEntity::class,
        CashDrawerEntity::class,
        DailyClosingEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class WakalaDatabase : RoomDatabase() {
    abstract fun wakalaDao(): WakalaDao

    companion object {
        @Volatile
        private var INSTANCE: WakalaDatabase? = null

        fun getDatabase(context: Context): WakalaDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    WakalaDatabase::class.java,
                    "wakala_mobile_money_db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
