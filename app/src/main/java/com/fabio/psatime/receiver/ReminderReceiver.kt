package com.fabio.psatime.receiver

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.fabio.psatime.MainActivity
import com.fabio.psatime.R

class ReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "android.intent.action.BOOT_COMPLETED") {
            NotificationScheduler.scheduleAnnualNotification(context)
            return
        }

        val type = intent.getStringExtra(NotificationScheduler.EXTRA_NOTIFICATION_TYPE)

        val title: String
        val message: String

        when (type) {
            NotificationScheduler.TYPE_ONE_YEAR_FOLLOWUP -> {
                title = "Atenção: Já faz 1 Ano!"
                message = "Para um cálculo de delta eficiente, é importante manter seu exame anual em dia. Registre seu novo resultado."
            }
            // NOVO BLOCO: Alerta de 3 Meses
            NotificationScheduler.TYPE_YELLOW_ALERT_FOLLOWUP -> {
                title = "Importante: Confirmação Necessária"
                message = "Houve uma variação no seu último exame. É crucial repetir o exame agora para confirmar o resultado e garantir um tratamento precoce, se necessário."
            }
            else -> {
                // Padrão (Novembro Azul)
                title = "Novembro Azul Chegou!"
                message = "É hora de cuidar da saúde. Agende seu exame de PSA preventivo."
                NotificationScheduler.scheduleAnnualNotification(context)
            }
        }

        showNotification(context, title, message)
    }

    private fun showNotification(context: Context, title: String, message: String) {
        createNotificationChannel(context)

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            context, 0, intent, PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_flask)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            val notificationId = (System.currentTimeMillis() % 10000).toInt()
            NotificationManagerCompat.from(context).notify(notificationId, builder.build())
        }
    }

    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Lembretes PSA"
            val descriptionText = "Notificações de saúde e follow-up"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    companion object {
        const val CHANNEL_ID = "psa_reminder_channel"
    }
}