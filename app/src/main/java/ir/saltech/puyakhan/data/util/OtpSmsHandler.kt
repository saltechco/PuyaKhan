package ir.saltech.puyakhan.data.util

import android.os.Bundle
import android.telephony.SmsMessage
import ir.saltech.puyakhan.data.model.OtpSms

private const val SMS_PDUS_KEY = "pdus"
private const val SMS_FORMAT = "3gpp"

object OtpSmsHandler {
	fun getNewOtpSms(bundle: Bundle?): OtpSms? {
		if (bundle != null) {
			if (bundle.containsKey(SMS_PDUS_KEY)) {
				val pdus = bundle[SMS_PDUS_KEY]
				if (pdus != null) {
					val pdusObj = pdus as Array<*>
					var message = ""
					var date = 0L
					for (i in pdusObj.indices) {
						val currentMessage =
							SmsMessage.createFromPdu(pdusObj[i] as ByteArray?, SMS_FORMAT)
						message += currentMessage.displayMessageBody
						date = currentMessage.timestampMillis
					}
					return OtpSms(message, date)
				} else {
					return null
				}
			} else {
				return null
			}
		} else {
			return null
		}
	}
}
