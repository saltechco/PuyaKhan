package ir.saltech.puyakhan.data.util

import android.content.Context
import android.provider.Telephony
import androidx.core.text.isDigitsOnly
import ir.saltech.puyakhan.data.model.App
import ir.saltech.puyakhan.data.model.OtpCode
import ir.saltech.puyakhan.data.model.OtpSms
import kotlin.text.iterator


@Deprecated("We gonna use `OtpProcessor.kt` instead of this.")
internal class OtpManager {

	object Actions {
		const val COPY_OTP_ACTION = "ir.saltech.puyakhan.COPY_OTP_ACTION"
	}

	companion object {
		private var selectionWords = "بانک|بلو|محرمانه&رمز|پویا&مبلغ&!کارمزد"
		private var recognitionWords = "رمز|پویا"

		fun getOtpFromSms(sms: OtpSms): OtpCode? {
			var otpTemp: String
			val smsBody = sms.body.split("\n").reversed()
			val bankName = if (smsBody.last().contains("بانک") || smsBody.last()
					.contains("بلو")
			) smsBody.last().trim() else return null
			for (line in smsBody) {
				if (recognizeOtpWords(line)) {
					if (line.contains(":")) {
						val splits = line.split(":")
						otpTemp = splits[splits.size - 1].trim()
						if (otpTemp.length <= 10 && otpTemp.isDigitsOnly()) return OtpCode(
							otpTemp, bankName, null, sms.date
						)
					} else {
						if (line.contains(" ")) {
							val splits = line.split(" ")
							otpTemp = splits[splits.size - 1].trim()
							if (otpTemp.length <= 10 && otpTemp.isDigitsOnly()) return OtpCode(
								otpTemp, bankName, null, sms.date
							)
						} else {
							otpTemp = line.trim()
							if (otpTemp.length <= 10 && otpTemp.isDigitsOnly()) return OtpCode(
								otpTemp, bankName, null,sms.date
							)
						}
					}
				}
			}
			return null
		}

		private fun getSmsList(context: Context, appSettings: App.Settings): List<OtpSms> {
			val otpSmsList = mutableListOf<OtpSms>()
			val resolver = context.contentResolver
			val cursor = resolver.query(
				Telephony.Sms.Inbox.CONTENT_URI,
				arrayOf("body", "date"),
				generateSelectionQuery(appSettings),
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

		fun getCodeList(context: Context, appSettings: App.Settings) =
			getSmsList(context, appSettings).mapNotNull {
				getOtpFromSms(it)
			}

		private fun recognizeOtpWords(
			otpSmsLine: String, newWords: String = recognitionWords
		): Boolean {
			recognitionWords = newWords
			for (otpWord in recognitionWords) {
				if (otpSmsLine.contains(otpWord)) return true
			}
			return false
		}

		private fun generateSelectionQuery(
			appSettings: App.Settings, column: String = "body", newWords: String = selectionWords
		): String {
			selectionWords = newWords
			val query = StringBuilder()
			val filterTime = System.currentTimeMillis() - appSettings.expireTime
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
