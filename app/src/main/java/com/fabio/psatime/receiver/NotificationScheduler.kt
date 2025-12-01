package com.fabio.psatime.receiver

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import java.util.Calendar

object NotificationScheduler {

    const val EXTRA_NOTIFICATION_TYPE = "notification_type"
    const val TYPE_NOVEMBRO_AZUL = "novembro_azul"
    const val TYPE_ONE_YEAR_FOLLOWUP = "one_year_followup"
    const val TYPE_YELLOW_ALERT_FOLLOWUP = "yellow_alert_followup"

    private const val ID_NOVEMBRO = 1001
    private const val ID_ONE_YEAR = 1002
    private const val ID_YELLOW_ALERT = 1003 // ID Exclusivo para o alerta de 3 meses

    // Agendamento Fixo: Novembro Azul
    fun scheduleAnnualNotification(context: Context) {
        val intent = Intent(context, ReminderReceiver::class.java).apply {
            putExtra(EXTRA_NOTIFICATION_TYPE, TYPE_NOVEMBRO_AZUL)
        }
        scheduleAlarm(context, intent, getNovemberFirstDate(), ID_NOVEMBRO)
    }

    // Agendamento Dinâmico: 1 Ano do Último Exame
    fun scheduleFollowUpNotification(context: Context, lastExamTimestamp: Long) {
        val intent = Intent(context, ReminderReceiver::class.java).apply {
            putExtra(EXTRA_NOTIFICATION_TYPE, TYPE_ONE_YEAR_FOLLOWUP)
        }

        // --- MODO TESTE (Dispara em 10 segundos) ---
        // Substitua temporariamente a lógica real por esta:
//        val targetDate = Calendar.getInstance()
//        targetDate.add(Calendar.SECOND, 10)

        // --- MODO REAL (Restaurado) ---
        val targetDate = Calendar.getInstance()
        targetDate.timeInMillis = lastExamTimestamp
        targetDate.add(Calendar.YEAR, 1) // +1 Ano

        targetDate.set(Calendar.HOUR_OF_DAY, 9)
        targetDate.set(Calendar.MINUTE, 0)
        targetDate.set(Calendar.SECOND, 0)

        // Se já passou (usuário esqueceu), agenda para amanhã às 9h
        if (targetDate.timeInMillis < System.currentTimeMillis()) {
            val tomorrow = Calendar.getInstance()
            tomorrow.add(Calendar.DAY_OF_YEAR, 1)
            tomorrow.set(Calendar.HOUR_OF_DAY, 9)
            tomorrow.set(Calendar.MINUTE, 0)
            targetDate.timeInMillis = tomorrow.timeInMillis
        }

//      para teste, comente até aqui

        scheduleAlarm(context, intent, targetDate, ID_ONE_YEAR)
    }

    // NOVO: Agendamento de Alerta Amarelo (3 Meses)
    fun scheduleYellowAlertFollowUp(context: Context, lastExamTimestamp: Long) {
        val intent = Intent(context, ReminderReceiver::class.java).apply {
            putExtra(EXTRA_NOTIFICATION_TYPE, TYPE_YELLOW_ALERT_FOLLOWUP)
        }

        // --- MODO TESTE (Dispara em 10 segundos) ---
        // Substitua temporariamente a lógica real por esta:
        //val targetDate = Calendar.getInstance()
        //targetDate.add(Calendar.SECOND, 10)

        // MODO REAL (Mantenha comentado durante o teste)
        // Calcula: Data do Exame + 3 Meses
        val targetDate = Calendar.getInstance()
        targetDate.timeInMillis = lastExamTimestamp
        targetDate.add(Calendar.MONTH, 3) // +3 Meses

        targetDate.set(Calendar.HOUR_OF_DAY, 9)
        targetDate.set(Calendar.MINUTE, 0)
        targetDate.set(Calendar.SECOND, 0)

        // Se a data já passou (usuário está atrasado na confirmação), agenda para amanhã
        if (targetDate.timeInMillis < System.currentTimeMillis()) {
            val tomorrow = Calendar.getInstance()
            tomorrow.add(Calendar.DAY_OF_YEAR, 1)
            tomorrow.set(Calendar.HOUR_OF_DAY, 9)
            tomorrow.set(Calendar.MINUTE, 0)
            targetDate.timeInMillis = tomorrow.timeInMillis
        }

//      para teste, comente até aqui

        scheduleAlarm(context, intent, targetDate, ID_YELLOW_ALERT)
    }

    // NOVO: Cancelar Alerta Amarelo (Chamado se o último exame for Verde ou Vermelho)
    fun cancelYellowAlertFollowUp(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, ReminderReceiver::class.java)
        // O PendingIntent deve ser igual ao de criação para conseguir cancelar (mesmo ID, mesma Intent)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            ID_YELLOW_ALERT,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }

    private fun scheduleAlarm(context: Context, intent: Intent, calendar: Calendar, requestCode: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
        } catch (e: SecurityException) {
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
        }
    }

    private fun getNovemberFirstDate(): Calendar {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.MONTH, Calendar.NOVEMBER)
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 9)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }
        if (calendar.timeInMillis <= System.currentTimeMillis()) {
            calendar.add(Calendar.YEAR, 1)
        }
        return calendar
    }
}