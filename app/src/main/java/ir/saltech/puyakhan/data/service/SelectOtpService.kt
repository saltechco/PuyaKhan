package ir.saltech.puyakhan.data.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import ir.saltech.puyakhan.ui.view.activity.NOTIFY_SERVICE_CHANNEL_ID
import ir.saltech.puyakhan.ui.view.window.SelectOtpWindow

private const val OTP_OVERLAY_SERVICE_ID = 8482

class SelectOtpService : Service() {
	override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
		showWindowNotification()
		SelectOtpWindow(this)
		return START_STICKY
	}

	private fun showWindowNotification() {
		val builder = NotificationCompat.Builder(this, NOTIFY_SERVICE_CHANNEL_ID)
			.setOnlyAlertOnce(true)
			.setSilent(true)
			.setContentTitle("در حال نمایش پنجره رمز یکبار مصرف")
			.setPriority(NotificationCompat.PRIORITY_LOW)
			.setVisibility(NotificationCompat.VISIBILITY_SECRET)
			.setCategory(NotificationCompat.CATEGORY_SERVICE)
		startForeground(OTP_OVERLAY_SERVICE_ID, builder.build())
	}

	override fun onBind(intent: Intent): IBinder? {
		throw UnsupportedOperationException("Not yet implemented")
	}
}
