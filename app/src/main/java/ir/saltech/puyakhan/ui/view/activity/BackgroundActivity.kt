package ir.saltech.puyakhan.ui.view.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import ir.saltech.puyakhan.data.model.App

internal class BackgroundActivity : ComponentActivity() {
	private var otp: String? = null

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		handleIntents()
		doCopyTask()
		finishAffinity()
	}

	private fun handleIntents() {
		if (intent != null) {
			val extras = intent.extras
			if (extras != null) {
				if (extras.containsKey(App.Key.CopyOtpCode)) {
					otp = extras.getString(App.Key.CopyOtpCode, null)
				}
			}
		}
	}

	private fun doCopyTask() {
		if (otp != null) {
			copySelectedCode(this, otp!!)
		}
	}
}
