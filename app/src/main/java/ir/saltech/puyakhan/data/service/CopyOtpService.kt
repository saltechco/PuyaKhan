package ir.saltech.puyakhan.data.service

import android.app.Service
import android.content.ActivityNotFoundException
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import android.util.Log
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status

internal val CREDENTIAL_PICKER_REQUEST = 42913  // Set to an unused request code
internal val SMS_CONSENT_REQUEST = 2  // Set to an unused request code

class CopyOtpService : Service() {
	private val smsVerificationReceiver = object : BroadcastReceiver() {
		override fun onReceive(context: Context, intent: Intent) {
			if (SmsRetriever.SMS_RETRIEVED_ACTION == intent.action) {
				val extras = intent.extras
				val smsRetrieverStatus = extras?.get(SmsRetriever.EXTRA_STATUS) as Status

				when (smsRetrieverStatus.statusCode) {
					CommonStatusCodes.SUCCESS -> {
						// Get consent intent
						val consentIntent =
							extras.getParcelable<Intent>(SmsRetriever.EXTRA_CONSENT_INTENT)
						try {
							// Start activity to show consent dialog to user, activity must be started in
							// 5 minutes, otherwise you'll receive another TIMEOUT intent
							Log.i("TAG", "An SMS Received")
							startActivity(consentIntent!!)
						} catch (e: ActivityNotFoundException) {
							// Handle the exception ...
						}
					}

					CommonStatusCodes.TIMEOUT -> {
						// Time out occurred, handle the error.
					}
				}
			}
		}
	}

	override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
		val intentFilter = IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION)

		registerReceiver(
			smsVerificationReceiver,
			intentFilter,
			SmsRetriever.SEND_PERMISSION,
			null,
			RECEIVER_EXPORTED
		)

		return START_STICKY
	}

	override fun onBind(intent: Intent?): IBinder? {
		return null
	}
}