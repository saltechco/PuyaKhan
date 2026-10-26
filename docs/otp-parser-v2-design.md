# PK-001 — OTP Parser V2 design proposal

Status: design only; no parser implementation has been merged.

## Observations in V1
1. `extractOtp` searches 4–8-digit numeric sequences and minimizes `distanceToPositive - distanceToNegative`; absolute distances, keywords spanning the whole SMS, and lack of a confidence gate lead to ambiguous picks.
2. `extractAmount` and `extractOtp` operate independently; amounts and reference numbers may be considered OTP candidates.
3. `extractOtp` requires a positive keyword. Nonstandard bank phrasing can yield false negatives.
4. `BANK_NAME_PATTERN` and `OTHER_BANK_NAME_PATTERN` should be regression-tested; their capture groups differ.
5. Inspect handling of Unicode numerals, ZWNJ, Persian/Arabic Yeh and Kaf, mixed separators, line breaks, URLs, and USSD text.

## Proposed pipeline
1. **Normalize** digits (ASCII/Persian/Arabic Indic to ASCII), whitespace, ZWNJ, punctuation, Persian/Arabic variants; preserve original offsets/value for UI.
2. **Candidate extraction**: bounded numerical tokens, configurable OTP length; attach span/line/neighbor text and sender metadata.
3. **Context classification**: mask/exclude phone numbers, dates, amounts, account/card/reference numbers, URLs/USSD. Local phrase matching near each candidate, preferably weighted by direction, line and word distance rather than absolute character distance.
4. **Templates**: optional versioned, sender-scoped, high-confidence patterns for *verified* bank message templates; generic fallback for unknown senders. Do not trust sender ID as authentication.
5. **Score & abstain**: independent positive/negative feature weights, hard exclusions where justified, bounded confidence plus required minimum margin between best and runner-up. Return sealed decision `Found / Ambiguous / NotFound` with reason tags; never auto-copy an ambiguous candidate.
6. **UX**: high-confidence auto-copy, medium-confidence choice UI, low-confidence no automatic extraction.
7. **Safety**: fully offline; avoid sensitive plaintext logging or third-party telemetry. Store only synthetic test fixtures in Git.

## Test matrix
- Persian, Arabic-Indic, Latin and mixed digits
- one OTP, OTP plus transaction amount, OTP plus receipt/reference
- ZWNJ and different spellings of رمز پویا/رمز یکبارمصرف
- punctuation, line breaks, formatted amounts, digit grouping
- false positives: order tracking, shipping, dates, phone numbers, IBAN/card/account
- false negatives: known bank formats without positive keywords
- several plausible OTP candidates / tie handling
- USSD with star/hash; URLs and links with numbers
- repeated or multipart SMS; Unicode transformations retaining offsets

## Evaluation
Keep labeled synthetic fixtures and sanitized volunteered samples. Report OTP precision, recall, false-auto-copy rate, ambiguous rate, and per-template regressions. Optimize safety of auto-copy before recall. Measure performance on low-end Android devices.

## Implementation sequence
A. Stabilize CI and test harness first.
B. Normalize and classify candidates (pure Kotlin API with deterministic tests).
C. Introduce confidence-based decision and integrate with caller/UI.
D. Add verified templates iteratively, not speculative bank-specific patterns.
E. Compare V1 and V2 offline on the same labeled dataset; report metrics.

## Release gates
No merge if unit tests fail; no production log of OTP/SMS; no distribution key in repository; no external SMS uploads; no automatic publish.
