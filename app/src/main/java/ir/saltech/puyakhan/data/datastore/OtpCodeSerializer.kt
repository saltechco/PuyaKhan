package ir.saltech.puyakhan.data.datastore

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import ir.saltech.puyakhan.data.model.OtpCode
import java.io.InputStream
import java.io.OutputStream

class OtpCodeSerializer(private val keystoreManager: KeystoreManager) : Serializer<List<OtpCode>> {
    override val defaultValue: List<OtpCode>
        get() = emptyList()

    override suspend fun readFrom(input: InputStream): List<OtpCode> {
        try {
            val encryptedBytes = input.readBytes()
            val decryptedBytes = keystoreManager.decrypt(encryptedBytes)
            val otpCodesProto = OtpCodes.parseFrom(decryptedBytes)
            return otpCodesProto.codesList.map { proto ->
                OtpCode(
                    otp = proto.otp,
                    bank = proto.bank,
                    price = proto.price,
                    sentTime = proto.sentTime,
                    expirationTime = proto.expirationTime,
                    elapsedTime = proto.elapsedTime
                )
            }
        } catch (e: Exception) {
            throw CorruptionException("Cannot read proto.", e)
        }
    }

    override suspend fun writeTo(t: List<OtpCode>, output: OutputStream) {
        val otpCodesProto = OtpCodes.newBuilder()
            .addAllCodes(t.map { code ->
                ir.saltech.puyakhan.data.datastore.OtpCode.newBuilder()
                    .setOtp(code.otp)
                    .setBank(code.bank ?: "")
                    .setPrice(code.price ?: "")
                    .setSentTime(code.sentTime)
                    .setExpirationTime(code.expirationTime)
                    .setElapsedTime(code.elapsedTime)
                    .build()
            })
            .build()
        val encryptedBytes = keystoreManager.encrypt(otpCodesProto.toByteArray())
        output.write(encryptedBytes)
    }
}
