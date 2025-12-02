package ir.saltech.puyakhan.data.model

import kotlinx.serialization.Serializable

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

@Serializable
data class OtpPayload(
    val type: String = "OTP_DETECTED",
    val otp: String,
    val amount: String? = null,
    val bank: String? = null,
    val sentTime: Long,
    val expirationTime: Long,
    val elapsedTime: Long = 0
)
