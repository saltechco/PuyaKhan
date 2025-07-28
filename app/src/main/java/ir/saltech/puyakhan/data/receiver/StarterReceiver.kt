package ir.saltech.puyakhan.data.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import ir.saltech.puyakhan.ApplicationLoader
import ir.saltech.puyakhan.data.util.startKeepAliveService

class StarterReceiver : BroadcastReceiver() {
	override fun onReceive(context: Context?, intent: Intent?) {
		if (intent?.action != "android.intent.action.BOOT_COMPLETED") {
			Log.e("StarterReceiver", "unrelated intent action detected. so ignore it.")
			return
		}
		startKeepAliveService(ApplicationLoader.applicationContext)
	}
}
