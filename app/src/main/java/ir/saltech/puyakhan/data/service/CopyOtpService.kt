package ir.saltech.puyakhan.data.service

import android.app.Service
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.util.Log
import ir.saltech.puyakhan.ui.manager.OTP_CODE_KEY

class CopyOtpService : Service() {

	override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
		val bundle = intent?.extras!!
		Log.i("TAG", "Service started!")
		if (bundle.containsKey(OTP_CODE_KEY)) {
			val otp = bundle.getString(OTP_CODE_KEY)!!
			val clipboardManager =
				getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
			clipboardManager.setPrimaryClip(
				ClipData(ClipData.newPlainText("otp_code", otp))
			)
			Log.i("SmsReceiver", "New OTP Code: $otp")
		}
		return START_STICKY
	}

	override fun onBind(intent: Intent?): IBinder? {
		return null
	}
}