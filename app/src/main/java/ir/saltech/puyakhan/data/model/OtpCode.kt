package ir.saltech.puyakhan.data.model

data class OtpCode(
	val id: Int,
	val otp: String,
	val bank: String?,
	val price: String?,
	val sentTime: Long,
	val expirationTime: Long,
	var elapsedTime: Long = 0,
)
