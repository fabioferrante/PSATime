package com.fabio.psatime.ui.dashboard

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.fabio.psatime.data.PsaDatabase
import com.fabio.psatime.data.PsaResult
import kotlinx.coroutines.launch

// ... (Sealed class PsaStatus continua igual) ...
sealed class PsaStatus {
    object Empty : PsaStatus()
    object Normal : PsaStatus()
    object Warning : PsaStatus()
    object Danger : PsaStatus()
}

class PsaViewModel(application: Application) : AndroidViewModel(application) {

    private val psaDao = PsaDatabase.getDatabase(application).psaDao()

    val allResults: LiveData<List<PsaResult>> = psaDao.getAllResults().asLiveData()

    val currentPsaStatus: LiveData<PsaStatus> = allResults.map { results ->
        calculateStatus(results)
    }

    // ... (fun calculateStatus continua igual) ...
    private fun calculateStatus(results: List<PsaResult>): PsaStatus {
        if (results.isEmpty()) return PsaStatus.Empty
        if (results.size < 2) return PsaStatus.Normal

        val latest = results[0]
        val previous = results[1]

        if (results.size == 2) {
            val diff = latest.value - previous.value
            return if (diff >= 0.4f) PsaStatus.Warning else PsaStatus.Normal
        }

        val anchor = results[2]
        val previousWasYellow = (previous.value - anchor.value) >= 0.4f
        val timeDiffMs = latest.timestamp - previous.timestamp
        val timeDiffDays = timeDiffMs / (1000 * 60 * 60 * 24)
        val isFollowUp = timeDiffDays < 120

        if (isFollowUp && previousWasYellow) {
            val diffVsAnchor = latest.value - anchor.value
            val diffVsPrevious = latest.value - previous.value

            if (diffVsAnchor >= 0.4f && diffVsPrevious >= 0f) return PsaStatus.Danger
            if (diffVsAnchor >= 0.4f) return PsaStatus.Warning
            return PsaStatus.Normal
        }

        val diffAnnual = latest.value - previous.value
        return if (diffAnnual >= 0.4f) PsaStatus.Warning else PsaStatus.Normal
    }

    // --- Novas Funções CRUD ---

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
}