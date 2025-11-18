package com.fabio.psatime

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.fabio.psatime.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        // 1. APLICA O TEMA SALVO (DEVE VIR ANTES DE 'super.onCreate')
        applySavedTheme()

        // Restante do código padrão
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // O NavHostFragment cuida do resto
    }

    /**
     * Lê a preferência de tema salva e a aplica
     */
    private fun applySavedTheme() {
        // Usa os mesmos nomes de arquivo e chave do SettingsFragment
        val prefs = getSharedPreferences("psa_theme_prefs", Context.MODE_PRIVATE)
        val savedTheme = prefs.getString("theme_preference", "system")

        // Converte a string salva ("light", "dark") no Modo do AppCompat
        val mode = when (savedTheme) {
            "light" -> AppCompatDelegate.MODE_NIGHT_NO
            "dark" -> AppCompatDelegate.MODE_NIGHT_YES
            else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM // Padrão
        }

        // Aplica o tema
        AppCompatDelegate.setDefaultNightMode(mode)
    }
}