package ir.saltech.puyakhan.data.model

data class OtpCode(
	val id: Int,
	val otp: String,
	val bank: String? = null,
	val price: String? = null,
	val expirationTime: Long,
	var elapsedTime: Long = 0,
	val sms: OtpSms,
)

data class OtpSms(val body: String, val sentTime: Long)
