package com.fabio.psatime.ui.dashboard

import android.content.res.ColorStateList
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
import android.graphics.Typeface
import android.text.SpannableString
import android.text.Spanned
import android.text.style.StyleSpan
import android.text.style.RelativeSizeSpan

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
        val previous = if (position < itemCount - 1) getItem(position + 1) else null
        val anchor = if (position < itemCount - 2) getItem(position + 2) else null

        holder.bind(current, previous, anchor)
    }

    inner class PsaViewHolder(private val binding: ItemPsaResultBinding) : RecyclerView.ViewHolder(binding.root) {
        private val fullDateFormat = SimpleDateFormat("dd 'de' MMM, yyyy", Locale.forLanguageTag("pt-BR"))

        fun bind(result: PsaResult, previous: PsaResult?, anchor: PsaResult?) {
            val context = binding.root.context

            // --- 1. Datas e Textos ---
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = result.timestamp
            val timestampYear = calendar.get(Calendar.YEAR)
            val dateDisplay = fullDateFormat.format(Date(result.timestamp))

            val finalDateText = if (timestampYear != result.year) {
                "${result.year} | $dateDisplay"
            } else {
                dateDisplay
            }

            // --- 2. Formatação do Valor ---
            val valueString = "${result.value} ng/mL"
            val spannable = SpannableString(valueString)
            val unitStart = valueString.indexOf(" ng/mL")

            if (unitStart != -1) {
                spannable.setSpan(StyleSpan(Typeface.BOLD), 0, unitStart, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                spannable.setSpan(RelativeSizeSpan(1.11f), 0, unitStart, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                spannable.setSpan(RelativeSizeSpan(0.83f), unitStart, valueString.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                spannable.setSpan(StyleSpan(Typeface.NORMAL), unitStart, valueString.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            }

            binding.tvResultValue.text = spannable
            binding.tvResultDate.text = finalDateText

            // --- 3. Lógica de Cores ---
            var badgeColorRes = R.color.status_green // Padrão
            var diffText = ""
            var showBadge = false

            if (previous != null) {
                val diff = result.value - previous.value
                val percentage = if (previous.value != 0f) (diff / previous.value) * 100 else 0f
                val diffString = String.format(Locale.US, "%.1f", diff)
                val percString = String.format(Locale.US, "%.0f", abs(percentage))

                val timeDiffMs = result.timestamp - previous.timestamp
                val timeDiffDays = timeDiffMs / (1000 * 60 * 60 * 24)
                val isShortPeriod = timeDiffDays < 120

                var isThreePointRed = false
                if (anchor != null) {
                    val prevDiff = previous.value - anchor.value
                    if (prevDiff >= 0.4f) {
                        val diffVsAnchor = result.value - anchor.value
                        if (isShortPeriod && diff >= 0 && diffVsAnchor >= 0.4f) {
                            isThreePointRed = true
                        }
                    }
                }

                when {
                    isThreePointRed -> badgeColorRes = R.color.status_red
                    diff >= 0.4f -> badgeColorRes = R.color.status_yellow
                    else -> badgeColorRes = R.color.status_green
                }

                val arrow = if (diff < 0) "↓" else "↑"
                val sign = if (diff > 0) "+" else ""
                diffText = "$arrow $sign$diffString | $percString%"
                showBadge = true
            } else {
                // CORREÇÃO AQUI: Sem histórico anterior (Item Único ou o último da lista)
                // Se for > 10, pinta o frasco de vermelho!
                if (result.value > 10f) {
                    badgeColorRes = R.color.status_red
                } else {
                    badgeColorRes = R.color.status_green
                }
                showBadge = false
            }

            // --- 4. Aplicação Visual ---
            val colorStateList = ContextCompat.getColorStateList(context, badgeColorRes)
            val whiteColorStateList = ContextCompat.getColorStateList(context, R.color.white)

            binding.imgFlask.backgroundTintList = colorStateList
            binding.imgFlask.imageTintList = whiteColorStateList

            if (showBadge) {
                binding.tvResultBadge.text = diffText
                binding.tvResultBadge.visibility = View.VISIBLE

                if (badgeColorRes == R.color.status_green) {
                    binding.tvResultBadge.setBackgroundResource(R.drawable.bg_history_badge_green)
                    binding.tvResultBadge.setTextColor(ContextCompat.getColor(context, R.color.status_green))
                } else {
                    binding.tvResultBadge.setBackgroundResource(R.drawable.bg_history_badge_red)
                    val textColor = if (badgeColorRes == R.color.status_yellow) R.color.status_yellow else R.color.status_red
                    binding.tvResultBadge.setTextColor(ContextCompat.getColor(context, textColor))
                }
            } else {
                binding.tvResultBadge.visibility = View.GONE
            }

            binding.btnEditItem.setOnClickListener { onEditClick(result) }
            binding.btnDeleteItem.setOnClickListener { onDeleteClick(result) }
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