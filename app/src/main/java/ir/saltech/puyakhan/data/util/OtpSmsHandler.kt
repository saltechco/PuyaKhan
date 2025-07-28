package ir.saltech.puyakhan.data.util

import android.os.Bundle
import android.telephony.SmsMessage
import ir.saltech.puyakhan.data.model.OtpSms

private const val SMS_PDUS_KEY = "pdus"
private const val SMS_FORMAT = "3gpp"

object OtpSmsHandler {
	fun getNewOtpSms(bundle: Bundle?): OtpSms? {
		val pdus = bundle?.get(SMS_PDUS_KEY) as? Array<*> ?: return null

		var message = ""
		val date = System.currentTimeMillis()
		for (i in pdus.indices) {
			val currentMessage =
				SmsMessage.createFromPdu(pdus[i] as ByteArray?, SMS_FORMAT)
			message += currentMessage.displayMessageBody
//			date = currentMessage.timestampMillis -> This represents the time above of sent time! :(
		}
		return OtpSms(message, date)
	}
}
