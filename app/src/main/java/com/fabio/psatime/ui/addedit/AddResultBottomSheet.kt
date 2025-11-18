package com.fabio.psatime.ui.addedit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.fabio.psatime.data.PsaResult
import com.fabio.psatime.databinding.BottomSheetAddResultBinding
import com.fabio.psatime.ui.dashboard.PsaViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.util.Calendar

class AddResultBottomSheet : BottomSheetDialogFragment() {

    companion object {
        const val TAG = "AddResultBottomSheet"

        // Método estático para criar instância com argumentos (edição)
        fun newInstance(resultId: Int, year: Int, value: Float): AddResultBottomSheet {
            val fragment = AddResultBottomSheet()
            val args = Bundle()
            args.putInt("id", resultId)
            args.putInt("year", year)
            args.putFloat("value", value)
            fragment.arguments = args
            return fragment
        }
    }

    private var _binding: BottomSheetAddResultBinding? = null
    private val binding get() = _binding!!
    private var resultId: Int? = null // Se null, é novo. Se tem valor, é edição.

    private val viewModel: PsaViewModel by viewModels({ activity as androidx.fragment.app.FragmentActivity })

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetAddResultBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Verifica se estamos editando
        arguments?.let {
            if (it.containsKey("id")) {
                resultId = it.getInt("id")
                binding.inputEditTextYear.setText(it.getInt("year").toString())
                binding.inputEditTextValue.setText(it.getFloat("value").toString())
                binding.tvBottomSheetTitle.text = "Editar Resultado"
            }
        }

        // Se não for edição, preenche ano atual
        if (resultId == null) {
            binding.inputEditTextYear.setText(Calendar.getInstance().get(Calendar.YEAR).toString())
        }

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
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)

        if (year == null || year < 2000 || year > (currentYear + 1)) {
            Toast.makeText(requireContext(), "Por favor, insira um ano válido.", Toast.LENGTH_SHORT).show()
            return
        }

        if (value == null || value < 0f || value > 7f) {
            Toast.makeText(requireContext(), "Valor inválido. Insira um valor de PSA entre 0 e 7.", Toast.LENGTH_SHORT).show()
            return
        }

        if (resultId != null) {
            // Atualizar existente
            val updatedResult = PsaResult(id = resultId!!, year = year, value = value)
            viewModel.updateResult(updatedResult)
        } else {
            // Inserir novo
            viewModel.insertResult(year, value)
        }

        dismiss()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}