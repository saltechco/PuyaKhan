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
import ir.saltech.puyakhan.data.model.OtpSms
import ir.saltech.puyakhan.ui.view.activity.NOTIFY_OTP_CHANNEL_ID
import ir.saltech.puyakhan.ui.view.components.manager.CLIPBOARD_OTP_CODE
import ir.saltech.puyakhan.ui.view.components.manager.OTP_CODE_KEY
import ir.saltech.puyakhan.ui.view.components.manager.OTP_SMS_EXPIRATION_TIME
import ir.saltech.puyakhan.ui.view.components.manager.OtpManager
import ir.saltech.puyakhan.ui.view.components.window.SelectOtpWindow
import kotlin.random.Random


class OtpSmsReceiver : BroadcastReceiver() {
	@SuppressLint("UnsafeProtectedBroadcastReceiver")
	override fun onReceive(context: Context, intent: Intent) {
		try {
			val bundle = intent.extras
			if (bundle != null) {
				val otpSms = getNewOtpSms(bundle)
				if (otpSms != null) {
					val (otp, bank) = OtpManager.getOtpFromSms(otpSms)
						?: return
					if (otp.isNotEmpty() && (bank ?: return).isNotEmpty()) {
						copyOtpToClipboard(context, otp)
						showOtpNotification(context, otp, bank)
						SelectOtpWindow.show(context)
					}
				}
			}
		} catch (e: Exception) {
			Log.e("SmsReceiver", "Exception smsReceiver: $e")
		}
	}

	private fun copyOtpToClipboard(context: Context, otp: String) {
		val clipboardManager =
			context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
		clipboardManager.setPrimaryClip(
			ClipData(ClipData.newPlainText(CLIPBOARD_OTP_CODE, otp))
		)
		Toast.makeText(
			context,
			context.getString(R.string.otp_copied_to_clipboard), Toast.LENGTH_SHORT
		).show()
	}

	private fun showOtpNotification(context: Context, otp: String, bank: String?) {
		val bigTextStyle = NotificationCompat.BigTextStyle()
		bigTextStyle.bigText("رمز یکبارمصرف شما $otp می باشد.")
		bigTextStyle.setBigContentTitle("از طرف ${bank ?: "بانک ناشناخته"}")
		val builder = NotificationCompat.Builder(context, NOTIFY_OTP_CHANNEL_ID)
			.setOnlyAlertOnce(true)
			.setSmallIcon(R.drawable.one_time_password_icon)
			.setContentTitle("رمز یکبار مصرف جدید")
			.setWhen(System.currentTimeMillis() + OTP_SMS_EXPIRATION_TIME)
			.setShowWhen(true)
			.setContentText("برای مشاهده رمز، این را به پایین بکشید.")
			.setStyle(bigTextStyle)
			.addAction(
				NotificationCompat.Action.Builder(
					R.drawable.otp_action_copy,
					"کپی کردن",
					PendingIntent.getBroadcast(
						context,
						6749,
						Intent(context, CopyOtpReceiver::class.java).apply {
							action = OtpManager.Actions.COPY_OTP_ACTION
							putExtra(OTP_CODE_KEY, otp)
						},
						PendingIntent.FLAG_IMMUTABLE
					)
				).build()
			)
			.setPriority(NotificationCompat.PRIORITY_HIGH)
			.setVisibility(NotificationCompat.VISIBILITY_SECRET)
			.setTimeoutAfter(OTP_SMS_EXPIRATION_TIME)
			.setAutoCancel(false)
			.setUsesChronometer(true)
			.setWhen(System.currentTimeMillis() + OTP_SMS_EXPIRATION_TIME)
			.setShowWhen(true)
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
			builder.setChronometerCountDown(true)
		with(NotificationManagerCompat.from(context)) {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
				if (ActivityCompat.checkSelfPermission(
						context,
						Manifest.permission.POST_NOTIFICATIONS
					) != PackageManager.PERMISSION_GRANTED
				) return
			}
			notify(Random.nextInt(100000, 1000000), builder.build())
		}
	}

	private fun getNewOtpSms(bundle: Bundle?): OtpSms? {
		if (bundle != null) {
			if (bundle.containsKey("pdus")) {
				val pdus = bundle.get("pdus")
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
