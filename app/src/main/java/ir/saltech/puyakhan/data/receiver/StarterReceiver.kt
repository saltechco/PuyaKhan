package ir.saltech.puyakhan.data.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import ir.saltech.puyakhan.ApplicationLoader
import ir.saltech.puyakhan.data.util.startKeepAliveService
import ir.saltech.puyakhan.data.util.startLocalServerService

class StarterReceiver : BroadcastReceiver() {
	override fun onReceive(context: Context?, intent: Intent?) {
		if (intent?.action != Intent.ACTION_BOOT_COMPLETED) {
			Log.e("StarterReceiver", "unrelated intent action detected. so ignore it.")
			return
		}
		if (!ApplicationLoader.isActivityLaunched) {
			Log.i("TAG", "On Keep Alive Service wanted -> receiver")
            ApplicationLoader.applicationContext.startKeepAliveService()
		}
        Log.i("TAG", "On LocalServer Service wanted -> receiver")
        ApplicationLoader.applicationContext.startLocalServerService()
    }
}
