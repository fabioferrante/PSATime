package com.fabio.psatime.ui.dashboard

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.fabio.psatime.R
import com.fabio.psatime.data.PsaResult
import com.fabio.psatime.databinding.ItemPsaResultBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.abs

// Adicionamos callbacks no construtor
class PsaHistoryAdapter(
    private val onEditClick: (PsaResult) -> Unit,
    private val onDeleteClick: (PsaResult) -> Unit
) : ListAdapter<PsaResult, PsaHistoryAdapter.PsaViewHolder>(PsaDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PsaViewHolder {
        val binding = ItemPsaResultBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PsaViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PsaViewHolder, position: Int) {
        val current = getItem(position)
        var previous: PsaResult? = null
        if (position < itemCount - 1) {
            previous = getItem(position + 1)
        }
        holder.bind(current, previous)
    }

    inner class PsaViewHolder(private val binding: ItemPsaResultBinding) : RecyclerView.ViewHolder(binding.root) {
        private val dateFormat = SimpleDateFormat("dd 'de' MMM, yyyy", Locale.forLanguageTag("pt-BR"))

        fun bind(result: PsaResult, previous: PsaResult?) {

            // 1. Lógica para formatar o texto da Data (com ou sem ano entre parênteses)
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = result.timestamp
            val timestampYear = calendar.get(Calendar.YEAR)

            val dateDisplay = dateFormat.format(Date(result.timestamp))

            val finalDateText = if (timestampYear != result.year) {
                // Se os anos forem diferentes, exibe o ano digitado entre parênteses
                "$dateDisplay (${result.year})"
            } else {
                // Se os anos forem iguais, exibe apenas a data
                dateDisplay
            }

            // 3. TROCA DE ATRIBUIÇÃO:

            // VALOR (agora é a linha de cima, maior e negrito)
            binding.tvResultValue.text = "${result.value} ng/mL"

            // DATA (agora é a linha de baixo, menor e secundária)
            binding.tvResultDate.text = finalDateText

            // Configura os cliques de Edição e Exclusão
            binding.btnEditItem.setOnClickListener { onEditClick(result) }
            binding.btnDeleteItem.setOnClickListener { onDeleteClick(result) }

            if (previous != null) {
                val diff = result.value - previous.value
                val percentage = if (previous.value != 0f) (diff / previous.value) * 100 else 0f
                val diffString = String.format(Locale.US, "%.1f", diff)
                val percString = String.format(Locale.US, "%.0f", abs(percentage))

                if (diff < 0) {
                    binding.tvResultBadge.text = "↓ $diffString | $percString%"
                    binding.tvResultBadge.setBackgroundResource(R.drawable.bg_history_badge_green)
                    binding.tvResultBadge.setTextColor(ContextCompat.getColor(binding.root.context, R.color.status_green))
                } else {
                    binding.tvResultBadge.text = "↑ +$diffString | $percString%"
                    binding.tvResultBadge.setBackgroundResource(R.drawable.bg_history_badge_red)
                    binding.tvResultBadge.setTextColor(ContextCompat.getColor(binding.root.context, R.color.status_red))
                }
                binding.tvResultBadge.visibility = View.VISIBLE
            } else {
                binding.tvResultBadge.visibility = View.GONE
            }
        }
    }

    class PsaDiffCallback : DiffUtil.ItemCallback<PsaResult>() {
        override fun areItemsTheSame(oldItem: PsaResult, newItem: PsaResult): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: PsaResult, newItem: PsaResult): Boolean {
            return oldItem == newItem
        }
    }
}