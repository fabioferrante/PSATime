package com.fabio.psatime.ui.settings

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.fabio.psatime.R
import com.fabio.psatime.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    // Nome do arquivo onde vamos salvar a escolha
    private val PREFS_NAME = "psa_theme_prefs"
    // Chave para salvar a escolha (ex: "light", "dark", "system")
    private val PREF_KEY_THEME = "theme_preference"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Faz a seta "Voltar" no toolbar funcionar
        binding.toolbarSettings.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        // 2. Carrega a preferência salva para marcar o botão certo
        loadThemePreference()

        // 3. Adiciona o listener para o RadioGroup
        binding.radioGroupTheme.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radio_theme_system -> saveAndApplyTheme(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM, "system")
                R.id.radio_theme_light -> saveAndApplyTheme(AppCompatDelegate.MODE_NIGHT_NO, "light")
                R.id.radio_theme_dark -> saveAndApplyTheme(AppCompatDelegate.MODE_NIGHT_YES, "dark")
            }
        }
    }

    /**
     * Lê a preferência salva no SharedPreferences e marca o RadioButton correto
     */
    private fun loadThemePreference() {
        val prefs = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val savedTheme = prefs.getString(PREF_KEY_THEME, "system") // Padrão é "system"

        when (savedTheme) {
            "light" -> binding.radioThemeLight.isChecked = true
            "dark" -> binding.radioThemeDark.isChecked = true
            else -> binding.radioThemeSystem.isChecked = true // "system" ou qualquer outro valor
        }
    }

    /**
     * Aplica o tema imediatamente e Salva a escolha no SharedPreferences
     */
    private fun saveAndApplyTheme(themeMode: Int, themeName: String) {
        // 1. Aplica o tema imediatamente
        AppCompatDelegate.setDefaultNightMode(themeMode)

        // 2. Salva a preferência
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