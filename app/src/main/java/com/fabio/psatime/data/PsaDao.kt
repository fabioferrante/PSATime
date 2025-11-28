package com.fabio.psatime.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface PsaDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertResult(result: PsaResult)

    // NOVO: Para restaurar vários de uma vez
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(results: List<PsaResult>)

    @Update
    suspend fun updateResult(result: PsaResult)

    @Delete
    suspend fun deleteResult(result: PsaResult)

    // NOVO: Deletar tudo (útil antes de restaurar para não duplicar)
    @Query("DELETE FROM psa_results")
    suspend fun deleteAll()

    @Query("SELECT * FROM psa_results ORDER BY timestamp DESC")
    fun getAllResults(): Flow<List<PsaResult>>

    // NOVO: Pega a lista direta (sem Flow) para o backup
    @Query("SELECT * FROM psa_results")
    suspend fun getAllResultsList(): List<PsaResult>
}