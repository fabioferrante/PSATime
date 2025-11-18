package com.fabio.psatime.ui.addedit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.viewModels
import com.fabio.psatime.databinding.BottomSheetAddResultBinding
import com.fabio.psatime.ui.dashboard.PsaViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.util.Calendar


class AddResultBottomSheet : BottomSheetDialogFragment() {

    companion object {
        const val TAG = "AddResultBottomSheet"
    }

    private var _binding: BottomSheetAddResultBinding? = null
    private val binding get() = _binding!!

    // Compartilha o ViewModel com o DashboardFragment
    private val viewModel: PsaViewModel by viewModels({ activity as FragmentActivity })

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetAddResultBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Preenche o ano atual como padrão
        binding.inputEditTextYear.setText(Calendar.getInstance().get(Calendar.YEAR).toString())

        binding.btnCancel.setOnClickListener {
            dismiss()
        }

        binding.btnSave.setOnClickListener {
            saveResult()
        }
    }

    private fun saveResult() {
        val yearStr = binding.inputEditTextYear.text.toString()
        val valueStr = binding.inputEditTextValue.text.toString().replace(',', '.')

        if (yearStr.isEmpty() || valueStr.isEmpty()) {
            Toast.makeText(requireContext(), "Preencha todos os campos", Toast.LENGTH_SHORT).show()
            return
        }

        val year = yearStr.toIntOrNull()
        val value = valueStr.toFloatOrNull()

        if (year == null || value == null) {
            Toast.makeText(requireContext(), "Valores inválidos", Toast.LENGTH_SHORT).show()
            return
        }

        if (value == null || value < 0f || value > 7f) {
            Toast.makeText(requireContext(), "Valor inválido. Insira um valor de PSA entre 0 e 7.", Toast.LENGTH_SHORT).show()
            return
        }

        viewModel.insertResult(year, value)
        dismiss()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}