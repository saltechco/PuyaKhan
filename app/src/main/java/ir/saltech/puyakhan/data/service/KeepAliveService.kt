package ir.saltech.puyakhan.data.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import ir.saltech.puyakhan.ApplicationLoader
import ir.saltech.puyakhan.R
import ir.saltech.puyakhan.ui.view.activity.NOTIFY_SERVICE_CHANNEL_ID
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val KEEP_ALIVE_SERVICE_NOTIFICATION_ID = 728939

class KeepAliveService : Service() {
	val serviceScope = CoroutineScope(Dispatchers.IO)

	override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
		sendNotification()
		serviceScope.launch {
			while (!ApplicationLoader.isActivityLaunched) {
				Log.i("KeepAliveService", "PuyaKhan runs a service to keep alive the app for listening new sms in xiaomi...")
				delay(4000)
			}
		}
		return START_STICKY
	}

	private fun sendNotification() {
		val builder = NotificationCompat.Builder(this, NOTIFY_SERVICE_CHANNEL_ID)
			.setOnlyAlertOnce(true)
			.setSilent(true)
			.setContentTitle(getString(R.string.app_keep_alive_alert))
			.setPriority(NotificationCompat.PRIORITY_LOW)
			.setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
			.setCategory(NotificationCompat.CATEGORY_SERVICE)
		startForeground(KEEP_ALIVE_SERVICE_NOTIFICATION_ID, builder.build())
	}

	override fun onDestroy() {
		super.onDestroy()
		serviceScope.cancel()
	}

	override fun onBind(intent: Intent?): IBinder? {
		return null
	}
}