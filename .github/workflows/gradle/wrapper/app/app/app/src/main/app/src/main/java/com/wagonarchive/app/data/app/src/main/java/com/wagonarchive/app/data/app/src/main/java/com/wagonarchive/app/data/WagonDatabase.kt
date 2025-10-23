package com.wagonarchive.app.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [WagonEntity::class], version = 1, exportSchema = false)
abstract class WagonDatabase : RoomDatabase() {
    abstract fun wagonDao(): WagonDao

    companion object {
        @Volatile
        private var INSTANCE: WagonDatabase? = null

        fun getDatabase(context: Context): WagonDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    WagonDatabase::class.java,
                    "wagon_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
