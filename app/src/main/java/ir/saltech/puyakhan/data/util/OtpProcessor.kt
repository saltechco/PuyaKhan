package ir.saltech.puyakhan.data.util

import java.util.regex.Pattern
import kotlin.math.abs

private data class FoundItem(val text: String, val index: Int)
private data class ScoredCode(val code: String, val score: Int)

// --- Structured and Final Expanded Keyword Datasets for maximum accuracy ---
private val persianPositive = listOf(
	"رمز", "گذرواژه", "تأیید", "تایید", "فعال سازی", "فعالسازی", "ورود",
	"موقت", "دسترسی", "احراز هویت", "یکبار مصرف", "یک بار مصرف", "شناسایی", "امنیتی",
	"اعتبارسنجی", "احراز", "کلید", "کد فعال‌سازی", "رمز یک‌بار مصرف", "کد امنیتی"
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
	"order", "tracking", "promo", "discount", "invoice", "support", "raffle",
	"ticket", "booking", "reference", "confirmation", "customer id", "flight", "shipment",
	"bill", "package", "credit", "contract", "id", "receipt", "order number", "tracking number",
	"zip code", "payment id", "reference number"
)

/**
 * This function extracts a 4-to-8-digit OTP (One-Time Password) from a given message string.
 * It uses an advanced contextual analysis algorithm. Instead of a simple regex, it finds all
 * potential codes and all keywords, then intelligently associates each code with its nearest
 * keyword to determine its validity. This approach accurately handles complex messages
 * containing multiple different types of codes.
 *
 * @param message The SMS or message content to parse.
 * @return The extracted OTP as a String, or null if no valid OTP is found.
 */
fun extractOtp(message: String): String? {

	// 1. Find all potential numeric codes in the message
	val codeCandidates = mutableListOf<FoundItem>()
	Pattern.compile("\\b(\\d{4,8})\\b").matcher(message).results().forEach {
		codeCandidates.add(FoundItem(it.group(1), it.start(1)))
	}

	if (codeCandidates.isEmpty()) {
		return null
	}

	// 2. Find all positive and negative keywords
	val positiveKeywords = findKeywords(message, persianPositive + englishPositive)
	val negativeKeywords = findKeywords(message, persianNegative + englishNegative)

	// Handle ambiguous "کد" and "code"
	val ambiguousKeywords = findKeywords(message, listOf("کد", "code"))
	val allNegativeContextKeywords = negativeKeywords + findAmbiguousNegative(message, ambiguousKeywords, persianNegative, englishNegative)
	val allPositiveContextKeywords = positiveKeywords + findAmbiguousPositive(message, ambiguousKeywords, persianNegative, englishNegative)

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

// Helper function to find all occurrences of a list of keywords
private fun findKeywords(message: String, keywords: List<String>): List<FoundItem> {
	val found = mutableListOf<FoundItem>()
	keywords.forEach { keyword ->
		// Use regex for whole-word matching for English keywords
		val pattern = if (keyword.first().isLetter() && keyword.first() <= 'z') "\\b${Pattern.quote(keyword)}\\b" else Pattern.quote(keyword)
		Pattern.compile(pattern, Pattern.CASE_INSENSITIVE).matcher(message).results().forEach {
			found.add(FoundItem(keyword, it.start()))
		}
	}
	return found
}

// Helper to find ambiguous keywords that are likely negative
private fun findAmbiguousNegative(message: String, ambiguous: List<FoundItem>, pNeg: List<String>, eNeg: List<String>): List<FoundItem> {
	return ambiguous.filter {
		val contextAfter = message.substring(it.index, minOf(message.length, it.index + 25)).lowercase()
		val contextBefore = message.substring(maxOf(0, it.index - 25), it.index).lowercase()
		pNeg.any { neg -> contextAfter.contains(neg) } || eNeg.any { neg -> contextBefore.contains(neg) }
	}
}

// Helper to find ambiguous keywords that are likely positive
private fun findAmbiguousPositive(message: String, ambiguous: List<FoundItem>, pNeg: List<String>, eNeg: List<String>): List<FoundItem> {
	return ambiguous.filterNot {
		val contextAfter = message.substring(it.index, minOf(message.length, it.index + 25)).lowercase()
		val contextBefore = message.substring(maxOf(0, it.index - 25), it.index).lowercase()
		pNeg.any { neg -> contextAfter.contains(neg) } || eNeg.any { neg -> contextBefore.contains(neg) }
	}
}


// --- Example Usage ---
fun main() {
    val messages = listOf(
        // --- Standard OTPs (should be found) ---
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
        "کد تخفیف: 132811\nکد تأیید: 819911",

        // --- New failing case (should now work correctly) ---
        "محرمانه\nخرید\nنامبرلند\n1,930,000\nرمز34195\n1404/04/17-08:49:55"
    )

	println("--- Running Rewritten Advanced Logic ---")
	for (sms in messages) {
		val otp = extractOtp(sms)
		println("Message: \"$sms\" -> OTP: ${otp ?: "Not Found"}")
	}
}
