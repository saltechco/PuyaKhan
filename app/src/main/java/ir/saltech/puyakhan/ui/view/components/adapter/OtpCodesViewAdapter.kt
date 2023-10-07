package ir.saltech.puyakhan.ui.view.components.adapter

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import ir.saltech.puyakhan.R
import ir.saltech.puyakhan.data.model.OtpCode
import ir.saltech.puyakhan.ui.view.components.manager.CLIPBOARD_OTP_CODE
import ir.saltech.puyakhan.ui.view.components.manager.OTP_SMS_EXPIRATION_TIME


private const val INTERVAL = 1000L

class OtpCodesViewAdapter(private var otpCodes: List<OtpCode>) :
	Adapter<OtpCodesViewAdapter.OtpCodesViewHolder>() {
	private lateinit var context: Context

	class OtpCodesViewHolder(v: View) : ViewHolder(v) {
		val otpCard: CardView = v.findViewById(R.id.otp_card)
		val otpCode: TextView = v.findViewById(R.id.otp_code)
		val copyOtpCode: ImageButton = v.findViewById(R.id.copy_otp_code)
		val shareOtpCode: ImageButton = v.findViewById(R.id.share_otp_code)
		val codeExpireBar: ProgressBar = v.findViewById(R.id.otp_expire_bar)
	}

	override fun onCreateViewHolder(p0: ViewGroup, p1: Int): OtpCodesViewHolder {
		context = p0.context
		return OtpCodesViewHolder(
			LayoutInflater.from(p0.context).inflate(R.layout.layout_template_otp, p0, false)
		)
	}

	override fun onBindViewHolder(
		holder: OtpCodesViewHolder,
		@SuppressLint("RecyclerView") position: Int
	) {
		holder.otpCode.text = otpCodes[position].otp
		holder.copyOtpCode.setOnClickListener {
			copyOtpCode(otpCodes[position].otp)
		}
		holder.shareOtpCode.setOnClickListener {
			shareOtpCode(
				otpCodes[position].otp,
				otpCodes[position].bank
			)
		}
		object : CountDownTimer(
			100000000,
			INTERVAL
		) {
			override fun onTick(millisUntilFinished: Long) {
				holder.codeExpireBar.progress =
					100 - (((System.currentTimeMillis() - otpCodes[position].sentTime).toDouble() / OTP_SMS_EXPIRATION_TIME.toDouble()) * 100).toInt()

				if (holder.codeExpireBar.progress == 0) {
					Toast.makeText(
						context,
						"مهلت استفاده از رمز پویا ${otpCodes[position].otp} به پایان رسید.",
						Toast.LENGTH_SHORT
					).show()
					showAsExpiredCode(holder)
					this.cancel()
				}
			}

			override fun onFinish() {

			}
		}.start()
	}

	private fun showAsExpiredCode(holder: OtpCodesViewHolder) {
		holder.otpCard.setCardBackgroundColor(
			ContextCompat.getColor(
				context,
				R.color.otpExpiredCardBackground
			)
		)
		holder.otpCard.isClickable = false
		holder.codeExpireBar.progress = 0
		holder.codeExpireBar.visibility = View.GONE
		holder.copyOtpCode.visibility = View.INVISIBLE
		holder.shareOtpCode.visibility = View.INVISIBLE
	}

	@SuppressLint("NotifyDataSetChanged")
	private fun deleteOtpCode(position: Int) {
		if (position >= 0) {
			otpCodes = otpCodes.minusElement(otpCodes[position])
			Toast.makeText(
				context,
				context.getString(R.string.otp_code_deleted),
				Toast.LENGTH_SHORT
			).show()
			notifyDataSetChanged()
		}
	}

	private fun copyOtpCode(otpCode: String) {
		val clipboardManager =
			context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
		clipboardManager.setPrimaryClip(
			ClipData(ClipData.newPlainText(CLIPBOARD_OTP_CODE, otpCode))
		)
		Toast.makeText(
			context,
			context.getString(R.string.otp_copied_to_clipboard), Toast.LENGTH_SHORT
		).show()
	}

	private fun getOtpCodeText(otpCode: String, bank: String?): String {
		return "رمز پویا من برای یک تراکنش، در  $bank، $otpCode می باشد."
	}

	private fun shareOtpCode(otpCode: String, bank: String?) {
		val shareIntent = Intent(Intent.ACTION_SEND)
		shareIntent.type = "text/plain"
		shareIntent.putExtra(Intent.EXTRA_TEXT, getOtpCodeText(otpCode, bank))
		context.startActivity(
			Intent.createChooser(
				shareIntent,
				context.getString(R.string.send_otp_to)
			).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
		)
	}

	override fun getItemCount(): Int = otpCodes.size

	override fun getItemViewType(position: Int): Int = position
}
