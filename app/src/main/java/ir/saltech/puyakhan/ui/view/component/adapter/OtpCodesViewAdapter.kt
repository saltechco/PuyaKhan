package ir.saltech.puyakhan.ui.view.component.adapter

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
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
import ir.saltech.puyakhan.App
import ir.saltech.puyakhan.R
import ir.saltech.puyakhan.data.model.OtpCode
import ir.saltech.puyakhan.data.util.shareSelectedOtpCode

internal class OtpCodesViewAdapter(private var otpCodes: MutableList<OtpCode>) :
	Adapter<OtpCodesViewAdapter.OtpCodesViewHolder>() {
	private lateinit var context: Context

	internal inner class OtpCodesViewHolder(v: View) : ViewHolder(v) {
		val otpCard: CardView = v.findViewById(R.id.otp_card)
		val otpCode: TextView = v.findViewById(R.id.otp_code)
		val copyOtpCode: ImageButton = v.findViewById(R.id.copy_otp_code)
		val shareOtpCode: ImageButton = v.findViewById(R.id.share_otp_code)
		val codeExpireBar: ProgressBar = v.findViewById(R.id.otp_expire_bar)
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OtpCodesViewHolder {
		context = parent.context
		return OtpCodesViewHolder(
			LayoutInflater.from(parent.context).inflate(R.layout.layout_template_otp, parent, false)
		)
	}

	override fun onBindViewHolder(
		holder: OtpCodesViewHolder, @SuppressLint("RecyclerView") position: Int,
	) {
		holder.otpCode.text = otpCodes[position].otp
		holder.copyOtpCode.setOnClickListener {
			copyOtpCode(otpCodes[position].otp)
		}
		holder.shareOtpCode.setOnClickListener {
			shareSelectedOtpCode(context, otpCodes[position])
		}
		holder.codeExpireBar.progress =
			100 - ((otpCodes[position].elapsedTime.toDouble() / otpCodes[position].expirationTime.toDouble()) * 100).toInt()
		if (holder.codeExpireBar.progress == 0) {
			showAsExpiredCode(holder)
		}
	}

	private fun showAsExpiredCode(holder: OtpCodesViewHolder) {
		holder.otpCard.setCardBackgroundColor(
			ContextCompat.getColor(
				context, R.color.otpExpiredCardBackground
			)
		)
		holder.otpCard.isClickable = false
		holder.codeExpireBar.progress = 0
		holder.codeExpireBar.visibility = View.GONE
		holder.copyOtpCode.visibility = View.INVISIBLE
		holder.shareOtpCode.visibility = View.INVISIBLE
	}

	private fun copyOtpCode(otpCode: String) {
		val clipboardManager =
			context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
		clipboardManager.setPrimaryClip(
			ClipData(ClipData.newPlainText(App.Key.OTP_CODE_COPY, otpCode))
		)
		Toast.makeText(
			context, context.getString(R.string.otp_copied_to_clipboard), Toast.LENGTH_SHORT
		).show()
	}

	override fun getItemCount(): Int = otpCodes.size

	override fun getItemViewType(position: Int): Int = position
}
