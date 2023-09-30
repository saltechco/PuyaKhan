package ir.saltech.puyakhan.ui.manager

import android.content.Context
import androidx.core.text.isDigitsOnly
import ir.saltech.puyakhan.data.model.OtpSms
import ir.saltech.puyakhan.ui.view.activity.activity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private const val OTP_WORDS_PREFERENCES = "otp_words"
internal const val OTP_CODE_KEY = "otp_code"

class OtpSmsManager(context: Context) {

	object Actions {
		const val COPY_OTP_ACTION = "ir.saltech.puyakhan.COPY_OTP_ACTION"
	}

	companion object {
		private var selectionWords = "بانک|بلو&رمز|پویا&مبلغ&!کارمزد"
		private var recognitionWords = "رمز|پویا"

		fun updateOtpWords(
			newSelectionWords: String = selectionWords,
			newRecognitionWords: String = recognitionWords
		) {
			selectionWords = newSelectionWords
			recognitionWords = newRecognitionWords
		}

		fun getOtpFromSms(sms: OtpSms, showBankName: Boolean = false): Pair<String, String?>? {
			var otpTemp: String
			val smsBody = sms.body.split("\n").reversed()
			val bankName = if (showBankName && (
						smsBody.last().contains("بانک") || smsBody.last().contains("بلو")
						)
			) smsBody.last().trim() else null
			for (line in smsBody) {
				if (recognizeOtpWords(line)) {
					if (line.contains(":")) {
						val splits = line.split(":")
						otpTemp = splits[splits.size - 1].trim()
						if (otpTemp.length <= 10 && otpTemp.isDigitsOnly()) return Pair(
							otpTemp,
							bankName
						)
					} else {
						if (line.contains(" ")) {
							val splits = line.split(" ")
							otpTemp = splits[splits.size - 1].trim()
							if (otpTemp.length <= 10 && otpTemp.isDigitsOnly()) return Pair(
								otpTemp,
								bankName
							)
						} else {
							otpTemp = line.trim()
							if (otpTemp.length <= 10 && otpTemp.isDigitsOnly()) return Pair(
								otpTemp,
								bankName
							)
						}
					}
				}
			}
			return null
		}

		fun getSmsList(): List<OtpSms> {
			val otpSmsList = mutableListOf<OtpSms>()
			val resolver = activity.contentResolver
			val cursor = resolver.query(
				android.provider.Telephony.Sms.Inbox.CONTENT_URI,
				arrayOf("body", "date"),
				generateSelectionQuery(),
				null,
				null
			)
			cursor.use { c ->
				while (c!!.moveToNext()) {
					otpSmsList += OtpSms(
						c.getString(c.getColumnIndexOrThrow("body")),
						getDateTime(c.getString(c.getColumnIndexOrThrow("date")))
					)
				}
			}
			return otpSmsList
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
			column: String = "body",
			newWords: String = selectionWords
		): String {
			selectionWords = newWords
			val query = StringBuilder()
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

			//query.append("((body like \"%بلو%\") or (body like \"%بانک%\")) and ((body like \"%رمز%\") or (body like \"%پویا%\")) and (body like \"%مبلغ%\") and (not body like \"%کارمزد%\") ")
			return query.removeRange(query.length - 4..<query.length).trim().toString()
		}
	}
}

fun getDateTime(s: String): String {
	return try {
		val sdf = SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.ENGLISH)
		val netDate = Date(s.toLong())
		sdf.format(netDate)
	} catch (e: Exception) {
		e.toString()
	}
}
