package cz.iddqd.smslocationping

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.telephony.SmsMessage
import android.util.Log
import android.widget.Toast

class SmsReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("BROADCAST", "context: $context, intent: $intent")

        val smsInfo = getSmsInfo(intent?.extras)

        Toast.makeText(context, "SmsReceiver: $smsInfo", Toast.LENGTH_SHORT).show()
    }

    private fun getSmsInfo(extras: Bundle?): SmsInfo {
        val pdus = extras?.get("pdus") as Array<*>
        val format = extras?.getString("format")

        var smsText = ""
        var senderNumber = ""
        for (pdu in pdus) {
            val smsMessage = SmsMessage.createFromPdu(pdu as ByteArray?, format)
            val subMessage = smsMessage?.displayMessageBody
            subMessage?.let { smsText = "$smsText$it" }
            senderNumber = smsMessage?.displayOriginatingAddress.toString()
        }

        return SmsInfo(smsText, senderNumber)
    }
}