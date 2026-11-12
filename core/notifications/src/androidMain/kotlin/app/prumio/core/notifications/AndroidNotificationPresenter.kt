package app.prumio.core.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.text.format.DateFormat
import androidx.core.app.NotificationCompat
import app.prumio.core.common.time.TimeProvider
import app.prumio.core.domain.reminder.AlarmKind
import app.prumio.core.domain.reminder.NotificationContent
import app.prumio.core.domain.reminder.NotificationPresenter
import java.util.Date

/** Native channels and item-tag replacement; strings stay localized in Android resources. Rules: BR-REM-008, BR-REM-015, BR-REM-016. */
class AndroidNotificationPresenter(private val context: Context, private val time: TimeProvider) :
    NotificationPresenter {
    private val manager = context.getSystemService(NotificationManager::class.java)
    override suspend fun show(content: NotificationContent) {
        channels()
        val alarm = content.alarm
        val kind = alarm.kind.name.lowercase()
        val target = context.packageManager.getLaunchIntentForPackage(context.packageName)?.component ?: return
        fun activity(uri: String) = PendingIntent.getActivity(
            context,
            0,
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse(uri),
            ).setComponent(target).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
        val date = Date(alarm.anchor.toEpochMilliseconds())
        val zone = java.util.TimeZone.getTimeZone(time.timeZone().id)
        val dateText = DateFormat.getDateFormat(context).apply { timeZone = zone }.format(date)
        val timeText = DateFormat.getTimeFormat(context).apply { timeZone = zone }.format(date)
        val dateTime = "$dateText · $timeText"
        val body = when (alarm.kind) {
            AlarmKind.Task -> context.getString(R.string.reminder_task_due, dateTime)

            AlarmKind.Appointment -> context.getString(
                R.string.reminder_appointment_at,
                dateTime,
                content.location.orEmpty(),
            )

            AlarmKind.Matter -> context.getString(R.string.reminder_matter_follow_up, content.waitingFor.orEmpty())
        }
        val notification = NotificationCompat.Builder(
            context,
            if (alarm.kind ==
                AlarmKind.Matter
            ) {
                FOLLOW_UPS
            } else {
                REMINDERS
            },
        )
            .setSmallIcon(R.drawable.ic_reminder).setContentTitle(content.title).setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body)).setAutoCancel(true)
            .setContentIntent(activity("prumio://$kind/${alarm.itemId}"))
            .setCategory(
                if (alarm.kind ==
                    AlarmKind.Appointment
                ) {
                    NotificationCompat.CATEGORY_EVENT
                } else {
                    NotificationCompat.CATEGORY_REMINDER
                },
            )
        if (alarm.kind == AlarmKind.Task) {
            val complete = PendingIntent.getBroadcast(
                context,
                0,
                Intent(
                    context,
                    NotificationActionReceiver::class.java,
                ).setAction(ACTION_COMPLETE).setData(Uri.parse("prumio://complete/${alarm.itemId}")),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            )
            notification.addAction(0, context.getString(R.string.reminder_complete), complete)
        }
        notification.addAction(
            0,
            context.getString(R.string.reminder_snooze),
            activity("prumio://snooze/${alarm.key.value}"),
        )
        manager.notify(alarm.itemKey, 0, notification.build())
    }
    override suspend fun cancelFor(itemKey: String) {
        manager.cancel(itemKey, 0)
    }
    fun channels() {
        manager.createNotificationChannels(
            listOf(
                NotificationChannel(
                    REMINDERS,
                    context.getString(R.string.channel_reminders),
                    NotificationManager.IMPORTANCE_HIGH,
                ),
                NotificationChannel(
                    FOLLOW_UPS,
                    context.getString(R.string.channel_follow_ups),
                    NotificationManager.IMPORTANCE_DEFAULT,
                ),
                NotificationChannel(
                    SUMMARIES,
                    context.getString(R.string.channel_summaries),
                    NotificationManager.IMPORTANCE_LOW,
                ),
            ),
        )
    }
    companion object {
        const val REMINDERS = "reminders"
        const val FOLLOW_UPS = "follow_ups"
        const val SUMMARIES = "summaries"
        const val ACTION_COMPLETE = "app.prumio.COMPLETE_TASK"
    }
}
