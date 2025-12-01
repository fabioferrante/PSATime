package com.fabio.psatime.ui.dashboard

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.fabio.psatime.data.PsaDatabase
import com.fabio.psatime.data.PsaResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// Define os estados de status
sealed class PsaStatus {
    object Empty : PsaStatus()
    object Normal : PsaStatus()
    object Warning : PsaStatus()
    object Danger : PsaStatus()
    object CriticalHigh : PsaStatus()
}

class PsaViewModel(application: Application) : AndroidViewModel(application) {

    private val psaDao = PsaDatabase.getDatabase(application).psaDao()

    val allResults: LiveData<List<PsaResult>> = psaDao.getAllResults().asLiveData()

    val currentPsaStatus: LiveData<PsaStatus> = allResults.map { results ->
        calculateStatus(results)
    }

    private fun calculateStatus(results: List<PsaResult>): PsaStatus {
        if (results.isEmpty()) {
            return PsaStatus.Empty
        }

        // --- Lógica para o Primeiro Resultado (Único) ---
        if (results.size == 1) {
            val value = results[0].value
            return when {
                value > 10f -> PsaStatus.CriticalHigh // Vermelho (> 10)
                value > 4f -> PsaStatus.Warning       // NOVO: Amarelo (> 4)
                else -> PsaStatus.Normal              // Verde (<= 4)
            }
        }

        val latest = results[0]
        val previous = results[1]

        // --- Lógica de Acompanhamento Anual (2 resultados) ---
        if (results.size == 2) {
            val diff = latest.value - previous.value
            return if (diff >= 0.4f) PsaStatus.Warning else PsaStatus.Normal
        }

        // --- Lógica de 3+ resultados ---
        val anchor = results[2]
        val previousWasYellow = (previous.value - anchor.value) >= 0.4f
        val timeDiffMs = latest.timestamp - previous.timestamp
        val timeDiffDays = timeDiffMs / (1000 * 60 * 60 * 24)
        val isFollowUp = timeDiffDays < 120

        if (isFollowUp && previousWasYellow) {
            val diffVsAnchor = latest.value - anchor.value
            val diffVsPrevious = latest.value - previous.value

            if (diffVsAnchor >= 0.4f && diffVsPrevious >= 0f) {
                return PsaStatus.Danger
            }
            if (diffVsAnchor >= 0.4f) {
                return PsaStatus.Warning
            }
            return PsaStatus.Normal
        }

        val diffAnnual = latest.value - previous.value
        return if (diffAnnual >= 0.4f) PsaStatus.Warning else PsaStatus.Normal
    }

    // ... (restante das funções de insert, update, delete, backup mantêm-se iguais) ...
    fun insertResult(year: Int, value: Float) {
        viewModelScope.launch {
            val result = PsaResult(year = year, value = value)
            psaDao.insertResult(result)
        }
    }

    fun updateResult(result: PsaResult) {
        viewModelScope.launch {
            psaDao.updateResult(result)
        }
    }

    fun deleteResult(result: PsaResult) {
        viewModelScope.launch {
            psaDao.deleteResult(result)
        }
    }

    suspend fun getResultsForBackup(): List<PsaResult> {
        return withContext(Dispatchers.IO) {
            psaDao.getAllResultsList()
        }
    }

    fun restoreBackup(results: List<PsaResult>) {
        viewModelScope.launch {
            psaDao.deleteAll()
            psaDao.insertAll(results)
        }
    }
}