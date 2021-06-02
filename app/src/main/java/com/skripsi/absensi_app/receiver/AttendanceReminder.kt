package com.skripsi.absensi_app.receiver

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.skripsi.absensi_app.R
import com.skripsi.absensi_app.ui.main.MainActivity
import com.skripsi.absensi_app.utils.pref.AttendancePref
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class AttendanceReminder : BroadcastReceiver() {

    companion object {
        const val ATTENDANCE = "Attendance"
        const val LATE = "Late"
        const val RESET = "Reset"
        const val EXTRA_MESSAGE = "message"
        const val EXTRA_TYPE = "type"

        private const val ID_ATTENDANCE = 101
        private const val ID_LATE = 102
        private const val ID_RESET = 103
        private const val TIME_FORMAT = "HH:mm"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val type = intent.getStringExtra(EXTRA_TYPE)
        val message = intent.getStringExtra(EXTRA_MESSAGE)

        val title = when {
            type.equals(ATTENDANCE, ignoreCase = true) -> ATTENDANCE
            type.equals(RESET, ignoreCase = true) -> RESET
            else -> LATE
        }
        val notifId = when {
            type.equals(ATTENDANCE, ignoreCase = true) -> ID_ATTENDANCE
            type.equals(RESET, ignoreCase = true) -> ID_RESET
            else -> ID_LATE
        }

        if (title == LATE || notifId == ID_LATE) {
            val attendanceStatus = AttendancePref.getAttendanceStatus(context)
            if (attendanceStatus) {
                Log.d("REMINDER_ME", "OnReceive : $title - $message - Status $attendanceStatus")
                AttendancePref.setAttendanceStatus(context, false)
                showNotification(context, title, message, notifId)
            }

        } else if (title == RESET || notifId == ID_RESET) {
            Log.d("REMINDER_ME", "OnReceive : $title - $message - Status ${AttendancePref.getAttendanceStatus(context)}")
            AttendancePref.setAttendanceStatus(context, true)
            Log.d("REMINDER_ME", "OnReceive : $title - $message - Status ${AttendancePref.getAttendanceStatus(context)}")
            showNotification(context, title, message, notifId)
        } else {
            Log.d("REMINDER_ME", "OnReceive : $title - $message")
            showNotification(context, title, message, notifId)
        }
    }

    fun setReminderAttendanceAlarm(context: Context, type: String?, time: String, message: String?) {

        if (isDateInvalid(time, TIME_FORMAT)) return

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AttendanceReminder::class.java)
        intent.putExtra(EXTRA_MESSAGE, message)
        intent.putExtra(EXTRA_TYPE, type)

        val timeArray = time.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(timeArray[0]))
        calendar.set(Calendar.MINUTE, Integer.parseInt(timeArray[1]))
        calendar.set(Calendar.SECOND, 0)

        val uniqueId = System.currentTimeMillis().toInt()
        val pendingIntent = PendingIntent.getBroadcast(context, uniqueId, intent, 0)
        alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
//        Toast.makeText(context, context.getString(R.string.reminder_active), Toast.LENGTH_SHORT).show()
        Log.d("REMINDER_ME", "OnSet : Attendance Time => $time")
    }

    fun setReminderLateAttendance(context: Context, type: String?, time: String, message: String?) {

        if (isDateInvalid(time, TIME_FORMAT)) return

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AttendanceReminder::class.java)
        intent.putExtra(EXTRA_MESSAGE, message)
        intent.putExtra(EXTRA_TYPE, type)

        val timeArray = time.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(timeArray[0]))
        calendar.set(Calendar.MINUTE, Integer.parseInt(timeArray[1]))
        calendar.set(Calendar.SECOND, 0)

        val uniqueId = System.currentTimeMillis().toInt()
        val pendingIntent = PendingIntent.getBroadcast(context, uniqueId, intent, 0)
        alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
//        Toast.makeText(context, context.getString(R.string.reminder_active), Toast.LENGTH_SHORT).show()
        Log.d("REMINDER_ME", "OnSet : Late Time => $time")
    }

    fun resetReminderAttendanceAlarm(context: Context, type: String?, time: String, message: String?) {

        if (isDateInvalid(time, TIME_FORMAT)) return

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AttendanceReminder::class.java)
        intent.putExtra(EXTRA_MESSAGE, message)
        intent.putExtra(EXTRA_TYPE, type)

        val timeArray = time.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(timeArray[0]))
        calendar.set(Calendar.MINUTE, Integer.parseInt(timeArray[1]))
        calendar.set(Calendar.SECOND, 0)

        val uniqueId = System.currentTimeMillis().toInt()
        val pendingIntent = PendingIntent.getBroadcast(context, uniqueId, intent, 0)
        alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
//        Toast.makeText(context, context.getString(R.string.reminder_active), Toast.LENGTH_SHORT).show()
        Log.d("REMINDER_ME", "OnSet : Attendance Time => $time")
    }

    private fun showNotification(context: Context, title: String, message: String?, notifId: Int) {
        val CHANNEL_ID = "Channel_id"
        val CHANNEL_NAME = "AlarmManager channel"
        Log.d("REMINDER_ME", "OnSuccess : $title")

        val notificationCompat =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val pendingIntent =
            PendingIntent.getActivity(context, 0, Intent(context, MainActivity::class.java), 0)

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_time)
            .setContentTitle(context.getString(R.string.app_name))
            .setContentText(message)
            .setContentIntent(pendingIntent)
            .setColor(ContextCompat.getColor(context, android.R.color.transparent))
            .setVibrate(longArrayOf(1000, 1000, 1000, 1000, 1000))
            .setSound(alarmSound)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setAutoCancel(true)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                enableVibration(true)
                vibrationPattern = longArrayOf(1000, 1000, 1000, 1000, 1000)
            }

            builder.setChannelId(CHANNEL_ID)
            notificationCompat.createNotificationChannel(channel)
        }

        val notification = builder.build()
        notificationCompat.notify(notifId, notification)
    }

    // Gunakan metode ini untuk mengecek apakah alarm tersebut sudah terdaftar di alarm manager
    fun isAlarmSet(context: Context, type: String): Boolean {
        val intent = Intent(context, AttendanceReminder::class.java)
        val requestCode = ID_ATTENDANCE

        return PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_NO_CREATE) != null
    }

    private fun isDateInvalid(date: String, format: String): Boolean {
        return try {
            val df = SimpleDateFormat(format, Locale.getDefault())
            df.isLenient = false
            df.parse(date)
            false
        } catch (e: ParseException) {
            true
        }
    }

}