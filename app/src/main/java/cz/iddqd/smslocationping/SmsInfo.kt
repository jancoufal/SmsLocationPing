package cz.iddqd.smslocationping

import android.os.Bundle
import android.telephony.SmsMessage

data class SmsInfo(
	var message: String,
	var senderNumber: String
) {
	companion object {
		fun createFromBundle(bundle: Bundle): SmsInfo {
			val format = bundle.getString("format")
			val pdus = bundle.get("pdus")

			if (pdus is Array<*>)
			{
				var senderNumber = ""
				val smsText = pdus
					.map { it as ByteArray? }
					.map { SmsMessage.createFromPdu(it, format) }
					.onEach { senderNumber = it?.displayOriginatingAddress.toString() }
					.mapNotNull { it?.displayMessageBody }
					.joinToString()

				return SmsInfo(smsText, senderNumber)
			}

			throw IllegalArgumentException("Failed to create SmsInfo from incoming data.")
		}
	}
}
