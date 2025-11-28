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

// ... (Sealed class PsaStatus mantém igual) ...
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

    // ... (fun calculateStatus mantém igual) ...
    private fun calculateStatus(results: List<PsaResult>): PsaStatus {
        if (results.isEmpty()) {
            return PsaStatus.Empty
        }

        if (results.size == 1) {
            return if (results[0].value > 10f) PsaStatus.CriticalHigh else PsaStatus.Normal
        }

        // Se o valor mais recente for > 10, independente do histórico, também é crítico?
        // Pela sua lógica estrita de variação, mantemos a variação, mas como segurança,
        // valores > 10 geralmente merecem atenção especial.
        // Vamos manter a lógica de variação pura por enquanto para os casos com histórico,
        // a menos que você queira que > 10 SEMPRE dispare esse alerta.
        // Por enquanto, apliquei apenas na entrada inicial ou lógica de variação abaixo.

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

    // --- NOVAS FUNÇÕES DE BACKUP ---

    // Pega os dados para salvar
    suspend fun getResultsForBackup(): List<PsaResult> {
        return withContext(Dispatchers.IO) {
            psaDao.getAllResultsList()
        }
    }

    // Restaura os dados (Apaga o atual e substitui, ou mescla)
    // Aqui vamos substituir para evitar duplicatas complexas
    fun restoreBackup(results: List<PsaResult>) {
        viewModelScope.launch {
            // Opção segura: Limpar banco atual antes de restaurar
            // Ou você pode fazer um "merge" inteligente, mas replace é mais simples para backup total
            psaDao.deleteAll()
            psaDao.insertAll(results)
        }
    }
}