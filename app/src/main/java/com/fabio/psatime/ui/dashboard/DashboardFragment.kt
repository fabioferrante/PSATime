package com.fabio.psatime.ui.dashboard

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
        // Passamos as funções de callback para o Adapter
        historyAdapter = PsaHistoryAdapter(
            onEditClick = { result ->
                // Abre o BottomSheet em modo de edição
                val bottomSheet = AddResultBottomSheet.newInstance(result.id, result.year, result.value)
                bottomSheet.show(parentFragmentManager, AddResultBottomSheet.TAG)
            },
            onDeleteClick = { result ->
                // Mostra confirmação antes de excluir
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
        AlertDialog.Builder(requireContext())
            .setTitle("Excluir Resultado")
            .setMessage("Tem certeza que deseja excluir o registro de ${result.year}?")
            .setPositiveButton("Excluir") { _, _ ->
                viewModel.deleteResult(result)
                Toast.makeText(requireContext(), "Registro excluído", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun setupObservers() {
        viewModel.allResults.observe(viewLifecycleOwner) { results ->
            historyAdapter.submitList(results)
        }

        viewModel.currentPsaStatus.observe(viewLifecycleOwner) { status ->
            updateStatusCard(status)
        }
    }

    // ... (função updateStatusCard permanece igual, não precisa alterar) ...
    private fun updateStatusCard(status: PsaStatus) {
        val context = requireContext()
        when (status) {
            is PsaStatus.Empty -> {
                binding.cardStatus.setCardBackgroundColor(ContextCompat.getColor(context, R.color.status_neutral_bg))
                binding.imgStatusIcon.background.setTint(ContextCompat.getColor(context, R.color.status_neutral))
                binding.imgStatusIcon.setImageResource(R.drawable.ic_warning)
                binding.tvStatusTitle.setTextColor(ContextCompat.getColor(context, R.color.status_neutral))
                binding.tvStatusTitle.setText(R.string.status_empty_title)
                binding.tvStatusMessage.setTextColor(ContextCompat.getColor(context, R.color.status_neutral))
                binding.tvStatusMessage.setText(R.string.status_empty_message)
            }
            is PsaStatus.Normal -> {
                binding.cardStatus.setCardBackgroundColor(ContextCompat.getColor(context, R.color.status_green_bg))
                binding.imgStatusIcon.background.setTint(ContextCompat.getColor(context, R.color.status_green))
                binding.imgStatusIcon.setImageResource(R.drawable.ic_check_circle)
                binding.tvStatusTitle.setTextColor(ContextCompat.getColor(context, R.color.status_green))
                binding.tvStatusTitle.setText(R.string.status_normal_title)
                binding.tvStatusMessage.setTextColor(ContextCompat.getColor(context, R.color.status_green))
                binding.tvStatusMessage.setText(R.string.status_normal_message)
            }
            is PsaStatus.Warning -> {
                binding.cardStatus.setCardBackgroundColor(ContextCompat.getColor(context, R.color.status_yellow_bg))
                binding.imgStatusIcon.background.setTint(ContextCompat.getColor(context, R.color.status_yellow))
                binding.imgStatusIcon.setImageResource(R.drawable.ic_warning)
                binding.tvStatusTitle.setTextColor(ContextCompat.getColor(context, R.color.status_yellow))
                binding.tvStatusTitle.setText(R.string.status_warning_title)
                binding.tvStatusMessage.setTextColor(ContextCompat.getColor(context, R.color.status_yellow))
                binding.tvStatusMessage.setText(R.string.status_warning_message)
            }
            is PsaStatus.Danger -> {
                binding.cardStatus.setCardBackgroundColor(ContextCompat.getColor(context, R.color.status_red_bg))
                binding.imgStatusIcon.background.setTint(ContextCompat.getColor(context, R.color.status_red))
                binding.imgStatusIcon.setImageResource(R.drawable.ic_error)
                binding.tvStatusTitle.setTextColor(ContextCompat.getColor(context, R.color.status_red))
                binding.tvStatusTitle.setText(R.string.status_danger_title)
                binding.tvStatusMessage.setTextColor(ContextCompat.getColor(context, R.color.status_red))
                binding.tvStatusMessage.setText(R.string.status_danger_message)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}