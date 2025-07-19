package ir.saltech.puyakhan.data.model

data class OtpCode(val otp: String, val bank: String?, val price: String?, val sentTime: Long, val expirationTime: Long, var elapsedTime: Long = 0)
