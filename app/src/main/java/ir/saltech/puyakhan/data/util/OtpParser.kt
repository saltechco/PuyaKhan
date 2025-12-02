package ir.saltech.puyakhan.data.util

import java.util.regex.Pattern
import kotlin.math.abs
import kotlin.math.min

object OtpParser {

    private data class FoundItem(val text: String, val index: Int)

    // ------------------------------------------------------------------------
    // CONFIGURATION
    // ------------------------------------------------------------------------

    private val POSITIVE_KEYWORDS = listOf(
        // Persian
        "کد", "رمز", "گذرواژه", "تایید", "تأیید", "ورود", "دسترسی",
        "احراز", "هویت", "یکبار", "یک‌بار", "امنیت", "کلید", "فعالسازی", "فعال‌سازی",
        "پویا", // Added for "رمز دوم پویا"
        // English
        "otp", "code", "pass", "pin", "token", "auth", "login", "key", "secret", "2fa",
        "verification", "access", "security", "credential", "password"
    )

    private val NEGATIVE_KEYWORDS = listOf(
        // Persian
        "واریز", "برداشت", "مانده", "موجودی", "سفارش", "پیگیری", "مرسوله", "محصول", "کالا",
        "فاکتور", "شبا", "کارت", "حساب", "تخفیف", "ملی", "شناسه", "پستی", "قرعه", "مشتری",
        "پرواز", "بلیت", "بلیط", "پرداخت", "قبض", "پرونده", "بارنامه", "رهگیری", "مبلغ",
        "ریال", "تومان", "هزینه", "شارژ", "بسته", "لغو", "اشتراک", "تراکنش", "پذیرنده",
        // English
        "order", "track", "promo", "discount", "invoice", "ticket", "flight", "bill",
        "pay", "ref", "shipment", "price", "cost", "amount", "id", "customer", "balance",
        "deposit", "withdrawal", "transaction", "validity"
    )

    // ------------------------------------------------------------------------
    // PATTERNS
    // ------------------------------------------------------------------------

    // Matches 4 to 8 digits.
    private val NUMERIC_CODE_PATTERN = Pattern.compile("(?<![0-9])([0-9]{4,8})(?![0-9])")

    private val POSITIVE_PATTERN = buildKeywordsPattern(POSITIVE_KEYWORDS)
    private val NEGATIVE_PATTERN = buildKeywordsPattern(NEGATIVE_KEYWORDS)

    // Helper patterns for other data extraction
    private val BANK_NAME_PATTERN = Pattern.compile("(?:\\*\\s*)?بانک\\s+([^*\\n\\r]+)")
    private val OTHER_BANK_NAME_PATTERN = Pattern.compile("(?:\\*\\s*)?(?:بلو|ویپاد|زیپاد|بلو جونیور|اوانو)\\s+([^*\\n\\r]+)")
    private val AMOUNT_PATTERN = Pattern.compile("(?:مبلغ|فی|price|amount|balance|موجودی)\\s*[:=]?\\s*([\\d,]+)", Pattern.CASE_INSENSITIVE)

    // ------------------------------------------------------------------------
    // THRESHOLDS
    // ------------------------------------------------------------------------

    private const val NEGATIVE_PROXIMITY_THRESHOLD = 20 // Chars
    private const val MAX_POSITIVE_DISTANCE = 70 // Chars
    private const val STRICT_POSITIVE_DISTANCE_FOR_ROUND_NUMBERS = 15 // Chars (for 1000, 2000...)

    // ------------------------------------------------------------------------
    // PUBLIC API
    // ------------------------------------------------------------------------

    fun extractOtp(rawMessage: String): String? {
        if (rawMessage.isBlank()) return null

        val normalizedMessage = normalizeMessage(rawMessage)

        // 1. Find numeric candidates
        val candidates = findMatches(normalizedMessage, NUMERIC_CODE_PATTERN, true)
            .filterNot { isLikelyDateOrTime(it, normalizedMessage) }
            .filterNot { isLikelyUssdCode(it, normalizedMessage) }
            .filterNot { isLikelyCurrency(it, normalizedMessage) }

        if (candidates.isEmpty()) return null

        // 2. Find keywords
        val positiveMatches = findMatches(normalizedMessage, POSITIVE_PATTERN, false)
        val negativeMatches = findMatches(normalizedMessage, NEGATIVE_PATTERN, false)

        if (positiveMatches.isEmpty()) return null

        // 3. Score and Select
        val bestCandidate = candidates.mapNotNull { candidate ->

            // A) Negative Check
            val closestNegative = candidate.findClosestItem(negativeMatches)
            if (closestNegative != null) {
                val dist = distanceBetween(candidate, closestNegative)
                // If very close to a negative keyword
                if (dist < NEGATIVE_PROXIMITY_THRESHOLD) {
                    // But ignore if they are in different sentences (split by . or \n)
                    if (!hasSentenceTerminator(normalizedMessage, candidate, closestNegative)) {
                        return@mapNotNull null
                    }
                }
            }

            // B) Positive Check
            val closestPositive = candidate.findClosestItem(positiveMatches) ?: return@mapNotNull null
            val distPositive = distanceBetween(candidate, closestPositive)

            // If too far from positive keyword
            if (distPositive > MAX_POSITIVE_DISTANCE) {
                return@mapNotNull null
            }

            // C) Round Number Check (e.g. 1000)
            if (isRoundNumber(candidate.text)) {
                // Must be very close to keywords like "Code" to be accepted
                if (distPositive > STRICT_POSITIVE_DISTANCE_FOR_ROUND_NUMBERS) {
                    return@mapNotNull null
                }
            }

            // D) Score (Lower distance is better)
            Pair(candidate, distPositive)
        }.minByOrNull { it.second }

        return bestCandidate?.first?.text
    }

