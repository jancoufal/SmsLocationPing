package cz.iddqd.smslocationping

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.util.Log
import android.widget.Toast

class SmsReceiver : BroadcastReceiver() {

	@SuppressLint("UnsafeProtectedBroadcastReceiver")
	override fun onReceive(context: Context?, intent: Intent?) {
		Log.d("BROADCAST", "context: $context, intent: $intent")

		try {
			when (intent?.action) {
				Telephony.Sms.Intents.SMS_RECEIVED_ACTION -> onSmsReceived(context, intent)
			}
		}
		catch (e: Exception) {
			Log.e("BROADCAST", "${e.javaClass.simpleName}: ${e.message}")
			Toast.makeText(context, "SmsReceiver: ${e.javaClass.simpleName}: ${e.message}", Toast.LENGTH_LONG).show()
		}
	}

	private fun onSmsReceived(context: Context?, intent: Intent?) {
		val smsInfo = intent?.extras?.let { SmsInfo.createFromBundle(it) }

		Toast.makeText(context, "SmsReceiver: $smsInfo", Toast.LENGTH_LONG).show()
	}
}