package ir.saltech.puyakhan.data.util

import android.content.Context
import android.util.Log
import androidx.compose.runtime.toMutableStateList
import ir.saltech.puyakhan.data.datastore.OtpDataStore
import ir.saltech.puyakhan.data.model.OtpCode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.regex.Pattern
import kotlin.math.abs

internal const val MAX_OTP_SMS_EXPIRATION_TIME = 120_000L
internal const val CLIPBOARD_OTP_CODE_KEY = "otp_code"

private const val TAG = "OtpProcessor"

object OtpProcessor {
	private data class FoundItem(val text: String, val index: Int) {
		fun findMinDistance(others: List<FoundItem>, default: Int): Int {
			return others.minOfOrNull { abs(it.index - this.index) } ?: default
		}
	}
	private data class ScoredCode(val code: String, val score: Int)

	object Actions {
		const val COPY_OTP_ACTION = "ir.saltech.puyakhan.COPY_OTP_ACTION"
	}

	private val WEAK_POSITIVE_KEYWORDS = listOf("کد", "code")

	private val PERSIAN_POSITIVE_KEYWORDS = listOf(
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

	private val ENGLISH_POSITIVE_KEYWORDS = listOf(
		"password", "otp", "verification", "passcode", "pin", "auth", "login",
		"access", "token", "security code", "authentication", "one-time", "2fa",
		"key", "secret", "credential", "2-step", "mfa", "validation", "one time password",
		"2-factor", "multi-factor", "confirmation code"
	)

	private val PERSIAN_NEGATIVE_KEYWORDS = listOf(
		"شارژ",
		"سفارش",
		"پیگیری",
		"مرسوله",
		"محصول",
		"کالا",
		"فاکتور",
		"شبا",
		"بارکد",
		"تخفیف",
		"ملی",
		"اقتصادی",
		"پشتیبانی",
		"قرعه کشی",
		"شناسه",
		"پستی",
		"قرعه\u200cکشی",
		"قرعه کشي",
		"مشتری",
		"پرواز",
		"رزرو",
		"بلیت",
		"صورتحساب",
		"اعتبار",
		"بسته",
		"حواله",
		"بارنامه",
		"قرارداد",
		"رهگیری",
		"پرداخت",
		"شماره مشتری",
		"کدملي",
		"ملي",
		"رهگیري"
	)
	private val ENGLISH_NEGATIVE_KEYWORDS = listOf(
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

	// Pre-compiled patterns for efficiency
	private val POSITIVE_KEYWORDS_PATTERN = buildKeywordsPattern(PERSIAN_POSITIVE_KEYWORDS + ENGLISH_POSITIVE_KEYWORDS + WEAK_POSITIVE_KEYWORDS)
	private val NEGATIVE_KEYWORDS_PATTERN = buildKeywordsPattern(PERSIAN_NEGATIVE_KEYWORDS + ENGLISH_NEGATIVE_KEYWORDS)
	private val NUMERIC_CODE_PATTERN = Pattern.compile("(?<!\\d)(\\d{4,8})(?!\\d)")
	private val BANK_NAME_PATTERN = Pattern.compile("(?:\\*\\s*)?بانک\\s+([^*\\n\\r]+)")
	private val OTHER_BANK_NAME_PATTERN = Pattern.compile("(?:\\*\\s*)?(?:بلو|ویپاد|زیپاد|بلو جونیور|اوانو)\\s+([^*\\n\\r]+)")
	private val COMMA_AMOUNT_PATTERN = Pattern.compile("\\b\\d{1,3}(?:,\\d{3})+\\b")
	private val PERSIAN_COMMA_AMOUNT_PATTERN = Pattern.compile("\\b\\d{1,3}(?:،\\d{3})+\\b")
	private val KEYWORD_AMOUNT_PATTERN = Pattern.compile(
		"(?:مبلغ|فی|\\bprice\\b|\\bamount\\b)\\s*:?\\s*([\\d,]+)",
		Pattern.CASE_INSENSITIVE
	)

	/**
	 * Builds a single, efficient regex pattern from a list of keywords.
	 */
	private fun buildKeywordsPattern(keywords: List<String>): Pattern {
		val patternString = keywords.joinToString("|") { keyword ->
			if (keyword.firstOrNull()?.isLetter() == true && keyword.first() <= 'z') {
				"\\b${Pattern.quote(keyword)}\\b" // Use word boundaries for English
			} else {
				Pattern.quote(keyword)
			}
		}
		return Pattern.compile(patternString, Pattern.CASE_INSENSITIVE)
	}

	/**
	 * Analyzes an SMS message to extract transaction details including OTP,
	 * bank name, and transaction amount.
	 *
	 * @param message The SMS content to parse.
	 * @return A [OtpCode] object containing the extracted data.
	 */
	suspend fun extractOtpInfo(
		context: Context,
		message: String,
		sendTime: Long = 0L,
		preferredExpireTime: Long = MAX_OTP_SMS_EXPIRATION_TIME,
	): OtpCode? {
		val otp = extractOtp(message)?.trim() ?: return null
		val bankName = extractBankName(message)?.trim()
		val amount = extractAmount(message)?.trim()

		val receivedOtp =
			OtpCode(
				id = kotlin.random.Random.nextInt(),
				bank = bankName,
				price = amount,
				otp = otp,
				sentTime = sendTime,
				expirationTime = preferredExpireTime
			)

		Log.d(TAG, "New OTP Code : $receivedOtp")
		OtpDataStore(context).addOtpCode(receivedOtp)

		return receivedOtp
	}

	fun getOtpCodes(context: Context): Flow<MutableList<OtpCode>> {
		val dataStore = OtpDataStore(context)
		return dataStore.getOtpCodes().map { codes ->
			codes.filter {
				System.currentTimeMillis() - it.sentTime < it.expirationTime
			}.toMutableStateList()
		}
	}

	suspend fun clearOtpCodes(context: Context) {
		val dataStore = OtpDataStore(context)
		dataStore.clearAll()
	}

	private fun extractAmount(message: String): String? {
		COMMA_AMOUNT_PATTERN.matcher(message).let { if (it.find()) return it.group(0)?.trim() }
		PERSIAN_COMMA_AMOUNT_PATTERN.matcher(message).let { if (it.find()) return it.group(0)?.trim() }
		KEYWORD_AMOUNT_PATTERN.matcher(message).let { if (it.find()) return it.group(1)?.trim() }
		return null
	}

	private fun extractBankName(message: String): String? {
		OTHER_BANK_NAME_PATTERN.matcher(message).let {
			if (it.find()) return it.group(0)?.trim()
		}
		BANK_NAME_PATTERN.matcher(message).let {
			if (it.find()) return it.group(1)?.trim()
		}
		return null
	}

	/**
	 * This function extracts a 4-to-8-digit OTP (One-Time Password) from a given message string.
	 * It uses an advanced contextual analysis algorithm.
	 *
	 * @param message The SMS or message content to parse.
	 * @return The extracted OTP as a String, or null if no valid OTP is found.
	 */
	private fun extractOtp(message: String): String? {
		val allNumericSequences = findMatches(message,
			NUMERIC_CODE_PATTERN
		)

		val candidates = allNumericSequences
			.filterNot { isLikelyDateOrTime(it, message) } // FIX: Use lambda to pass message
			.filterNot { isLikelyUssdCode(it, message) }   // FIX: Use lambda to pass message

		if (candidates.isEmpty()) return null


		// 3. Find all positive and negative keywords using the efficient pre-compiled patterns.
		val allPositiveKeywords = findMatches(message,
			POSITIVE_KEYWORDS_PATTERN
		)
		val allNegativeKeywords = findMatches(message,
			NEGATIVE_KEYWORDS_PATTERN
		)

		if (allPositiveKeywords.isEmpty()) return candidates.firstOrNull()?.text

		// 4. Score each candidate based on its proximity to the nearest positive and negative keywords.
		val scoredCandidates = candidates.map { code ->
			val distToPositive = code.findMinDistance(allPositiveKeywords, message.length)
			val distToNegative = code.findMinDistance(allNegativeKeywords, message.length)

			// The score is the difference in distances. A smaller (or more negative) score is better,
			// indicating it's much closer to a positive keyword than a negative one.
			val score = distToPositive - distToNegative
			ScoredCode(code.text, score)
		}

		// 5. Return the code with the best (lowest) score.
		return scoredCandidates.minByOrNull { it.score }?.code

//		val codeCandidates = allNumericSequences.filterNot { candidate ->
//			val indexAfterCode = candidate.index + candidate.text.length
//			if (indexAfterCode < message.length) {
//				val charAfter = message[indexAfterCode]
//				if (charAfter == '/' || charAfter == '-') return@filterNot true
//			}
//			val indexBeforeCode = candidate.index - 1
//			if (indexBeforeCode >= 0) {
//				val charBefore = message[indexBeforeCode]
//				if (charBefore == '/' || charBefore == '-' || charBefore == ':') return@filterNot true
//			}
//			false
//		}

//		if (codeCandidates.isEmpty()) {
//			return null
//		}
//
//		val positiveKeywords = findMatches(message, PERSIAN_POSITIVE_KEYWORDS + ENGLISH_POSITIVE_KEYWORDS)
//		val negativeKeywords = findKeywords(message, PERSIAN_NEGATIVE_KEYWORDS + ENGLISH_NEGATIVE_KEYWORDS)
//
//		val ambiguousKeywords = findKeywords(message, WEAK_POSITIVE_KEYWORDS)
//		val allNegativeKeywords = negativeKeywords + findAmbiguousNegative(
//			message,
//			ambiguousKeywords,
//			PERSIAN_NEGATIVE_KEYWORDS,
//			ENGLISH_NEGATIVE_KEYWORDS
//		)
//		val allPositiveKeywords = positiveKeywords + findAmbiguousPositive(
//			message,
//			ambiguousKeywords,
//			PERSIAN_NEGATIVE_KEYWORDS,
//			ENGLISH_NEGATIVE_KEYWORDS
//		)
//
//		if (allPositiveKeywords.isEmpty()) {
//			return null
//		}
//
//		// 4. Score each code candidate based on its nearest keyword
//		val scoredCandidates = codeCandidates.mapNotNull { code ->
//			val closestPositive = allPositiveKeywords.minByOrNull { abs(it.index - code.index) }
//			val closestNegative = allNegativeKeywords.minByOrNull { abs(it.index - code.index) }
//
//			val distToPositive =
//				closestPositive?.let { abs(it.index - code.index) } ?: Int.MAX_VALUE
//			val distToNegative =
//				closestNegative?.let { abs(it.index - code.index) } ?: Int.MAX_VALUE
//
//			if (distToPositive <= distToNegative) {
//				ScoredCode(code.text, distToPositive)
//			} else {
//				null
//			}
//		}
//
//		// 5. Return the valid code with the best (lowest distance) score
//		return scoredCandidates.minByOrNull { it.score }?.code
	}

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

	/**
	 * Checks if a found item is likely part of a date or time string.
	 * e.g., filters out the 01 in 2024/01/01
	 */
	private fun isLikelyDateOrTime(candidate: FoundItem, message: String): Boolean {
		val indexAfter = candidate.index + candidate.text.length
		if (indexAfter < message.length && (message[indexAfter] == '/' || message[indexAfter] == '-')) {
			return true
		}
		val indexBefore = candidate.index - 1
		if (indexBefore >= 0 && (message[indexBefore] == '/' || message[indexBefore] == '-')) {
			return true
		}
		return false
	}

	/**
	 * Checks if a found item is likely part of a USSD code.
	 * e.g., filters out the 9727 in *9727#
	 */
	private fun isLikelyUssdCode(candidate: FoundItem, message: String): Boolean {
		val indexBefore = candidate.index - 1
		if (indexBefore >= 0 && (message[indexBefore] == '*' || message[indexBefore] == '#')) {
			return true
		}
		val indexAfter = candidate.index + candidate.text.length
		if (indexAfter < message.length && (message[indexAfter] == '*' || message[indexAfter] == '#')) {
			return true
		}
		return false
	}


	/**
	 * A generic helper to find all matches for a given pattern in a message.
	 */
	private fun findMatches(message: String, pattern: Pattern): List<FoundItem> {
		val items = mutableListOf<FoundItem>()
		val matcher = pattern.matcher(message)
		while (matcher.find()) {
			val text = if (matcher.groupCount() >= 1) matcher.group(1) else matcher.group(0)
			if (text != null) {
				items.add(FoundItem(text, matcher.start()))
			}
		}
		return items
	}
}