    // --- Helper Extraction Methods ---

    fun extractAmount(message: String): String? {
        val normalized = normalizeMessage(message)
        AMOUNT_PATTERN.matcher(normalized).let {
            if (it.find()) return it.group(1)?.trim()
        }
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

    // ------------------------------------------------------------------------
    // INTERNAL LOGIC
    // ------------------------------------------------------------------------

    private fun normalizeMessage(message: String): String {
        var result = message.lowercase()
            .replace(Regex("[۰٠]"), "0").replace(Regex("[۱١]"), "1")
            .replace(Regex("[۲٢]"), "2").replace(Regex("[۳٣]"), "3")
            .replace(Regex("[۴٤]"), "4").replace(Regex("[۵٥]"), "5")
            .replace(Regex("[۶٦]"), "6").replace(Regex("[۷٧]"), "7")
            .replace(Regex("[۸٨]"), "8").replace(Regex("[۹٩]"), "9")
            .replace("ي", "ی").replace("ك", "ک")

        // Remove commas only if between digits (1,000 -> 1000)
        result = result.replace(Regex("(?<=\\d),(?=\\d)"), "")
        return result
    }

    private fun buildKeywordsPattern(keywords: List<String>): Pattern {
        val sortedKeywords = keywords.sortedByDescending { it.length }
        val patternString = sortedKeywords.joinToString("|") { Pattern.quote(it) }
        return Pattern.compile(patternString, Pattern.CASE_INSENSITIVE)
    }

    private fun findMatches(message: String, pattern: Pattern, isGroup1: Boolean): List<FoundItem> {
        val items = mutableListOf<FoundItem>()
        val matcher = pattern.matcher(message)
        while (matcher.find()) {
            val text = if (isGroup1 && matcher.groupCount() >= 1) matcher.group(1) else matcher.group(0)
            if (text != null) {
                items.add(FoundItem(text, matcher.start()))
            }
        }
        return items
    }

    private fun FoundItem.findClosestItem(others: List<FoundItem>): FoundItem? {
        if (others.isEmpty()) return null
        return others.minByOrNull { distanceBetween(this, it) }
    }

    private fun distanceBetween(item1: FoundItem, item2: FoundItem): Int {
        val start1 = item1.index
        val end1 = item1.index + item1.text.length
        val start2 = item2.index
        val end2 = item2.index + item2.text.length

        // Calculate distance between edges
        return maxOf(0, maxOf(start1, start2) - minOf(end1, end2))
    }

    /**
     * Checks if there is a sentence terminator (newline or dot) between two items.
     */
    private fun hasSentenceTerminator(message: String, item1: FoundItem, item2: FoundItem): Boolean {
        val start = minOf(item1.index + item1.text.length, item2.index + item2.text.length)
        val end = maxOf(item1.index, item2.index)

        if (start >= end) return false

        val substring = message.substring(start, end)
        return substring.contains("\n") || substring.contains(".") || substring.contains("\r")
    }

    // --- FILTERS ---

    private fun isLikelyDateOrTime(candidate: FoundItem, message: String): Boolean {
        val start = candidate.index
        val end = start + candidate.text.length
        val charBefore = message.getOrNull(start - 1)
        val charAfter = message.getOrNull(end)

        // Time: 12:30
        val isTime = charAfter == ':' || charBefore == ':'

        // Date: 2024/12/01 (Slash is a strong indicator)
        val isDateSlash = charBefore == '/' || charAfter == '/'

        return isTime || isDateSlash
    }

    private fun isLikelyUssdCode(candidate: FoundItem, message: String): Boolean {
        val start = candidate.index
        val end = start + candidate.text.length
        val charBefore = message.getOrNull(start - 1)
        val charAfter = message.getOrNull(end)
        return charBefore == '*' || charAfter == '#'
    }

    private fun isLikelyCurrency(candidate: FoundItem, message: String): Boolean {
        val end = candidate.index + candidate.text.length
        // Look ahead 15 chars
        val contextAfter = message.substring(end, minOf(message.length, end + 15)).trim()

        // Clean up leading special chars (like : or space)
        val cleanContext = contextAfter.replace(Regex("^[:\\s]+"), "")

        return cleanContext.startsWith("ریال") ||
                cleanContext.startsWith("تومان") ||
                cleanContext.startsWith("rial") ||
                cleanContext.startsWith("toman") ||
                cleanContext.startsWith("irr")
    }

    private fun isRoundNumber(text: String): Boolean {
        // Matches 1000, 500, 20000 (Starts with 1-9, rest 0)
        return text.matches(Regex("^[1-9]0+$"))
    }
}