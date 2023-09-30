package ir.saltech.puyakhan.data.receiver

import android.content.BroadcastReceiver
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.util.Log
import ir.saltech.puyakhan.ui.manager.OTP_CODE_KEY


class CopyOtpReceiver : BroadcastReceiver() {
	override fun onReceive(context: Context?, intent: Intent?) {
		val bundle = intent?.extras!!
		if (bundle.containsKey(OTP_CODE_KEY)) {
			val otp = bundle.getString(OTP_CODE_KEY)!!
			val clipboardManager =
				context!!.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
			clipboardManager.setPrimaryClip(
				ClipData(ClipData.newPlainText("otp_code", otp))
			)
			Log.i(
				"SmsReceiver",
				"New OTP Code: ${clipboardManager.primaryClip?.getItemAt(0)?.text}"
			)
		}
	}

}