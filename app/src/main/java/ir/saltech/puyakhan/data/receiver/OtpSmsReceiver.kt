package ir.saltech.puyakhan.data.receiver

import android.Manifest
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.telephony.SmsMessage
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import ir.saltech.puyakhan.R
import ir.saltech.puyakhan.data.error.UnknownPresentMethodException
import ir.saltech.puyakhan.data.model.App
import ir.saltech.puyakhan.data.model.OtpSms
import ir.saltech.puyakhan.data.util.CLIPBOARD_OTP_CODE
import ir.saltech.puyakhan.data.util.OtpProcessor
import ir.saltech.puyakhan.ui.view.activity.BackgroundActivity
import ir.saltech.puyakhan.ui.view.activity.NOTIFY_OTP_CHANNEL_ID
import ir.saltech.puyakhan.ui.view.window.SelectOtpWindow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.random.Random


private const val PDUS = "pdus"

class OtpSmsReceiver : BroadcastReceiver() {
	private lateinit var appSettings: App.Settings

	@SuppressLint("UnsafeProtectedBroadcastReceiver")
	override fun onReceive(context: Context, intent: Intent) {
		if (intent.extras == null) return
		val pendingResult = goAsync()
		CoroutineScope(Dispatchers.IO).launch {
			try {
				appSettings = App.getSettings(context)
				val smsMessage = getNewOtpSms(intent.extras)
				if (smsMessage != null) {
					val otpCodeObj = OtpProcessor.extractOtpInfo(
						smsMessage.body.trim(),
						smsMessage.date,
						appSettings
					)
					if (otpCodeObj != null) {
						if (otpCodeObj.otp.isNotEmpty()) {
							handleReceivedOtp(
								context,
								otpCodeObj.otp,
								otpCodeObj.bank,
								price = otpCodeObj.price
							)
						}
					}
				}
			} catch (e: Exception) {
				Log.e("SmsReceiver", "Exception smsReceiver: $e")
			} finally {
				pendingResult.finish()
			}
		}
	}

	private fun handleReceivedOtp(context: Context, otp: String, bank: String?, price: String?) {
		for (presentMethod in appSettings.presentMethods) {
			when (presentMethod) {
				App.PresentMethod.Otp.COPY -> copyOtpToClipboard(context, otp)

				App.PresentMethod.Otp.NOTIFY -> showOtpNotification(context, otp, bank, price)

				App.PresentMethod.Otp.SELECT -> SelectOtpWindow.show(context)

				else -> throw UnknownPresentMethodException("The Present method must be either of Copy, Notify or Select")
			}
		}
	}

	private fun copyOtpToClipboard(context: Context, otp: String) {
		val clipboardManager =
			context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
		clipboardManager.setPrimaryClip(
			ClipData(ClipData.newPlainText(CLIPBOARD_OTP_CODE, otp))
		)
		Toast.makeText(
			context, context.getString(R.string.otp_copied_to_clipboard), Toast.LENGTH_SHORT
		).show()
	}

	private fun showOtpNotification(context: Context, otp: String, bank: String?, price: String?) {
		val expireTime = appSettings.expireTime
		val bigTextStyle = NotificationCompat.BigTextStyle()
		if (bank != null) {
			bigTextStyle.bigText(context.getString(R.string.your_new_otp_code_is, otp))
			if (price != null) {
				bigTextStyle.setBigContentTitle(
					context.getString(
						R.string.otp_code_from_bank_with_price, bank, price
					)
				)
			} else {
				bigTextStyle.setBigContentTitle(
					context.getString(
						R.string.otp_code_from_bank, bank
					)
				)
			}
		} else if (price != null) {
			bigTextStyle.bigText(context.getString(R.string.your_new_otp_code_is, otp))
			bigTextStyle.setBigContentTitle(
				context.getString(
					R.string.otp_code_with_price, price
				)
			)
		} else  {
			bigTextStyle.bigText(context.getString(R.string.copy_otp_code_hint))
			bigTextStyle.setBigContentTitle(context.getString(R.string.your_new_otp_code_is, otp))
		}
		val builder =
			NotificationCompat.Builder(context, NOTIFY_OTP_CHANNEL_ID).setOnlyAlertOnce(true)
				.setSmallIcon(R.drawable.one_time_password_icon)
				.setContentTitle(context.getString(R.string.otp_sms_notification_title))
				.setWhen(System.currentTimeMillis() + expireTime).setShowWhen(true)
				.setContentText(context.getString(R.string.otp_sms_notification_short_message))
				.setStyle(bigTextStyle).addAction(
					NotificationCompat.Action.Builder(
						R.drawable.otp_action_copy,
						context.getString(R.string.copy_otp_code),
						PendingIntent.getActivity(
							context, 6749, Intent(OtpProcessor.Actions.COPY_OTP_ACTION).apply {
								setClass(context.applicationContext, BackgroundActivity::class.java)
								putExtra(App.Key.OTP_CODE_COPY_KEY, otp)
							}, PendingIntent.FLAG_IMMUTABLE
						)
					).build()
				).setPriority(NotificationCompat.PRIORITY_HIGH)
				.setVisibility(NotificationCompat.VISIBILITY_SECRET).setTimeoutAfter(expireTime)
				.setAutoCancel(false).setUsesChronometer(true)
				.setWhen(System.currentTimeMillis() + expireTime).setShowWhen(true)
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) builder.setChronometerCountDown(true)
		with(NotificationManagerCompat.from(context)) {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
				if (ActivityCompat.checkSelfPermission(
						context, Manifest.permission.POST_NOTIFICATIONS
					) != PackageManager.PERMISSION_GRANTED
				) return
			}
			notify(Random.nextInt(100000, 1000000), builder.build())
		}
	}

	private fun getNewOtpSms(bundle: Bundle?): OtpSms? {
		if (bundle != null) {
			if (bundle.containsKey(PDUS)) {
				val pdus = bundle.get(PDUS)
				if (pdus != null) {
					val pdusObj = pdus as Array<*>
					var message = ""
					var date = 0L
					for (i in pdusObj.indices) {
						val currentMessage = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
							SmsMessage.createFromPdu(pdusObj[i] as ByteArray?, "3gpp")
						} else {
							SmsMessage.createFromPdu(pdusObj[i] as ByteArray?)
						}
						message += currentMessage.displayMessageBody
						date = currentMessage.timestampMillis
					} // end for loop
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
