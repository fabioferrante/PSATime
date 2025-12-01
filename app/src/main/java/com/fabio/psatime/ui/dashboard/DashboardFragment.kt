package com.fabio.psatime.ui.dashboard

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.fabio.psatime.R
import com.fabio.psatime.data.PsaResult
import com.fabio.psatime.databinding.FragmentDashboardBinding
import com.fabio.psatime.ui.addedit.AddResultBottomSheet

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PsaViewModel by viewModels({ activity as androidx.fragment.app.FragmentActivity })
    private lateinit var historyAdapter: PsaHistoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupClickListeners()
        setupObservers()
    }

    private fun setupClickListeners() {
        binding.fabAddResult.setOnClickListener {
            AddResultBottomSheet().show(parentFragmentManager, AddResultBottomSheet.TAG)
        }

        binding.toolbarDashboard.setNavigationOnClickListener {
            Toast.makeText(requireContext(), "Menu", Toast.LENGTH_SHORT).show()
        }

        binding.toolbarDashboard.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_settings -> {
                    findNavController().navigate(R.id.action_dashboardFragment_to_settingsFragment)
                    true
                }
                else -> false
            }
        }
    }

    private fun setupRecyclerView() {
        historyAdapter = PsaHistoryAdapter(
            onEditClick = { result ->
                val bottomSheet = AddResultBottomSheet.newInstance(result.id, result.year, result.value)
                bottomSheet.show(parentFragmentManager, AddResultBottomSheet.TAG)
            },
            onDeleteClick = { result ->
                showDeleteConfirmationDialog(result)
            }
        )

        binding.recyclerViewHistory.apply {
            adapter = historyAdapter
            layoutManager = LinearLayoutManager(requireContext())
            (itemAnimator as? SimpleItemAnimator)?.supportsChangeAnimations = false
        }
    }

    private fun showDeleteConfirmationDialog(result: PsaResult) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_delete_confirmation, null)

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        dialogView.findViewById<TextView>(R.id.tv_dialog_message).text = "Tem certeza que deseja excluir o registro de ${result.year}?"

        dialogView.findViewById<Button>(R.id.btn_cancel_delete).setOnClickListener {
            dialog.dismiss()
        }

        dialogView.findViewById<Button>(R.id.btn_confirm_delete).setOnClickListener {
            viewModel.deleteResult(result)
            Toast.makeText(requireContext(), "Registro excluído", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }

        dialog.show()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
    }

    private fun setupObservers() {
        viewModel.allResults.observe(viewLifecycleOwner) { results ->
            historyAdapter.submitList(results)
        }

        viewModel.currentPsaStatus.observe(viewLifecycleOwner) { status ->
            updateStatusCard(status)
        }
    }

    private fun updateStatusCard(status: PsaStatus) {
        val context = requireContext()

        // Helper para configurar cores e textos
        fun setCardState(bgColorRes: Int, contentColorRes: Int, iconRes: Int, titleRes: Int, messageRes: Int) {
            binding.cardStatus.setCardBackgroundColor(ContextCompat.getColor(context, bgColorRes))

            // Ícone e Título continuam coloridos
            binding.imgStatusIcon.background.setTint(ContextCompat.getColor(context, contentColorRes))
            binding.imgStatusIcon.setImageResource(iconRes)
            binding.tvStatusTitle.setTextColor(ContextCompat.getColor(context, contentColorRes))
            binding.tvStatusTitle.setText(titleRes)

            // AJUSTE TEMPORÁRIO: Mensagem sempre preta (ou cor do tema onSurface)
            // Em vez de 'contentColorRes', usamos a cor 'black' definida em colors.xml ou Color.BLACK
            binding.tvStatusMessage.setTextColor(ContextCompat.getColor(context, R.color.black))
            binding.tvStatusMessage.setText(messageRes)
        }

        when (status) {
            is PsaStatus.Empty -> {
                setCardState(
                    R.color.status_neutral_bg, R.color.status_neutral, R.drawable.ic_warning,
                    R.string.status_empty_title, R.string.status_empty_message
                )
            }
            is PsaStatus.Normal -> {
                setCardState(
                    R.color.status_green_bg, R.color.status_green, R.drawable.ic_check_circle,
                    R.string.status_normal_title, R.string.status_normal_message
                )
            }
            is PsaStatus.Warning -> {
                setCardState(
                    R.color.status_yellow_bg, R.color.status_yellow, R.drawable.ic_warning,
                    R.string.status_warning_title, R.string.status_warning_message
                )
            }
            is PsaStatus.Danger -> {
                setCardState(
                    R.color.status_red_bg, R.color.status_red, R.drawable.ic_error,
                    R.string.status_danger_title, R.string.status_danger_message
                )
            }
            is PsaStatus.CriticalHigh -> {
                setCardState(
                    R.color.status_red_bg, R.color.status_red, R.drawable.ic_error,
                    R.string.status_critical_high_title, R.string.status_critical_high_message
                )
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}