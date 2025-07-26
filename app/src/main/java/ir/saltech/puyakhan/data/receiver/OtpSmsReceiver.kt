package ir.saltech.puyakhan.data.receiver

import android.Manifest
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import ir.saltech.puyakhan.App
import ir.saltech.puyakhan.R
import ir.saltech.puyakhan.data.error.UnknownPresentMethodException
import ir.saltech.puyakhan.data.model.OtpCode
import ir.saltech.puyakhan.data.util.OtpProcessor
import ir.saltech.puyakhan.data.util.OtpSmsHandler.getNewOtpSms
import ir.saltech.puyakhan.data.util.runOnUiThread
import ir.saltech.puyakhan.ui.view.activity.BackgroundActivity
import ir.saltech.puyakhan.ui.view.activity.MainActivity
import ir.saltech.puyakhan.ui.view.activity.NOTIFY_OTP_CHANNEL_ID
import ir.saltech.puyakhan.ui.view.window.SelectOtpWindow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private const val TAG = "OtpSmsReceiver"

class OtpSmsReceiver : BroadcastReceiver() {
	private lateinit var appSettings: App.Settings

	override fun onReceive(context: Context, intent: Intent) {
		if (!(intent.action == "android.provider.Telephony.SMS_RECEIVED" || intent.action == "android.intent.action.BOOT_COMPLETED")) {
			Log.e(TAG, "unrelated intent action detected. so ignore it.")
			return
		}
		if (intent.extras == null) return
		val pendingResult = goAsync()
		CoroutineScope(Dispatchers.IO).launch {
			try {
				val smsMessage = getNewOtpSms(intent.extras)
				if (smsMessage != null) {
					appSettings = App.getSettings(context)
					val parsedOtpCode = OtpProcessor.parseOtpCode(
						context,
						smsMessage,
						appSettings.expireTime
					)
					if (parsedOtpCode != null) {
						if (parsedOtpCode.otp.isNotEmpty()) {
							handleReceivedOtp(
								context,
								parsedOtpCode
							)
						}
					} else {
						Log.e(TAG, "Failed to parseOtpCode: parsed otpCode is null!")
					}
				}
			} catch (e: Exception) {
				Log.e("SmsReceiver", "Exception smsReceiver: $e")
			} finally {
				pendingResult.finish()
			}
		}
	}

	private fun handleReceivedOtp(context: Context, newOtpCode: OtpCode) {
		for (presentMethod in appSettings.presentMethods) {
			when (presentMethod) {
				App.PresentMethod.Otp.COPY -> copyOtpToClipboard(context, newOtpCode.otp)

				App.PresentMethod.Otp.NOTIFY -> showOtpNotification(context, newOtpCode)

				App.PresentMethod.Otp.SELECT -> SelectOtpWindow.show(context, appSettings)

				else -> throw UnknownPresentMethodException("The Present method must be either of Copy, Notify or Select")
			}
		}
	}

	private fun copyOtpToClipboard(context: Context, otp: String) {
		CoroutineScope(Dispatchers.IO).launch {
			val clipboardManager =
				context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
			clipboardManager.setPrimaryClip(
				ClipData(ClipData.newPlainText(App.Key.OTP_CODE_COPY, otp))
			)
			runOnUiThread {
				Toast.makeText(
					context, context.getString(R.string.otp_copied_to_clipboard), Toast.LENGTH_SHORT
				).show()
			}
		}
	}

	private fun showOtpNotification(context: Context, otpCode: OtpCode) {
		val bigTextStyle = NotificationCompat.BigTextStyle()
		if (otpCode.bank != null) {
			bigTextStyle.bigText(context.getString(R.string.your_new_otp_code_is, otpCode.otp))
			if (otpCode.price != null) {
				bigTextStyle.setBigContentTitle(
					context.getString(
						R.string.otp_code_from_bank_with_price, otpCode.bank, otpCode.price
					)
				)
			} else {
				bigTextStyle.setBigContentTitle(
					context.getString(
						R.string.otp_code_from_bank, otpCode.bank
					)
				)
			}
		} else if (otpCode.price != null) {
			bigTextStyle.bigText(context.getString(R.string.your_new_otp_code_is, otpCode.otp))
			bigTextStyle.setBigContentTitle(
				context.getString(
					R.string.otp_code_with_price, otpCode.price
				)
			)
		} else {
			bigTextStyle.bigText(context.getString(R.string.copy_otp_code_hint))
			bigTextStyle.setBigContentTitle(
				context.getString(
					R.string.your_new_otp_code_is,
					otpCode.otp
				)
			)
		}
		val builder =
			NotificationCompat.Builder(context, NOTIFY_OTP_CHANNEL_ID).setOnlyAlertOnce(true)
				.setSmallIcon(R.drawable.one_time_password_icon)
				.setContentTitle(context.getString(R.string.otp_sms_notification_title))
				.setWhen(System.currentTimeMillis() + otpCode.expirationTime).setShowWhen(true)
				.setContentText(context.getString(R.string.otp_sms_notification_short_message))
				.setContentIntent(
					PendingIntent.getActivity(
						context, 2312, Intent(
							context,
							MainActivity::class.java
						), PendingIntent.FLAG_IMMUTABLE
					)
				)
				.setStyle(bigTextStyle).addAction(
					NotificationCompat.Action.Builder(
						R.drawable.otp_action_copy,
						context.getString(R.string.copy_otp_code),
						PendingIntent.getActivity(
							context, 6749, Intent(OtpProcessor.Actions.COPY_OTP_ACTION).apply {
								setClass(context.applicationContext, BackgroundActivity::class.java)
								putExtra(App.Key.OTP_CODE_COPY, otpCode)
							}, PendingIntent.FLAG_IMMUTABLE
						)
					).build()
				).setPriority(NotificationCompat.PRIORITY_HIGH)
				.setVisibility(NotificationCompat.VISIBILITY_SECRET)
				.setTimeoutAfter(otpCode.expirationTime)
				.setAutoCancel(false).setUsesChronometer(true)
				.setWhen(System.currentTimeMillis() + otpCode.expirationTime).setShowWhen(true)
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) builder.setChronometerCountDown(true)
		with(NotificationManagerCompat.from(context)) {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
				if (ActivityCompat.checkSelfPermission(
						context, Manifest.permission.POST_NOTIFICATIONS
					) != PackageManager.PERMISSION_GRANTED
				) return
			}
			notify(otpCode.id, builder.build())
		}
	}
}
