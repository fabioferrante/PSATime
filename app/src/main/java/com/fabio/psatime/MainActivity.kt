package com.fabio.psatime

import android.Manifest
import android.content.Context
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
        applySavedTheme()

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupNotifications()
        setupFollowUpReminder()
    }

    private fun setupFollowUpReminder() {
        viewModel.allResults.observe(this) { results ->
            if (results.isNotEmpty()) {
                val latest = results[0]

                // 1. Agenda sempre o lembrete anual (regra geral)
                NotificationScheduler.scheduleFollowUpNotification(this, latest.timestamp)

                // 2. Lógica para o Alerta de 3 Meses (Amarelo)
                var isYellowCondition = false
                if (results.size >= 2) {
                    val previous = results[1]
                    val diff = latest.value - previous.value
                    // Se a diferença foi >= 0.4, consideramos um alerta que exige reteste
                    if (diff >= 0.4f) {
                        isYellowCondition = true
                    }
                }

                if (isYellowCondition) {
                    // Se estamos em alerta amarelo, agenda o aviso de 3 meses
                    NotificationScheduler.scheduleYellowAlertFollowUp(this, latest.timestamp)
                } else {
                    // Se está tudo normal (ou já virou vermelho confirmado/verde),
                    // cancela qualquer aviso de 3 meses pendente para não incomodar o usuário.
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
        val prefs = getSharedPreferences("psa_theme_prefs", Context.MODE_PRIVATE)
        val savedTheme = prefs.getString("theme_preference", "system")

        val mode = when (savedTheme) {
            "light" -> AppCompatDelegate.MODE_NIGHT_NO
            "dark" -> AppCompatDelegate.MODE_NIGHT_YES
            else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        }
        AppCompatDelegate.setDefaultNightMode(mode)
    }
}