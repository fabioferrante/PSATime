package com.fabio.psatime

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.fabio.psatime.databinding.ActivityTermsBinding

class TermsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTermsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        // Aplica o tema correto antes de carregar a tela
        applySavedTheme()

        super.onCreate(savedInstanceState)
        binding = ActivityTermsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnAcceptTerms.setOnClickListener {
            acceptTermsAndProceed()
        }
    }

    private fun acceptTermsAndProceed() {
        // Salva que o usuário aceitou
        val prefs = getSharedPreferences("psa_prefs", MODE_PRIVATE)
        prefs.edit().putBoolean("terms_accepted", true).apply()

        // Vai para a Dashboard
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish() // Fecha a tela de termos para não voltar nela com o botão "Voltar"
    }

    private fun applySavedTheme() {
        val prefs = getSharedPreferences("psa_theme_prefs", MODE_PRIVATE)
        val savedTheme = prefs.getString("theme_preference", "system")

        val mode = when (savedTheme) {
            "light" -> AppCompatDelegate.MODE_NIGHT_NO
            "dark" -> AppCompatDelegate.MODE_NIGHT_YES
            else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        }
        AppCompatDelegate.setDefaultNightMode(mode)
    }
}