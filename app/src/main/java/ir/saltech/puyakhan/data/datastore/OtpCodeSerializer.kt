package ir.saltech.puyakhan.data.datastore

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.toMutableStateList
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import ir.saltech.puyakhan.data.model.OtpCode
import ir.saltech.puyakhan.data.model.OtpSms
import java.io.InputStream
import java.io.OutputStream

class OtpCodeSerializer(private val keystoreManager: KeystoreManager) :
	Serializer<MutableList<OtpCode>> {
	override val defaultValue: MutableList<OtpCode>
		get() = mutableStateListOf()

	override suspend fun readFrom(input: InputStream): MutableList<OtpCode> {
		try {
			val encryptedBytes = input.readBytes()
			val decryptedBytes = keystoreManager.decrypt(encryptedBytes)
			val otpCodesProto = OtpCodes.parseFrom(decryptedBytes)
			return otpCodesProto.codesList.map { proto ->
				OtpCode(
					id = proto.id,
					otp = proto.otp,
					bank = proto.bank.ifEmpty { null },
					price = proto.price.ifEmpty { null },
					expirationTime = proto.expirationTime,
					elapsedTime = proto.elapsedTime,
					sms = OtpSms(body = proto.sms.body, sentTime = proto.sms.sentTime)
				)
			}.toMutableStateList()
		} catch (e: Exception) {
			throw CorruptionException("Cannot read proto.", e)
		}
	}

	override suspend fun writeTo(t: MutableList<OtpCode>, output: OutputStream) {
		val otpCodesProto = OtpCodes.newBuilder().addAllCodes(t.map { code ->
				ir.saltech.puyakhan.data.datastore.OtpCode.newBuilder().setId(code.id)
					.setOtp(code.otp).setBank(code.bank ?: "").setPrice(code.price ?: "")
					.setExpirationTime(code.expirationTime).setElapsedTime(code.elapsedTime)
					.setSms(ir.saltech.puyakhan.data.datastore.OtpSms.newBuilder().setBody(code.sms.body).setSentTime(code.sms.sentTime).build()).build()
			}).build()
		val encryptedBytes = keystoreManager.encrypt(otpCodesProto.toByteArray())
		output.write(encryptedBytes)
	}
}
