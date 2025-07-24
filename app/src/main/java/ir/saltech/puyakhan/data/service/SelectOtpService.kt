package ir.saltech.puyakhan.data.service

import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import ir.saltech.puyakhan.App
import ir.saltech.puyakhan.R
import ir.saltech.puyakhan.data.util.OtpProcessor
import ir.saltech.puyakhan.data.util.runOnUiThread
import ir.saltech.puyakhan.ui.view.activity.NOTIFY_SERVICE_CHANNEL_ID
import ir.saltech.puyakhan.ui.view.window.SelectOtpWindow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private const val OTP_OVERLAY_SERVICE_ID = 8482

class SelectOtpService : Service() {
	companion object {
		private const val TAG = "SelectOtpService"
	}

	override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
		showWindowNotification()
		val appSettings: App.Settings? =
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
				intent.getParcelableExtra(
					SelectOtpWindow.APP_SETTINGS_KEY,
					App.Settings::class.java
				)
			} else {
				intent.getParcelableExtra(SelectOtpWindow.APP_SETTINGS_KEY)
			}
		if (appSettings != null) {
			val otpWindow = SelectOtpWindow.getInstance(applicationContext, appSettings)
			getOtpCodes(otpWindow)
			Log.i(TAG, "Starting SelectOtpWindow ..")
		} else {
			Log.e(
				TAG,
				"AppSettings is null or OtpCodes is null or them are not initiated for this service"
			)
		}
		return START_STICKY_COMPATIBILITY
	}

	private fun getOtpCodes(otpWindow: SelectOtpWindow) {
		CoroutineScope(Dispatchers.IO).launch {
			OtpProcessor.getOtpCodes(applicationContext).collect { otpCodes ->
				Log.i(TAG, "New Otp Codes are arrived")
				runOnUiThread {
					otpWindow.setOtpCodes(otpCodes)
				}
			}
		}
	}

	private fun showWindowNotification() {
		val builder = NotificationCompat.Builder(this, NOTIFY_SERVICE_CHANNEL_ID)
			.setOnlyAlertOnce(true)
			.setSilent(true)
			.setContentTitle(getString(R.string.otp_sms_window_alert))
			.setPriority(NotificationCompat.PRIORITY_LOW)
			.setVisibility(NotificationCompat.VISIBILITY_SECRET)
			.setCategory(NotificationCompat.CATEGORY_SERVICE)
		startForeground(OTP_OVERLAY_SERVICE_ID, builder.build())
	}

	override fun onBind(intent: Intent): IBinder? {
		return null
	}
}
