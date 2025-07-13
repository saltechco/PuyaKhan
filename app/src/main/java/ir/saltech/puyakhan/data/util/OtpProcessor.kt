package ir.saltech.puyakhan.data.util

import android.util.Log
import ir.saltech.puyakhan.data.model.App
import ir.saltech.puyakhan.data.model.OtpCode
import ir.saltech.puyakhan.data.util.OtpProcessor.Companion.appSettings
import ir.saltech.puyakhan.data.util.OtpProcessor.Companion.extractOtpInfo
import ir.saltech.puyakhan.data.util.OtpProcessor.Companion.receivedOtpQueue
import java.util.regex.Pattern
import kotlin.math.abs
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

internal const val OTP_SMS_EXPIRATION_TIME = 120_000L
internal const val CLIPBOARD_OTP_CODE = "otp_code"

internal class OtpProcessor {
	private data class FoundItem(val text: String, val index: Int)
	private data class ScoredCode(val code: String, val score: Int)

	object Actions {
		const val COPY_OTP_ACTION = "ir.saltech.puyakhan.COPY_OTP_ACTION"
	}

	companion object {

		val receivedOtpQueue: MutableList<OtpCode> = mutableListOf()

		private val ambiguousKeywords = listOf("کد", "code")

		private val persianPositive = listOf(
			"رمز",
			"گذرواژه",
			"تأیید",
			"تایید",
			"فعال سازی",
			"فعالسازی",
			"ورود",
			"موقت",
			"دسترسی",
			"احراز هویت",
			"یکبار مصرف",
			"یک بار مصرف",
			"شناسایی",
			"امنیتی",
			"اعتبارسنجی",
			"اعتبار سنجی",
			"احراز",
			"کلید",
			"کد فعال‌سازی",
			"رمز یک‌بار مصرف",
			"کد امنیتی"
		)

		private val englishPositive = listOf(
			"password", "otp", "verification", "passcode", "pin", "auth", "login",
			"access", "token", "security code", "authentication", "one-time", "2fa",
			"key", "secret", "credential", "2-step", "mfa", "validation", "one time password",
			"2-factor", "multi-factor", "confirmation code"
		)

		private val persianNegative = listOf(
			"سفارش", "پیگیری", "مرسوله", "محصول", "کالا", "فاکتور", "شبا", "بارکد",
			"تخفیف", "ملی", "اقتصادی", "پشتیبانی", "قرعه کشی", "شناسه", "پستی",
			"مشتری", "پرواز", "رزرو", "بلیت", "صورتحساب", "اعتبار", "بسته", "حواله",
			"بارنامه", "قرارداد", "رهگیری", "پرداخت", "شماره مشتری"
		)
		private val englishNegative = listOf(
			"order",
			"tracking",
			"promo",
			"discount",
			"invoice",
			"support",
			"raffle",
			"ticket",
			"booking",
			"reference",
			"confirmation",
			"customer id",
			"flight",
			"shipment",
			"bill",
			"package",
			"credit",
			"contract",
			"id",
			"receipt",
			"order number",
			"tracking number",
			"zip code",
			"payment id",
			"reference number"
		)

		private var appSettings: App.Settings? = null

		/**
		 * Analyzes an SMS message to extract transaction details including OTP,
		 * bank name, and transaction amount.
		 *
		 * @param message The SMS content to parse.
		 * @return A [OtpCode] object containing the extracted data.
		 */
		fun extractOtpInfo(message: String, sendTime: Long = 0L, settings: App.Settings? = null): OtpCode? {
			appSettings = settings;
			val otp = extractOtp(message)?.trim() ?: return null
			val bankName = extractBankName(message)?.trim()
			val amount = extractAmount(message)?.trim()

			val receivedOtp =
				OtpCode(bank = bankName, price = amount, otp = otp, sentTime = sendTime)
			removePreviousOTPs()
			receivedOtpQueue.add(receivedOtp)
			Log.i("TAG", "Currently OTP Codes : $receivedOtpQueue")
			return receivedOtp
		}

		@OptIn(ExperimentalTime::class)
		private fun removePreviousOTPs() {
			try {
				receivedOtpQueue.let {
					if (it.isEmpty()) return@let
					it.forEachIndexed { index, otp ->
						if ((otp.sentTime + (appSettings ?: return).expireTime) <= (System.currentTimeMillis() + 1000)) {
							it.removeAt(index)
						}
					}
				}
			} catch (e: ConcurrentModificationException) {
				e.printStackTrace()
			}
		}

		/**
		 * Extracts the transaction amount from the message.
		 * It primarily looks for numbers formatted with commas (e.g., 1,234,567),
		 * as this is a strong indicator of a monetary value, even without specific keywords.
		 * If not found, it falls back to looking for numbers after various amount-related keywords.
		 */
		private fun extractAmount(message: String): String? {
			// Strategy 1: Find numbers formatted with commas. This is the most reliable signal.
			val commaAmountPattern = Pattern.compile("\\b\\d{1,3}(?:,\\d{3})+\\b")
			val commaAmountMatcher = commaAmountPattern.matcher(message)
			if (commaAmountMatcher.find()) {
				return commaAmountMatcher.group(0) // group(0) is the whole match
			}

			// Strategy 2 (Fallback): Find numbers after various amount-related keywords.
			val amountKeywords = listOf("مبلغ", "فی", "\\bprice\\b", "\\bamount\\b").joinToString("|")
			val keywordAmountPattern =
				Pattern.compile("(?:$amountKeywords)\\s*:?\\s*([\\d,]+)", Pattern.CASE_INSENSITIVE)
			val keywordAmountMatcher = keywordAmountPattern.matcher(message)
			if (keywordAmountMatcher.find()) {
				return keywordAmountMatcher.group(1)
			}

			return null
		}

		/**
		 * Extracts the bank name from the message using a regex.
		 * Looks for the keyword "بانک" and captures the descriptive text following it.
		 */
		private fun extractBankName(message: String): String? {
			// This regex finds "بانک" (optionally surrounded by '*') and captures the text after it on the same line.
			val pattern = Pattern.compile("(?:\\*\\s*)?بانک\\s+([^*\\n\\r]+)")
			val matcher = pattern.matcher(message)
			return if (matcher.find()) {
				// Reconstruct the full name for a clean output
				"بانک " + matcher.group(1)?.trim()
			} else {
				null
			}
		}

		/**
		 * This function extracts a 4-to-8-digit OTP (One-Time Password) from a given message string.
		 * It uses an advanced contextual analysis algorithm.
		 *
		 * @param message The SMS or message content to parse.
		 * @return The extracted OTP as a String, or null if no valid OTP is found.
		 */
		private fun extractOtp(message: String): String? {

			// 1. Find all potential numeric codes in the message.
			val allNumericSequences = findItems(message, "(?<!\\d)(\\d{4,8})(?!\\d)")

			// 2. Post-filter to remove numbers that are clearly part of a date or time.
			val codeCandidates = allNumericSequences.filterNot { candidate ->
				val indexAfterCode = candidate.index + candidate.text.length
				if (indexAfterCode < message.length) {
					val charAfter = message[indexAfterCode]
					if (charAfter == '/' || charAfter == '-') return@filterNot true
				}
				val indexBeforeCode = candidate.index - 1
				if (indexBeforeCode >= 0) {
					val charBefore = message[indexBeforeCode]
					if (charBefore == '/' || charBefore == '-' || charBefore == ':') return@filterNot true
				}
				false
			}

			if (codeCandidates.isEmpty()) {
				return null
			}

			// 2. Find all positive and negative keywords
			val positiveKeywords = findKeywords(message, persianPositive + englishPositive)
			val negativeKeywords = findKeywords(message, persianNegative + englishNegative)

			// Handle ambiguous "کد" and "code"
			val ambiguousKeywords = findKeywords(message, ambiguousKeywords)
			val allNegativeKeywords = negativeKeywords + findAmbiguousNegative(
				message,
				ambiguousKeywords,
				persianNegative,
				englishNegative
			)
			val allPositiveKeywords = positiveKeywords + findAmbiguousPositive(
				message,
				ambiguousKeywords,
				persianNegative,
				englishNegative
			)

			if (allPositiveKeywords.isEmpty()) {
				return null
			}

			// 4. Score each code candidate based on its nearest keyword
			val scoredCandidates = codeCandidates.mapNotNull { code ->
				val closestPositive = allPositiveKeywords.minByOrNull { abs(it.index - code.index) }
				val closestNegative = allNegativeKeywords.minByOrNull { abs(it.index - code.index) }

				val distToPositive =
					closestPositive?.let { abs(it.index - code.index) } ?: Int.MAX_VALUE
				val distToNegative =
					closestNegative?.let { abs(it.index - code.index) } ?: Int.MAX_VALUE

				if (distToPositive <= distToNegative) {
					ScoredCode(code.text, distToPositive)
				} else {
					null
				}
			}

			// 5. Return the valid code with the best (lowest distance) score
			return scoredCandidates.minByOrNull { it.score }?.code
		}

		private fun findItems(message: String, pattern: String): MutableList<FoundItem> {
			val items = mutableListOf<FoundItem>()
			Pattern.compile(pattern, Pattern.CASE_INSENSITIVE and Pattern.MULTILINE)
				.matcher(message).results().forEach {
				val text = if (it.groupCount() > 0) it.group(1) else it.group(0)
				items.add(FoundItem(text, it.start()))
			}
			return items
		}

		private fun findKeywords(message: String, keywords: List<String>): MutableList<FoundItem> {
			val found = mutableListOf<FoundItem>()
			keywords.forEach { keyword ->
				val pattern = if (keyword.first().isLetter() && keyword.first() <= 'z') "\\b${
					Pattern.quote(
						keyword
					)
				}\\b" else Pattern.quote(keyword)
				found.addAll(findItems(message, pattern))
			}
			return found
		}

