package ir.saltech.puyakhan.data.receiver

import android.content.BroadcastReceiver
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import ir.saltech.puyakhan.R
import ir.saltech.puyakhan.ui.view.component.manager.CLIPBOARD_OTP_CODE
import ir.saltech.puyakhan.ui.view.component.manager.OTP_CODE_KEY

@Deprecated(message = "Use BackgroundActivity instead")
class CopyOtpReceiver : BroadcastReceiver() {
	override fun onReceive(context: Context?, intent: Intent?) {
		val bundle = intent?.extras!!
		if (bundle.containsKey(OTP_CODE_KEY)) {
			val otp = bundle.getString(OTP_CODE_KEY)!!
			val clipboardManager =
				context!!.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
			clipboardManager.setPrimaryClip(
				ClipData(ClipData.newPlainText(CLIPBOARD_OTP_CODE, otp))
			)
			Toast.makeText(
				context,
				"رمز پویا $otp کپی شد.", Toast.LENGTH_SHORT
			).show()
		}
	}

}