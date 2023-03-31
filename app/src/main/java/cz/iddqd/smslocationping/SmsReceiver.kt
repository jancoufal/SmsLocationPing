package cz.iddqd.smslocationping

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast

class SmsReceiver : BroadcastReceiver() {
	override fun onReceive(context: Context?, intent: Intent?) {
		Log.d("BROADCAST", "context: $context, intent: $intent")

		val smsInfo = intent?.extras?.let { SmsInfo.createFromBundle(it) }

		Toast.makeText(context, "SmsReceiver: $smsInfo", Toast.LENGTH_SHORT).show()
	}
}