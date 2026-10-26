package ir.saltech.puyakhan.data.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class OtpParserTest {
    @Test fun extractsSimplePersianOtp() {
        assertEquals("483921", OtpParser.extractOtp("رمز یکبار مصرف شما: 483921"))
    }

    @Test fun extractsSimpleEnglishOtp() {
        assertEquals("583241", OtpParser.extractOtp("Your verification code is 583241"))
    }

    @Test fun refusesUnrelatedMessageWithoutOtpContext() {
        assertNull(OtpParser.extractOtp("شماره پیگیری سفارش 583241 است"))
    }

    @Test fun prefersOtpOverTransactionAmount() {
        assertEquals(
            "583241",
            OtpParser.extractOtp("مبلغ پرداخت: 250000 تومان\nرمز پویا: 583241")
        )
    }

    @Test fun supportsPersianDigits() {
        assertEquals("۵۸۳۲۴۱", OtpParser.extractOtp("رمز یکبار مصرف: ۵۸۳۲۴۱"))
    }

    @Test fun supportsArabicIndicDigits() {
        assertEquals("٥٨٣٢٤١", OtpParser.extractOtp("رمز یکبار مصرف: ٥٨٣٢٤١"))
    }

    @Test fun doesNotTreatUssdAsOtp() {
        assertNull(OtpParser.extractOtp("کد دستوری *12345# را شماره‌گیری کنید"))
    }

    @Test fun extractsCommaFormattedAmount() {
        assertEquals("1,250,000", OtpParser.extractAmount("مبلغ: 1,250,000 ریال"))
    }
}
