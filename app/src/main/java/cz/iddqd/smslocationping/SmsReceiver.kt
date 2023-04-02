package cz.iddqd.smslocationping

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Telephony
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat

class SmsReceiver : BroadcastReceiver() {

	@SuppressLint("UnsafeProtectedBroadcastReceiver")
	override fun onReceive(context: Context?, intent: Intent?) {
		Log.d("BROADCAST", "context: $context, intent: $intent")

		try {
			when (intent?.action) {
				Telephony.Sms.Intents.SMS_RECEIVED_ACTION -> context?.let { onSmsReceived(context, intent) }
			}
		}
		catch (e: Exception) {
			Log.e("BROADCAST", "${e.javaClass.simpleName}: ${e.message}")
			Toast.makeText(context, "SmsReceiver: ${e.javaClass.simpleName}: ${e.message}", Toast.LENGTH_LONG).show()
		}
	}

	private fun onSmsReceived(context: Context, intent: Intent?) {
		val smsInfo = intent?.extras?.let { SmsInfo.createFromBundle(it) }

		Toast.makeText(context, "SmsReceiver: $smsInfo", Toast.LENGTH_LONG).show()
		showNotification(context, smsInfo)
	}

	private fun showNotification(context: Context, smsInfo: SmsInfo?) {
		val contentIntent = PendingIntent.getActivity(context, 0, Intent(context, MainActivity::class.java), PendingIntent.FLAG_IMMUTABLE)

		val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

		val notificationBuilder = NotificationCompat.Builder(context, "default")
			.setSmallIcon(R.drawable.ic_launcher_foreground)
			.setVisibility(NotificationCompat.VISIBILITY_SECRET)
			.setContentTitle("New Location Ping")
			.setContentText("${smsInfo?.senderNumber ?: "(unknown)"} sends a location")
			.setContentIntent(contentIntent)
			.setDefaults(Notification.DEFAULT_ALL)
			.setAutoCancel(true)

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			val notificationChannelId = "10001"

			val notificationChannel = NotificationChannel(
				notificationChannelId,
				"NOTIFICATION_CHANNEL_NAME",
				NotificationManager.IMPORTANCE_HIGH
			)

			notificationBuilder.setChannelId(notificationChannelId)

			notificationManager.createNotificationChannel(notificationChannel)
		}

		notificationManager.notify(1, notificationBuilder.build())
	}
}