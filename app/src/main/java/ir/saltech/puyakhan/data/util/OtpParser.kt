package ir.saltech.puyakhan.data.util

import java.util.regex.Pattern
import kotlin.compareTo
import kotlin.math.abs

object OtpParser {
	private data class FoundItem(val text: String, val index: Int) {
		fun findMinDistance(others: List<FoundItem>, default: Int): Int {
			return others.minOfOrNull { abs(it.index - this.index) } ?: default
		}
	}
	private data class ScoredCode(val code: String, val score: Int)

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

	// --------------------------

	fun extractAmount(message: String): String? {
		COMMA_AMOUNT_PATTERN.matcher(message).let { if (it.find()) return it.group(0)?.trim() }
		PERSIAN_COMMA_AMOUNT_PATTERN.matcher(message).let { if (it.find()) return it.group(0)?.trim() }
		KEYWORD_AMOUNT_PATTERN.matcher(message).let { if (it.find()) return it.group(1)?.trim() }
		return null
	}

	fun extractBankName(message: String): String? {
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
	fun extractOtp(message: String): String? {
		val allNumericSequences = findMatches(message,
			NUMERIC_CODE_PATTERN
		)

		val candidates = allNumericSequences
			.filterNot { isLikelyDateOrTime(it, message) }
			.filterNot { isLikelyUssdCode(it, message) }

		if (candidates.isEmpty()) return null


		val allPositiveKeywords = findMatches(message,
			POSITIVE_KEYWORDS_PATTERN
		)
		val allNegativeKeywords = findMatches(message,
			NEGATIVE_KEYWORDS_PATTERN
		)

		if (allPositiveKeywords.isEmpty()) return candidates.firstOrNull()?.text

		val scoredCandidates = candidates.map { code ->
			val distToPositive = code.findMinDistance(allPositiveKeywords, message.length)
			val distToNegative = code.findMinDistance(allNegativeKeywords, message.length)
			val score = distToPositive - distToNegative
			ScoredCode(code.text, score)
		}

		return scoredCandidates.minByOrNull { it.score }?.code
	}

	// --------------------------

	@Deprecated("Temporarily deprecated")
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

	@Deprecated("Temporarily deprecated")
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
		return indexBefore >= 0 && (message[indexBefore] == '/' || message[indexBefore] == '-')
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
		return indexAfter < message.length && (message[indexAfter] == '*' || message[indexAfter] == '#')
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