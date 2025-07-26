package ir.saltech.puyakhan.ui.view.activity

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.core.app.NotificationManagerCompat
import ir.saltech.puyakhan.App
import ir.saltech.puyakhan.data.model.OtpCode

internal class BackgroundActivity : ComponentActivity() {
	private var otpCode: OtpCode? = null

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		handleIntents()
		with(NotificationManagerCompat.from(this)) {
			cancel(otpCode?.id ?: return@with)
		}
		doCopyTask()
		finishAffinity()
		finish()
	}

	private fun handleIntents() {
		if (intent != null) {
			val extras = intent.extras
			if (extras != null) {
				if (extras.containsKey(App.Key.OTP_CODE_COPY)) {
					otpCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
						extras.getParcelable(App.Key.OTP_CODE_COPY, OtpCode::class.java)
					} else {
						extras.getParcelable(App.Key.OTP_CODE_COPY)
					}
				}
			}
		}
	}

	private fun doCopyTask() {
		if (otpCode != null) {
			copySelectedCode(this, otpCode!!.otp)
		}
	}
}
