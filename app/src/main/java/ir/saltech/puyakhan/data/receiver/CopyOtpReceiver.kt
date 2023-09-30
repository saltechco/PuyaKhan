package ir.saltech.puyakhan.data.receiver

import android.content.BroadcastReceiver
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import ir.saltech.puyakhan.ui.manager.OTP_CODE_KEY


class CopyOtpReceiver : BroadcastReceiver() {
	override fun onReceive(context: Context?, intent: Intent?) {
		val bundle = intent?.extras!!
		if (bundle.containsKey(OTP_CODE_KEY)) {
			val otp = bundle.getString(OTP_CODE_KEY)!!
			val clipboardManager =
				context!!.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
			clipboardManager.setPrimaryClip(
				ClipData(
					"Bank OTP Code",
					arrayOf("text/plain"),
					ClipData.Item(otp)
				)
			)
		}
	}

}