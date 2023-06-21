package cz.iddqd.smslocationping

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Looper
import android.provider.Telephony
import android.telephony.SmsManager
import android.util.Log
import android.widget.Toast
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.Tasks
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.time.Duration.Companion.seconds

class SmsPingStaticBroadcastReceiver : BroadcastReceiver() {

	val ACCEPTED_NUMBERS = setOf(
		"+420999888777",
	)

	val REQUEST_MESSAGE = "kdesi"

	val RESPONSE_MESSAGE = "Tady (presnost %.2fm): https://www.google.com/maps/search/?api=1&query=%f%%2C%f"

	// how many times we ask system to provide a location whether it still returns null
	val LOCATION_RETRIEVE_TRIES = 3

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

					if (ACCEPTED_NUMBERS.contains(it.senderNumber) && REQUEST_MESSAGE.equals(smsInfo.message, ignoreCase = true)) {
						// when we initialize SMS manager first, we can use in a catch block send at least something
						var smsManager : SmsManager? = null
						try {
							Log.d(TAG, "context: $context")

							smsManager = context.getSystemService(SmsManager::class.java) as SmsManager

							val locationProvider = LocationServices.getFusedLocationProviderClient(context)

							// get accurate location with retrying
							var location : Location? = null
							var tryCounter = LOCATION_RETRIEVE_TRIES
							while (location == null && tryCounter-- > 0) {
								val tokenSource = CancellationTokenSource()
								location = Tasks.await(
									locationProvider.getCurrentLocation(
										LocationRequest.PRIORITY_HIGH_ACCURACY,
										tokenSource.token
									)
								)
							}

							if (location == null) {
								var locationByCallback : Location? = null
								val locationCallbackInvoked = CountDownLatch(1)

								val locationCallback = object : LocationCallback() {
									override fun onLocationResult(locationResult : LocationResult) {
										for (loc in locationResult.locations) {
											Log.d(TAG, "Location hook: $loc")
											if (loc != null)
												locationByCallback = loc
										}

										if (locationByCallback == null) {
											Log.d(TAG, "Using last location")
											locationByCallback = locationResult.lastLocation
										}

										locationCallbackInvoked.countDown()
									}
								}

								val locationRequest = LocationRequest.create()
								locationRequest.fastestInterval = 5L.seconds.inWholeMilliseconds
								locationRequest.smallestDisplacement = 10.0f
								locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

								Log.d(TAG, "Requesting location by callback.")
								locationProvider.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())

								if (locationCallbackInvoked.await(20L, TimeUnit.SECONDS))
								{
									location = locationByCallback
								}

								Log.d(TAG, "Removing location callback.")
								locationProvider.removeLocationUpdates(locationCallback)
							}

							Log.d(TAG, "Location: $location")

							// send response sms
							val smsMessage = when (location) {
								null -> "Unable to resolve location"
								else -> RESPONSE_MESSAGE.format(location.accuracy, location.latitude, location.longitude)
							}

							smsManager.sendTextMessage(smsInfo.senderNumber, null, smsMessage, null, null)
						}
						catch (e : Exception)
						{
							val errorMessage = "Error: ${e.javaClass.simpleName}: ${e.message}"

							smsManager?.apply {
								sendTextMessage(smsInfo.senderNumber, null, errorMessage, null, null)
							}

							Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
						}
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