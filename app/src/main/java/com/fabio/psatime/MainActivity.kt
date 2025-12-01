package com.fabio.psatime

import android.Manifest
import android.content.Context
import android.content.Intent // Importante
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.fabio.psatime.databinding.ActivityMainBinding
import com.fabio.psatime.receiver.NotificationScheduler
import com.fabio.psatime.ui.dashboard.PsaViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: PsaViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        // 1. Aplica o Tema (Sempre primeiro)
        applySavedTheme()

        super.onCreate(savedInstanceState)

        // 2. CHECK DE TERMOS DE USO (O Porteiro)
        if (!isTermsAccepted()) {
            // Se não aceitou, manda para a tela de Termos e encerra esta
            val intent = Intent(this, TermsActivity::class.java)
            startActivity(intent)
            finish()
            return // Para a execução do onCreate aqui
        }

        // Se chegou aqui, é porque já aceitou. Carrega o App normal.
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupNotifications()
        setupFollowUpReminder()
    }

    // Função auxiliar para checar se aceitou
    private fun isTermsAccepted(): Boolean {
        val prefs = getSharedPreferences("psa_prefs", MODE_PRIVATE)
        return prefs.getBoolean("terms_accepted", false)
    }

    private fun setupFollowUpReminder() {
        viewModel.allResults.observe(this) { results ->
            if (results.isNotEmpty()) {
                val latest = results[0]
                NotificationScheduler.scheduleFollowUpNotification(this, latest.timestamp)

                var isYellowCondition = false
                if (results.size >= 2) {
                    val previous = results[1]
                    val diff = latest.value - previous.value
                    if (diff >= 0.4f) isYellowCondition = true
                }

                if (isYellowCondition) {
                    NotificationScheduler.scheduleYellowAlertFollowUp(this, latest.timestamp)
                } else {
                    NotificationScheduler.cancelYellowAlertFollowUp(this)
                }
            }
        }
    }

    private fun setupNotifications() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 101)
            } else {
                NotificationScheduler.scheduleAnnualNotification(this)
            }
        } else {
            NotificationScheduler.scheduleAnnualNotification(this)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 101) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                NotificationScheduler.scheduleAnnualNotification(this)
            }
        }
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