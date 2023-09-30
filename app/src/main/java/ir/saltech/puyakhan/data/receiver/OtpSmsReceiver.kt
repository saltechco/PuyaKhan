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
import android.telephony.SmsMessage
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import ir.saltech.puyakhan.R
import ir.saltech.puyakhan.data.model.OtpSms
import ir.saltech.puyakhan.ui.manager.OTP_CODE_KEY
import ir.saltech.puyakhan.ui.manager.OtpSmsManager
import ir.saltech.puyakhan.ui.view.activity.NOTIFY_CHANNEL_ID
import kotlin.random.Random

class OtpSmsReceiver : BroadcastReceiver() {
	@SuppressLint("UnsafeProtectedBroadcastReceiver")
	override fun onReceive(context: Context, intent: Intent) {
		val bundle = intent.extras
		try {
			if (bundle != null) {
				val pdusObj = bundle.get("pdus") as Array<*>
				var message = ""
				var date = ""
				for (i in pdusObj.indices) {
					val currentMessage = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
						SmsMessage.createFromPdu(pdusObj[i] as ByteArray?, "3gpp")
					} else {
						SmsMessage.createFromPdu(pdusObj[i] as ByteArray?)
					}
					message += currentMessage.displayMessageBody
					date = currentMessage.timestampMillis.toString()
				} // end for loop
				val otpSms = OtpSms(message, date)
				val (otp, bank) = OtpSmsManager.getOtpFromSms(otpSms, true) ?: return
				val clipboardManager =
					context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
				clipboardManager.setPrimaryClip(
					ClipData(
						"Bank OTP Code",
						arrayOf("text/plain"),
						ClipData.Item(otp)
					)
				)
				val bigStyle = NotificationCompat.BigTextStyle()
				bigStyle.bigText("رمز یکبارمصرف شما $otp می باشد.")
				bigStyle.setBigContentTitle("از طرف ${bank ?: "بانک ناشناخته"}")
				val builder = NotificationCompat.Builder(context, NOTIFY_CHANNEL_ID)
					.setOnlyAlertOnce(true)
					.setSmallIcon(R.drawable.one_time_password_icon)
					.setContentTitle("رمز یکبار مصرف جدید")
					.setSubText(bank ?: "بانک ناشناخته")
					.setContentText("برای مشاهده رمز، این را به پایین بکشید.")
					.setStyle(bigStyle)
					.addAction(
						NotificationCompat.Action.Builder(
							R.drawable.otp_password_copy,
							"کپی کردن",
							PendingIntent.getBroadcast(
								context,
								6749,
								Intent(context, CopyOtpReceiver::class.java).apply {
									action = OtpSmsManager.Actions.COPY_OTP_ACTION
									putExtra(OTP_CODE_KEY, otp)
								},
								PendingIntent.FLAG_IMMUTABLE
							)
						).build()
					)
					.setPriority(NotificationCompat.PRIORITY_HIGH)
					.setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
					.setAutoCancel(false)
				Log.i("SmsReceiver", "message: $message, data:$date")
				// Show Alert
				with(NotificationManagerCompat.from(context)) {
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
						if (ActivityCompat.checkSelfPermission(
								context,
								Manifest.permission.POST_NOTIFICATIONS
							) != PackageManager.PERMISSION_GRANTED
						) return
					}
					val randomInt = Random.nextInt(100000, 1000000)
					Log.i("SmsReceiver", "notificationId: $randomInt")
					notify(randomInt, builder.build())
				}
			} // bundle is null
		} catch (e: Exception) {
			Log.e("SmsReceiver", "Exception smsReceiver: $e")
		}
	}
}
