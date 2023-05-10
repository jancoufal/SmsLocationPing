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
import android.telephony.SmsManager
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.Tasks
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class SmsPingStaticBroadcastReceiver : BroadcastReceiver() {

	val ACCEPTED_NUMBERS = setOf(
		"+420777666555",
		"+420999888777",
	)

	val REQUEST_MESSAGE = "kdesi"

	val RESPONSE_MESSAGE = "Tady (presnost %.2fm): https://www.google.com/maps/search/?api=1&query=%f%%2C%f"

	@SuppressLint("UnsafeProtectedBroadcastReceiver")
	override fun onReceive(context: Context?, intent: Intent?) {
		Log.d(TAG, "context: $context, intent: $intent")

		try {
			when (intent?.action) {
				Telephony.Sms.Intents.SMS_RECEIVED_ACTION -> context?.let { onSmsReceived(context, intent) }
			}
		}
		catch (e: Exception) {
			Log.e(TAG, "${e.javaClass.simpleName}: ${e.message}")
			Toast.makeText(context, "SmsReceiver: ${e.javaClass.simpleName}: ${e.message}", Toast.LENGTH_LONG).show()
		}
	}

	@OptIn(DelicateCoroutinesApi::class)
	@SuppressLint("MissingPermission")
	private fun onSmsReceived(context: Context, intent: Intent?) {
		try {
			val smsInfo = intent?.extras?.let { SmsInfo.createFromBundle(it) }

			GlobalScope.launch(Dispatchers.Default) {
				smsInfo?.let {
					if (ACCEPTED_NUMBERS.contains(it.senderNumber) && REQUEST_MESSAGE == smsInfo.message) {

						Log.d(TAG, "context: $context")

						// get accurate location
						val tokenSource = CancellationTokenSource()
						val locSvc = LocationServices.getFusedLocationProviderClient(context)
						val location = Tasks.await(
							locSvc.getCurrentLocation(
								LocationRequest.PRIORITY_HIGH_ACCURACY,
								tokenSource.token
							)
						)

						Log.d(TAG, "location: $location")

						// send response sms
						val smsMessage = when(location) {
							null -> "Unable to resolve location"
							else -> RESPONSE_MESSAGE.format(location.accuracy, location.latitude, location.longitude)
						}
						val smsManager = context.getSystemService(SmsManager::class.java) as SmsManager
						smsManager.sendTextMessage(
							smsInfo.senderNumber,
							null,
							smsMessage,
							null,
							null
						)
					}
				}

				//Toast.makeText(context, "SmsReceiver: $smsInfo", Toast.LENGTH_LONG).show()
				//showNotification(context, smsInfo)
			}
		}
		catch (e : Exception)
		{
			Log.e(TAG, "${e.javaClass.simpleName}: ${e.message}")
		}
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

	companion object {
		val TAG: String = SmsPingStaticBroadcastReceiver::class.java.simpleName
	}
}