		// Helper to find ambiguous keywords that are likely negative
		private fun findAmbiguousNegative(
			message: String,
			ambiguous: List<FoundItem>,
			pNeg: List<String>,
			eNeg: List<String>,
		): List<FoundItem> {
			return ambiguous.filter {
				val contextAfter =
					message.substring(it.index, minOf(message.length, it.index + 25)).lowercase()
				val contextBefore = message.substring(maxOf(0, it.index - 25), it.index).lowercase()
				pNeg.any { neg -> contextAfter.contains(neg) } || eNeg.any { neg ->
					contextBefore.contains(
						neg
					)
				}
			}
		}

		// Helper to find ambiguous keywords that are likely positive
		private fun findAmbiguousPositive(
			message: String,
			ambiguous: List<FoundItem>,
			pNeg: List<String>,
			eNeg: List<String>,
		): List<FoundItem> {
			return ambiguous.filterNot {
				val contextAfter =
					message.substring(it.index, minOf(message.length, it.index + 25)).lowercase()
				val contextBefore = message.substring(maxOf(0, it.index - 25), it.index).lowercase()
				pNeg.any { neg -> contextAfter.contains(neg) } || eNeg.any { neg ->
					contextBefore.contains(
						neg
					)
				}
			}
		}
	}
}


// --- Example Usage ---
fun main() {
	val messages = listOf(
		"""
        *بانک قرض الحسنه مهر ايران*
        خريد
        سازمان پژوهش و برنامه ریزی آموزشی
        مبلغ 1,570,000 ريال
        رمز 505679
        زمان اعتبار رمز 20:12:16
        """.trimIndent(),
		"""
        محرمانه
        خرید
        نامبرلند
        1,930,000
        رمز34785
        1404/04/17-08:49:55
        """.trimIndent(),
		"کد تخفیف: 132811\nکد تأیید: 819911",
		"کد تایید شما: 123456",

		"رمز یکبار مصرف شما 9876 می باشد.",

		"Your access code is 778899",

		"556677 کد ورود شما به اپلیکیشن است.",

		"Your security code is 454545",

		"کد شناسایی شما 887766 است",

		"کد اعتبارسنجی: 112233",

		"Your 2FA code is 998877",


// --- Non-OTPs (should be ignored) ---

		"کد سفارش شما : 231122 از خریدتان متشکریم.",

		"کد پیگیری مرسوله: 565656",

		"Your tracking code is T12345B.",

		"کد پشتیبانی شما 102030 است.",

		"شناسه رزرو شما 998877 است.",

		"Your booking reference is 112233.",

		"شماره قرارداد شما 456789 است.",


// --- The tricky composite message (should now work correctly) ---

		"The order code: 12812211 the login code is : 818221",


// --- Multiple کد words (should now work correctly) ---

		"کد مرسوله: 123456 کد تخفیف: 789012",

		"کد سفارش: 111111 کد پیگیری: 222222",
	)

	println("--- Running Full Transaction Parser ---")
	messages.forEachIndexed { index, sms ->
		val info = extractOtpInfo(sms)
		println("--- Message ${index + 1} ---")
		println("Bank: ${info?.bank ?: "Not Found"}")
		println("Amount: ${info?.price ?: "Not Found"}")
		println("OTP: ${info?.otp ?: "Not Found"}")
		println("--------------------")
	}

	println("Received Queue: $receivedOtpQueue")
}
