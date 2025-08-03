package ir.saltech.puyakhan.ui.view.activity

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.core.app.NotificationManagerCompat
import ir.saltech.puyakhan.App
import ir.saltech.puyakhan.data.util.copySelectedCode
import kotlin.math.abs

internal class BackgroundActivity : ComponentActivity() {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		handleIntents()
		finishAffinity()
	}

	private fun handleIntents() {
		if (intent != null) {
			val extras = intent.extras
			if (extras != null) {
				if (extras.containsKey(App.Key.OTP_ID_INTENT)) {
					removeRelatedNotification(extras.getInt(App.Key.OTP_ID_INTENT))
				}
				if (extras.containsKey(App.Key.OTP_CODE_INTENT)) {
					doCopyTask(extras.getString(App.Key.OTP_CODE_INTENT)!!)
				}
			}
		}
	}

	private fun removeRelatedNotification(id: Int) {
		with(NotificationManagerCompat.from(this)) {
			Log.i("TAG", "Try to remove related notification -> $id")
			cancel(abs(id))
		}
	}

	private fun doCopyTask(code: String) {
		Log.i("TAG", "copy the code -> code is $code")
		copySelectedCode(this, code)
	}
}
