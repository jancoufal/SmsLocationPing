package cz.iddqd.smslocationping

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.telephony.SmsManager
import android.util.Log
import android.widget.Toast
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
			}
		}
		catch (e : Exception)
		{
			Log.e(TAG, "${e.javaClass.simpleName}: ${e.message}")
		}
	}

	companion object {
		val TAG: String = SmsPingStaticBroadcastReceiver::class.java.simpleName
	}
}