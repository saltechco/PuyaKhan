package ir.saltech.puyakhan.ui.view.component.manager

import android.content.Context
import android.util.Log
import androidx.core.text.isDigitsOnly
import ir.saltech.puyakhan.data.model.App
import ir.saltech.puyakhan.data.model.OtpCode
import ir.saltech.puyakhan.data.model.OtpSms
import java.util.Date

internal const val OTP_SMS_EXPIRATION_TIME = 120_000L
internal const val OTP_CODE_KEY = "otp_code"
internal const val CLIPBOARD_OTP_CODE = "otp_code"

class OtpManager {

	object Actions {
		const val COPY_OTP_ACTION = "ir.saltech.puyakhan.COPY_OTP_ACTION"
	}

	companion object {
		private var selectionWords = "بانک|بلو&رمز|پویا&مبلغ&!کارمزد"
		private var recognitionWords = "رمز|پویا"

		fun getOtpFromSms(sms: OtpSms): OtpCode? {
			var otpTemp: String
			val smsBody = sms.body.split("\n").reversed()
			val bankName = if (
				smsBody.last().contains("بانک") ||
				smsBody.last().contains("بلو")
			) smsBody.last().trim() else return null
			for (line in smsBody) {
				if (recognizeOtpWords(line)) {
					if (line.contains(":")) {
						val splits = line.split(":")
						otpTemp = splits[splits.size - 1].trim()
						if (otpTemp.length <= 10 && otpTemp.isDigitsOnly()) return OtpCode(
							otpTemp,
							bankName,
							sms.date
						)
					} else {
						if (line.contains(" ")) {
							val splits = line.split(" ")
							otpTemp = splits[splits.size - 1].trim()
							if (otpTemp.length <= 10 && otpTemp.isDigitsOnly()) return OtpCode(
								otpTemp,
								bankName,
								sms.date
							)
						} else {
							otpTemp = line.trim()
							if (otpTemp.length <= 10 && otpTemp.isDigitsOnly()) return OtpCode(
								otpTemp,
								bankName,
								sms.date
							)
						}
					}
				}
			}
			return null
		}

		private fun getSmsList(context: Context): List<OtpSms> {
			val otpSmsList = mutableListOf<OtpSms>()
			val resolver = context.contentResolver
			val cursor = resolver.query(
				android.provider.Telephony.Sms.Inbox.CONTENT_URI,
				arrayOf("body", "date"),
				generateSelectionQuery(context),
				null,
				null
			)
			cursor.use { c ->
				while (c!!.moveToNext()) {
					otpSmsList += OtpSms(
						c.getString(c.getColumnIndexOrThrow("body")),
						c.getLong(c.getColumnIndexOrThrow("date"))
					)
				}
			}
			return otpSmsList
		}

		fun getCodeList(context: Context) = getSmsList(context).mapNotNull {
			Log.i("TAG", "${getOtpFromSms(it)}")
			getOtpFromSms(it)
		}

		private fun recognizeOtpWords(
			otpSmsLine: String,
			newWords: String = recognitionWords
		): Boolean {
			recognitionWords = newWords
			for (otpWord in recognitionWords) {
				if (otpSmsLine.contains(otpWord)) return true
			}
			return false
		}

		private fun generateSelectionQuery(
			context: Context,
			column: String = "body",
			newWords: String = selectionWords
		): String {
			selectionWords = newWords
			val query = StringBuilder()
			val filterTime = System.currentTimeMillis() - App.getSettings(context).expireTime
			for (andPairedWords in selectionWords.split("&")) {
				query.append(" (")
				for (orPairedWord in andPairedWords.split("|")) {
					query.append(
						" (" + if (orPairedWord.startsWith("!")) {
							"not $column like \"%${orPairedWord.substring(1)}%\""
						} else {
							"$column like \"%$orPairedWord%\""
						} + ") or"
					)
				}
				query.delete(query.length - 2, query.length).append(") and")
			}
			return "${
				query.removeRange(query.length - 4..<query.length).trim()
			} and date > $filterTime"
		}
	}
}

fun getDateTime(timestamp: Long) = Date(timestamp)
