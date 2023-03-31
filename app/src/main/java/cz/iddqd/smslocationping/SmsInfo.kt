package cz.iddqd.smslocationping

import android.os.Bundle
import android.telephony.SmsMessage

data class SmsInfo(
	var message: String,
	var senderNumber: String
) {
	companion object {
		fun createFromBundle(bundle: Bundle): SmsInfo {
			val pdus = bundle.get("pdus") as Array<*>
			val format = bundle.getString("format")

			var senderNumber = ""
			val smsText = pdus
				.map { it as ByteArray? }
				.map { SmsMessage.createFromPdu(it, format) }
				.onEach { senderNumber = it?.displayOriginatingAddress.toString() }
				.mapNotNull { it?.displayMessageBody }
				.joinToString()

			return SmsInfo(smsText, senderNumber)
		}
	}
}
