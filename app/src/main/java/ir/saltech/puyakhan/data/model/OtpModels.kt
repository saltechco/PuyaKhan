package ir.saltech.puyakhan.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class OtpCode(
	val id: Int,
	val otp: String,
	val bank: String?,
	val price: String?,
	val sentTime: Long,
	val expirationTime: Long,
	var elapsedTime: Long = 0,
) : Parcelable

data class OtpSms(val body: String, val date: Long)
