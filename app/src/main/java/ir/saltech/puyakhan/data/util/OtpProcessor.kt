package ir.saltech.puyakhan.data.util

import android.R.id.message
import android.content.Context
import android.util.Log
import androidx.compose.runtime.toMutableStateList
import ir.saltech.puyakhan.data.datastore.OtpDataStore
import ir.saltech.puyakhan.data.model.OtpCode
import ir.saltech.puyakhan.data.model.OtpSms
import ir.saltech.puyakhan.data.util.OtpParser.extractAmount
import ir.saltech.puyakhan.data.util.OtpParser.extractBankName
import ir.saltech.puyakhan.data.util.OtpParser.extractOtp
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.collections.filter
import kotlin.io.encoding.Base64
import kotlin.random.Random

internal const val MAX_OTP_SMS_EXPIRATION_TIME = 180_000L

private const val TAG = "OtpProcessor"

object OtpProcessor {

	object Actions {
		const val COPY_OTP_ACTION = "ir.saltech.puyakhan.COPY_OTP_ACTION"
	}

	/**
	 * Analyzes an SMS message to extract code details including OTP,
	 * bank name, and transaction amount.
	 *
	 * @param message The SMS content to parse.
	 * @return A [OtpCode] object containing the extracted data.
	 */
	suspend fun parseOtpCode(
		context: Context,
		sms: OtpSms,
		preferredExpireTime: Long
	): OtpCode? {
		val message = sms.body.trim()
		val otp = extractOtp(message)?.trim() ?: return null
		val bankName = extractBankName(message)?.trim()
		val amount = extractAmount(message)?.trim()

		val receivedOtp =
			OtpCode(
				id = Random.nextInt(),
				bank = bankName,
				price = amount,
				otp = otp,
				expirationTime = preferredExpireTime,
				sms = sms.copy(body = Base64.UrlSafe.encode(message.toByteArray()))
			)

		Log.d(TAG, "New OTP Code : $receivedOtp")
		OtpDataStore(context).addOtpCode(receivedOtp)

		return receivedOtp
	}

	fun getOtpCodes(context: Context): Flow<MutableList<OtpCode>> {
		val dataStore = OtpDataStore(context)
		return dataStore.getOtpCodes().map { codes ->
			codes.filter {
				System.currentTimeMillis() - it.sms.sentTime < it.expirationTime
			}.toMutableStateList()
		}
	}

	suspend fun clearOtpCodes(context: Context) {
		val dataStore = OtpDataStore(context)
		dataStore.clearAll()
	}
}
