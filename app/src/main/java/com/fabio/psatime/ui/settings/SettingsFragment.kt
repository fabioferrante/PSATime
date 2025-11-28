package com.fabio.psatime.ui.settings

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.fabio.psatime.R
import com.fabio.psatime.data.PsaResult
import com.fabio.psatime.databinding.FragmentSettingsBinding
import com.fabio.psatime.ui.dashboard.PsaViewModel
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    // ViewModel para acessar os dados
    private val viewModel: PsaViewModel by viewModels({ activity as androidx.fragment.app.FragmentActivity })

    private val PREFS_NAME = "psa_theme_prefs"
    private val PREF_KEY_THEME = "theme_preference"

    // Dados de Doação
    private val BTC_ADDRESS = "BC1QCGGEYAWVVSG5N8UYUPXU93HAPS8NH9Q79SPY0V"
    // URI corrigida: bitcoin:<address>?amount=0.01&message=<address>
    private val BTC_URI = "bitcoin:$BTC_ADDRESS?amount=0.01&message=$BTC_ADDRESS"

    // --- SELETORES DE ARQUIVO ---

    // Exportar (Criar arquivo)
    private val exportLauncher = registerForActivityResult(ActivityResultContracts.CreateDocument("application/json")) { uri ->
        if (uri != null) {
            exportDataToUri(uri)
        }
    }

    // Importar (Abrir arquivo)
    private val importLauncher = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) {
            importDataFromUri(uri)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbarSettings.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        // Configuração de Tema
        loadThemePreference()
        binding.radioGroupTheme.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radio_theme_system -> saveAndApplyTheme(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM, "system")
                R.id.radio_theme_light -> saveAndApplyTheme(AppCompatDelegate.MODE_NIGHT_NO, "light")
                R.id.radio_theme_dark -> saveAndApplyTheme(AppCompatDelegate.MODE_NIGHT_YES, "dark")
            }
        }

        // --- CLIQUES DE BACKUP ---

        binding.btnBackupExport.setOnClickListener {
            val date = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())
            val filename = "psa_backup_$date.json"
            exportLauncher.launch(filename)
        }

        binding.btnBackupImport.setOnClickListener {
            importLauncher.launch(arrayOf("application/json"))
        }

        // --- CLIQUES DE APOIO/DOAÇÃO (NOVO) ---

        // Clique no QR Code
        binding.ivQrCodeBtc.setOnClickListener {
            copyBtcAndOpenWallet()
        }

        // Clique no Endereço de Texto
        binding.tvEnderecoBtc.setOnClickListener {
            copyBtcAndOpenWallet()
        }

        // Clique no Twitter (Extra)
        binding.layoutTwitter.setOnClickListener {
            openUrl("https://twitter.com/fabioferrante")
        }
    }

    // Função auxiliar para Copiar e Abrir Carteira
    private fun copyBtcAndOpenWallet() {
        // 1. Copiar para o Clipboard
        val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Endereço Bitcoin", BTC_ADDRESS)
        clipboard.setPrimaryClip(clip)

        Toast.makeText(requireContext(), "Endereço Bitcoin copiado!", Toast.LENGTH_SHORT).show()

        // 2. Abrir App de Carteira (Intent)
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(BTC_URI))
            startActivity(intent)
        } catch (e: Exception) {
            // Caso o usuário não tenha app de carteira instalado, apenas avisa que copiou
            Toast.makeText(requireContext(), "Nenhum app de carteira encontrado, mas o endereço foi copiado.", Toast.LENGTH_LONG).show()
        }
    }

    // Função auxiliar para abrir URLs
    private fun openUrl(url: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Não foi possível abrir o link.", Toast.LENGTH_SHORT).show()
        }
    }

    // --- LÓGICA DE EXPORTAÇÃO ---
    private fun exportDataToUri(uri: Uri) {
        lifecycleScope.launch {
            try {
                // 1. Pega os dados do ViewModel
                val results = viewModel.getResultsForBackup()

                if (results.isEmpty()) {
                    Toast.makeText(requireContext(), "Sem dados para exportar.", Toast.LENGTH_SHORT).show()
                    return@launch
                }

                // 2. Converte para JSON String
                val jsonArray = JSONArray()
                results.forEach { result ->
                    val jsonObj = JSONObject()
                    jsonObj.put("year", result.year)
                    jsonObj.put("value", result.value.toDouble())
                    jsonObj.put("timestamp", result.timestamp)
                    jsonArray.put(jsonObj)
                }
                val jsonString = jsonArray.toString()

                // 3. Escreve no arquivo escolhido pelo usuário
                requireContext().contentResolver.openOutputStream(uri)?.use { outputStream ->
                    outputStream.write(jsonString.toByteArray())
                }

                Toast.makeText(requireContext(), "Backup salvo com sucesso!", Toast.LENGTH_LONG).show()

            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(requireContext(), "Erro ao exportar: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    // --- LÓGICA DE IMPORTAÇÃO ---
    private fun importDataFromUri(uri: Uri) {
        lifecycleScope.launch {
            try {
                val stringBuilder = StringBuilder()
                requireContext().contentResolver.openInputStream(uri)?.use { inputStream ->
                    BufferedReader(InputStreamReader(inputStream)).use { reader ->
                        var line: String? = reader.readLine()
                        while (line != null) {
                            stringBuilder.append(line)
                            line = reader.readLine()
                        }
                    }
                }

                val jsonArray = JSONArray(stringBuilder.toString())
                val listToRestore = mutableListOf<PsaResult>()

                for (i in 0 until jsonArray.length()) {
                    val obj = jsonArray.getJSONObject(i)
                    if (obj.has("year") && obj.has("value")) {
                        val result = PsaResult(
                            year = obj.getInt("year"),
                            value = obj.getDouble("value").toFloat(),
                            timestamp = obj.optLong("timestamp", System.currentTimeMillis())
                        )
                        listToRestore.add(result)
                    }
                }

                if (listToRestore.isNotEmpty()) {
                    viewModel.restoreBackup(listToRestore)
                    Toast.makeText(requireContext(), "Dados restaurados com sucesso!", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(requireContext(), "Nenhum dado válido encontrado no arquivo.", Toast.LENGTH_LONG).show()
                }

            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(requireContext(), "Erro ao importar: Arquivo inválido.", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun loadThemePreference() {
        val prefs = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val savedTheme = prefs.getString(PREF_KEY_THEME, "system")

        when (savedTheme) {
            "light" -> binding.radioThemeLight.isChecked = true
            "dark" -> binding.radioThemeDark.isChecked = true
            else -> binding.radioThemeSystem.isChecked = true
        }
    }

    private fun saveAndApplyTheme(themeMode: Int, themeName: String) {
        AppCompatDelegate.setDefaultNightMode(themeMode)
        val prefs = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        with(prefs.edit()) {
            putString(PREF_KEY_THEME, themeName)
            apply()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}