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
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.ViewModelFactoryDsl
import ir.saltech.puyakhan.R
import ir.saltech.puyakhan.data.error.UnknownPresentMethodException
import ir.saltech.puyakhan.data.model.App
import ir.saltech.puyakhan.data.model.OtpSms
import ir.saltech.puyakhan.ui.view.activity.BackgroundActivity
import ir.saltech.puyakhan.ui.view.activity.NOTIFY_OTP_CHANNEL_ID
import ir.saltech.puyakhan.ui.view.component.manager.CLIPBOARD_OTP_CODE
import ir.saltech.puyakhan.ui.view.component.manager.OTP_CODE_KEY
import ir.saltech.puyakhan.ui.view.component.manager.OtpManager
import ir.saltech.puyakhan.ui.view.component.window.SelectOtpWindow
import ir.saltech.puyakhan.ui.view.model.OtpCodesVM
import kotlin.random.Random


class OtpSmsReceiver : BroadcastReceiver() {
	private lateinit var appSettings: App.Settings

	@SuppressLint("UnsafeProtectedBroadcastReceiver")
	override fun onReceive(context: Context, intent: Intent) {
		try {
			appSettings = App.getSettings(context)
			val (otp, bank) = OtpManager.getOtpFromSms(getNewOtpSms(intent.extras!!)!!)
				?: return
			if (otp.isNotEmpty() && (bank ?: return).isNotEmpty()) {
				handleReceivedOtp(context, otp, bank)
			}
		} catch (e: Exception) {
			Log.e("SmsReceiver", "Exception smsReceiver: $e")
		}
	}

	private fun handleReceivedOtp(context: Context, otp: String, bank: String) {
		for (presentMethod in appSettings.presentMethods) {
			when (presentMethod) {
				App.PresentMethod.Otp.Copy ->
					copyOtpToClipboard(context, otp)

				App.PresentMethod.Otp.Notify ->
					showOtpNotification(context, otp, bank)

				App.PresentMethod.Otp.Select ->
					SelectOtpWindow.show(context)

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
			context,
			context.getString(R.string.otp_copied_to_clipboard), Toast.LENGTH_SHORT
		).show()
	}

	private fun showOtpNotification(context: Context, otp: String, bank: String?) {
		val expireTime = appSettings.expireTime
		val bigTextStyle = NotificationCompat.BigTextStyle()
		bigTextStyle.bigText("رمز یکبارمصرف شما $otp می باشد.")
		bigTextStyle.setBigContentTitle("از طرف ${bank ?: "بانک ناشناخته"}")
		val builder = NotificationCompat.Builder(context, NOTIFY_OTP_CHANNEL_ID)
			.setOnlyAlertOnce(true)
			.setSmallIcon(R.drawable.one_time_password_icon)
			.setContentTitle("رمز یکبار مصرف جدید")
			.setWhen(System.currentTimeMillis() + expireTime)
			.setShowWhen(true)
			.setContentText("برای مشاهده رمز، این را به پایین بکشید.")
			.setStyle(bigTextStyle)
			.addAction(
				NotificationCompat.Action.Builder(
					R.drawable.otp_action_copy,
					"کپی کردن",
					PendingIntent.getActivity(
						context,
						6749,
						Intent(OtpManager.Actions.COPY_OTP_ACTION).apply {
							setClass(context.applicationContext, BackgroundActivity::class.java)
							//putExtra(OTP_CODE_KEY, otp)
						},
						PendingIntent.FLAG_IMMUTABLE
					)
				).build()
			)
			.setPriority(NotificationCompat.PRIORITY_HIGH)
			.setVisibility(NotificationCompat.VISIBILITY_SECRET)
			.setTimeoutAfter(expireTime)
			.setAutoCancel(false)
			.setUsesChronometer(true)
			.setWhen(System.currentTimeMillis() + expireTime)
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
