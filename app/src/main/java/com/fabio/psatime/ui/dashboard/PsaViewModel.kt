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

// Define os 4 estados de status
sealed class PsaStatus {
    object Empty : PsaStatus() // <-- NOVO ESTADO
    object Normal : PsaStatus()
    object Warning : PsaStatus()
    object Danger : PsaStatus()
}

class PsaViewModel(application: Application) : AndroidViewModel(application) {

    private val psaDao = PsaDatabase.getDatabase(application).psaDao()

    val allResults: LiveData<List<PsaResult>> = psaDao.getAllResults().asLiveData()

    // Este LiveData calcula o status toda vez que a lista de resultados muda
    val currentPsaStatus: LiveData<PsaStatus> = allResults.map { results ->
        calculateStatus(results)
    }

    private fun calculateStatus(results: List<PsaResult>): PsaStatus {
        if (results.isEmpty()) { // <-- MUDANÇA AQUI
            return PsaStatus.Empty // Se a lista estiver vazia, retorne o estado Empty
        }

        if (results.size < 2) {
            return PsaStatus.Normal // Menos de 2 resultados, tudo normal
        }

        // 'results' já está ordenado por 'timestamp DESC' (do mais novo para o mais velho)
        val latest = results[0]     // V3 (Ex: 0.91, 0.60, ou 0.4)
        val previous = results[1]   // V2 (Ex: 0.8)

        // --- Lógica de Acompanhamento Anual (Apenas 2 resultados na base) ---
        if (results.size == 2) {
            val diff = latest.value - previous.value
            return if (diff >= 0.4f) PsaStatus.Warning else PsaStatus.Normal
        }

        // --- Lógica de 3+ resultados (com V1, V2, V3) ---
        val anchor = results[2] // V1 (Ex: 0.1)

        // V2 foi um Alerta Amarelo? (Comparado com V1)
        val previousWasYellow = (previous.value - anchor.value) >= 0.4f

        // V3 é um follow-up de 3 meses? (Comparado com V2)
        val timeDiffMs = latest.timestamp - previous.timestamp
        val timeDiffDays = timeDiffMs / (1000 * 60 * 60 * 24)
        // Usamos 120 dias (aprox 4 meses) para dar uma margem
        val isFollowUp = timeDiffDays < 120

        // Se V3 é um follow-up E V2 foi um alerta, aplicamos a lógica dos 3 Casos
        if (isFollowUp && previousWasYellow) {
            val diffVsAnchor = latest.value - anchor.value // Compara V3 vs V1
            val diffVsPrevious = latest.value - previous.value // Compara V3 vs V2

            // Caso 1 (Vermelho): Subiu/manteve (vs V2) E continua alto (vs V1)
            if (diffVsAnchor >= 0.4f && diffVsPrevious >= 0f) {
                return PsaStatus.Danger // Seu Caso 1 (0.91)
            }

            // Caso 2 (Amarelo): Baixou (vs V2) MAS continua alto (vs V1)
            if (diffVsAnchor >= 0.4f) {
                return PsaStatus.Warning // Seu Caso 2 (0.60)
            }

            // Caso 3 (Verde): Baixou (vs V2) E normalizou (vs V1)
            // (diffVsAnchor < 0.4f)
            return PsaStatus.Normal // Seu Caso 3 (0.4)
        }

        // --- Lógica Padrão (Anual) ---
        // Se não for um follow-up (ou se V2 era normal),
        // apenas compare V3 com V2 (o último anual)
        val diffAnnual = latest.value - previous.value
        return if (diffAnnual >= 0.4f) PsaStatus.Warning else PsaStatus.Normal
    }

    fun insertResult(year: Int, value: Float) {
        viewModelScope.launch {
            val result = PsaResult(year = year, value = value)
            psaDao.insertResult(result)
        }
    }

    // Você pode adicionar delete/update aqui
}