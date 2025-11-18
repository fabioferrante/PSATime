package com.fabio.psatime.ui.dashboard

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

    // Usamos 'activity as ...' para compartilhar o ViewModel com o BottomSheet
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

        // Configura os componentes da tela
        setupRecyclerView()
        setupClickListeners()
        setupObservers()
    }

    /**
     * Configura todos os listeners de clique da tela
     */
    private fun setupClickListeners() {
        // Botão flutuante para adicionar novo resultado
        binding.fabAddResult.setOnClickListener {
            AddResultBottomSheet().show(parentFragmentManager, AddResultBottomSheet.TAG)
        }

        // Botão de Menu (Hamburger) - (ícone de navegação)
        binding.toolbarDashboard.setNavigationOnClickListener {
            // Por enquanto, mostra um Toast.
            // No futuro, aqui abriria um Navigation Drawer (menu lateral).
            Toast.makeText(requireContext(), "Botão de Menu Clicado", Toast.LENGTH_SHORT).show()
        }

        // Botão de Configurações (Ícone de engrenagem) - (menu de itens)
        binding.toolbarDashboard.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_settings -> {
                    // Navega para o SettingsFragment
                    findNavController().navigate(R.id.action_dashboardFragment_to_settingsFragment)
                    true
                }
                else -> false
            }
        }
    }

    /**
     * Configura o RecyclerView (a lista de histórico)
     */
    private fun setupRecyclerView() {
        historyAdapter = PsaHistoryAdapter()
        binding.recyclerViewHistory.apply {
            adapter = historyAdapter
            layoutManager = LinearLayoutManager(requireContext())
            // Desativa a animação "change" (o "pisca") para uma UI mais limpa
            (itemAnimator as? SimpleItemAnimator)?.supportsChangeAnimations = false
        }
    }

    /**
     * Observa as mudanças no ViewModel (Lista de resultados e Status)
     */
    private fun setupObservers() {
        // Observa a lista de resultados e atualiza o adapter
        viewModel.allResults.observe(viewLifecycleOwner) { results ->
            historyAdapter.submitList(results)
        }

        // Observa o status calculado e atualiza o card principal
        viewModel.currentPsaStatus.observe(viewLifecycleOwner) { status ->
            updateStatusCard(status)
        }
    }

    /**
     * Atualiza o Card de Status (cores, textos, ícones) com base no estado
     */
    private fun updateStatusCard(status: PsaStatus) {
        val context = requireContext()
        when (status) {
            is PsaStatus.Empty -> {
                binding.cardStatus.setCardBackgroundColor(ContextCompat.getColor(context, R.color.status_neutral_bg))
                binding.imgStatusIcon.background.setTint(ContextCompat.getColor(context, R.color.status_neutral))
                binding.imgStatusIcon.setImageResource(R.drawable.ic_warning) // Pode trocar por ic_info
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
        // Limpa a referência ao binding para evitar memory leaks
        _binding = null
    }
}