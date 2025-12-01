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
import androidx.core.os.LocaleListCompat
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

        // --- Configuração de Tema ---
        loadThemePreference()
        binding.radioGroupTheme.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radio_theme_system -> saveAndApplyTheme(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM, "system")
                R.id.radio_theme_light -> saveAndApplyTheme(AppCompatDelegate.MODE_NIGHT_NO, "light")
                R.id.radio_theme_dark -> saveAndApplyTheme(AppCompatDelegate.MODE_NIGHT_YES, "dark")
            }
        }

        // --- Configuração de Idioma ---
        loadLanguagePreference()
        binding.radioGroupLanguage.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radio_lang_en -> changeLanguage("en-US")
                R.id.radio_lang_pt -> changeLanguage("pt-BR")
            }
        }

        // --- Cliques de Backup ---
        binding.btnBackupExport.setOnClickListener {
            val date = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())
            val filename = "psa_backup_$date.json"
            exportLauncher.launch(filename)
        }

        binding.btnBackupImport.setOnClickListener {
            importLauncher.launch(arrayOf("application/json"))
        }

        // --- Cliques de Apoio/Doação ---
        binding.ivQrCodeBtc.setOnClickListener { copyBtcAndOpenWallet() }
        binding.tvEnderecoBtc.setOnClickListener { copyBtcAndOpenWallet() }
        binding.layoutTwitter.setOnClickListener { openUrl("https://x.com/fabioferrante") }
    }

    // --- Lógica de Idioma ---
    private fun loadLanguagePreference() {
        val currentLocales = AppCompatDelegate.getApplicationLocales()
        if (!currentLocales.isEmpty) {
            val currentTag = currentLocales.toLanguageTags()
            if (currentTag.contains("pt")) {
                binding.radioLangPt.isChecked = true
            } else {
                binding.radioLangEn.isChecked = true
            }
        } else {
            val deviceLocale = Locale.getDefault().language
            if (deviceLocale == "pt") {
                binding.radioLangPt.isChecked = true
            } else {
                binding.radioLangEn.isChecked = true
            }
        }
    }

    private fun changeLanguage(languageTag: String) {
        val appLocale = LocaleListCompat.forLanguageTags(languageTag)
        AppCompatDelegate.setApplicationLocales(appLocale)
    }

    // Função auxiliar para Copiar e Abrir Carteira
    private fun copyBtcAndOpenWallet() {
        val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Endereço Bitcoin", BTC_ADDRESS)
        clipboard.setPrimaryClip(clip)

        Toast.makeText(requireContext(), getString(R.string.toast_address_copied), Toast.LENGTH_SHORT).show()

        try {
            // CORREÇÃO: Constrói a URI dinamicamente usando a string traduzida
            val message = getString(R.string.btc_support_message)
            val btcUri = "bitcoin:$BTC_ADDRESS?message=$message"

            // Encode spaces in the URI just in case, though Uri.parse often handles it.
            // Replacing spaces with %20 is safer for intents.
            val encodedUri = btcUri.replace(" ", "%20")
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(encodedUri))
            startActivity(intent)
        } catch (e: Exception) {
            // Ignora se não tiver wallet
        }
    }

    private fun openUrl(url: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        } catch (e: Exception) { }
    }

    // --- LÓGICA DE EXPORTAÇÃO ---
    private fun exportDataToUri(uri: Uri) {
        lifecycleScope.launch {
            try {
                val results = viewModel.getResultsForBackup()
                if (results.isEmpty()) {
                    return@launch
                }
                val jsonArray = JSONArray()
                results.forEach { result ->
                    val jsonObj = JSONObject()
                    jsonObj.put("year", result.year)
                    jsonObj.put("value", result.value.toDouble())
                    jsonObj.put("timestamp", result.timestamp)
                    jsonArray.put(jsonObj)
                }
                requireContext().contentResolver.openOutputStream(uri)?.use { outputStream ->
                    outputStream.write(jsonArray.toString().toByteArray())
                }
                Toast.makeText(requireContext(), getString(R.string.toast_backup_success), Toast.LENGTH_LONG).show()
            } catch (e: Exception) { }
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
                    Toast.makeText(requireContext(), getString(R.string.toast_restore_success), Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) { }
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