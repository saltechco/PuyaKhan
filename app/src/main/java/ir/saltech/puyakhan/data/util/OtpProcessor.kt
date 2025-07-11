package ir.saltech.puyakhan.data.util

import java.util.regex.Pattern
import kotlin.math.abs

private data class FoundItem(val text: String, val index: Int)
private data class ScoredCode(val code: String, val score: Int)

// --- Structured and Final Expanded Keyword Datasets for maximum accuracy ---
private val persianPositive = listOf(
	"رمز", "گذرواژه", "تأیید", "تایید", "فعال سازی", "فعالسازی", "ورود",
	"موقت", "دسترسی", "احراز هویت", "یکبار مصرف", "یک بار مصرف", "شناسایی", "امنیتی",
	"اعتبارسنجی", "اعتبار سنجی", "احراز", "کلید", "کد فعال‌سازی", "رمز یک‌بار مصرف", "کد امنیتی"
)

private val englishPositive = listOf(
	"password", "otp", "verification", "passcode", "pin", "auth", "login",
	"access", "token", "security code", "authentication", "one-time", "2fa",
	"key", "secret", "credential", "2-step", "mfa", "validation", "one time password",
	"2-factor", "multi-factor", "confirmation code"
)

val persianNegative = listOf(
	"سفارش", "پیگیری", "مرسوله", "محصول", "کالا", "فاکتور", "شبا", "بارکد",
	"تخفیف", "ملی", "اقتصادی", "پشتیبانی", "قرعه کشی", "شناسه", "پستی",
	"مشتری", "پرواز", "رزرو", "بلیت", "صورتحساب", "اعتبار", "بسته", "حواله",
	"بارنامه", "قرارداد", "رهگیری", "پرداخت", "شماره مشتری"
)
val englishNegative = listOf(
	"order", "tracking", "promo", "discount", "invoice", "support", "raffle",
	"ticket", "booking", "reference", "confirmation", "customer id", "flight", "shipment",
	"bill", "package", "credit", "contract", "id", "receipt", "order number", "tracking number",
	"zip code", "payment id", "reference number"
)

/**
 * Main data class to hold all extracted information from a financial SMS.
 */
data class TransactionInfo(
	val bankName: String? = null,
	val amount: String? = null,
	val otp: String? = null
)

// --- Main public function to extract all transaction info ---

/**
 * Analyzes an SMS message to extract transaction details including OTP,
 * bank name, and transaction amount.
 *
 * @param message The SMS content to parse.
 * @return A [TransactionInfo] object containing the extracted data.
 */
fun extractTransactionInfo(message: String): TransactionInfo {
	val otp = extractOtp(message)
	val bankName = extractBankName(message)
	val amount = extractAmount(message)

	return TransactionInfo(bankName = bankName, amount = amount, otp = otp)
}


// --- Private helper functions for specific data extraction ---

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
	val keywords = listOf("مبلغ", "فی", "\\bprice\\b", "\\bamount\\b").joinToString("|")
	val keywordAmountPattern = Pattern.compile("(?:$keywords)\\s*:?\\s*([\\d,]+)", Pattern.CASE_INSENSITIVE)
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
fun extractOtp(message: String): String? {
	// --- Structured and Final Expanded Keyword Datasets for maximum accuracy ---

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
	val ambiguousKeywords = findKeywords(message, listOf("کد", "code"))
	val allNegativeContextKeywords = negativeKeywords + findAmbiguousNegative(
		message,
		ambiguousKeywords,
		persianNegative,
		englishNegative
	)
	val allPositiveContextKeywords = positiveKeywords + findAmbiguousPositive(
		message,
		ambiguousKeywords,
		persianNegative,
		englishNegative
	)

	if (allPositiveContextKeywords.isEmpty()) {
		return null // No positive signal in the message
	}

	// 3. Score each code candidate based on its nearest keyword
	val scoredCandidates = codeCandidates.map { code ->
		val closestPositive = allPositiveContextKeywords.minByOrNull { abs(it.index - code.index) }
		val closestNegative = allNegativeContextKeywords.minByOrNull { abs(it.index - code.index) }

		val distToPositive = closestPositive?.let { abs(it.index - code.index) } ?: Int.MAX_VALUE
		val distToNegative = closestNegative?.let { abs(it.index - code.index) } ?: Int.MAX_VALUE

		// The code is valid only if its closest keyword is positive
		if (distToPositive <= distToNegative) {
			ScoredCode(code.text, distToPositive) // Lower distance (score) is better
		} else {
			ScoredCode(code.text, -1) // Invalid
		}
	}

	// 4. Return the valid code with the best (lowest distance) score
	return scoredCandidates.filter { it.score != -1 }.minByOrNull { it.score }?.code
}

private fun findItems(message: String, regex: String): MutableList<FoundItem> {
	val foundItems = mutableListOf<FoundItem>()
	Pattern.compile(regex, Pattern.CASE_INSENSITIVE).matcher(message)
		.results().forEach {
			foundItems.add(FoundItem(it.group(1), it.start(1)))
		}
	return foundItems
}

private fun findKeywords(message: String, keywords: List<String>): MutableList<FoundItem> {
	val found = mutableListOf<FoundItem>()
	keywords.forEach { keyword ->
		// Use regex for whole-word matching for English keywords
		val pattern = if (keyword.first().isLetter() && keyword.first() <= 'z') "\\b${
			Pattern.quote(
				keyword
			)
		}\\b" else Pattern.quote(keyword)
		Pattern.compile(pattern, Pattern.CASE_INSENSITIVE).matcher(message).results().forEach {
			found.add(FoundItem(keyword, it.start()))
		}
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
		val info = extractTransactionInfo(sms)
		println("--- Message ${index + 1} ---")
		println("Bank: ${info.bankName ?: "Not Found"}")
		println("Amount: ${info.amount ?: "Not Found"}")
		println("OTP: ${info.otp ?: "Not Found"}")
		println("--------------------")
	}
}
