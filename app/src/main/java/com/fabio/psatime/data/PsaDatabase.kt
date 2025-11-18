package com.fabio.psatime.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [PsaResult::class], version = 1, exportSchema = false)
abstract class PsaDatabase : RoomDatabase() {

    abstract fun psaDao(): PsaDao

    companion object {
        @Volatile
        private var INSTANCE: PsaDatabase? = null

        fun getDatabase(context: Context): PsaDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PsaDatabase::class.java,
                    "psa_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}