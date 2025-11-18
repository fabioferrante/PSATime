package com.fabio.psatime.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "psa_results")
data class PsaResult(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val year: Int,
    val value: Float,
    val timestamp: Long = System.currentTimeMillis()
)