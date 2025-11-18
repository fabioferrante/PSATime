package com.fabio.psatime.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PsaDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertResult(result: PsaResult)

    @Delete
    suspend fun deleteResult(result: PsaResult)

    @Query("SELECT * FROM psa_results ORDER BY timestamp DESC")
    fun getAllResults(): Flow<List<PsaResult>>
}