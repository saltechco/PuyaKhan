package ir.saltech.puyakhan.data.model

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import ir.saltech.puyakhan.data.util.MAX_OTP_SMS_EXPIRATION_TIME
import ir.saltech.puyakhan.data.util.dataStore
import ir.saltech.puyakhan.data.util.get
import ir.saltech.puyakhan.data.util.set

object App {
	enum class Page {
		Main, Settings
	}

	data class Settings(
		var presentMethods: Set<String> = mutableSetOf(PresentMethod.Otp.NOTIFY),
		var expireTime: Long = MAX_OTP_SMS_EXPIRATION_TIME,
		var otpWindowPos: WindowPosition? = null,
		@Deprecated("Privacy now added into SettingsView, so ignoring it.")
		var privacyAccepted: Boolean = false,
		var savedOtpCodesCount: Int = 0,
	)

	object Key {
		const val OTP_CODE_COPY_KEY = "otp_code_copy_key"
		val PresentMethod = stringSetPreferencesKey("present_method")
		val ExpireTime = longPreferencesKey("expire_time")
		val WindowPosition = stringSetPreferencesKey("window_position")
		val PrivacyAccepted = booleanPreferencesKey("privacy_accepted")
		val SavedOtpCodesCount = intPreferencesKey("saved_otp_codes_count")
	}

	data class WindowPosition(
		var x: Int, var y: Int
	) {
		companion object {
			fun fromStringSet(set: Set<String>): WindowPosition {
				return WindowPosition(set.elementAt(0).toInt(), set.elementAt(1).toInt())
			}
		}

		fun toStringSet(): Set<String> {
			return mutableSetOf(x.toString(), y.toString())
		}
	}

	sealed class PresentMethod {
		object Otp {
			const val COPY = "copy"
			const val NOTIFY = "notify"
			const val SELECT = "select"
		}
	}

	suspend fun getSettings(context: Context): Settings {
		return Settings(
			context.dataStore[Key.PresentMethod] ?: mutableSetOf(PresentMethod.Otp.NOTIFY),
			context.dataStore[Key.ExpireTime] ?: MAX_OTP_SMS_EXPIRATION_TIME,
			context.dataStore[Key.WindowPosition]?.let { WindowPosition.fromStringSet(it) },
			context.dataStore[Key.PrivacyAccepted] ?: false,
			context.dataStore[Key.SavedOtpCodesCount] ?: 0
		)
	}

	suspend fun setSettings(context: Context, settings: Settings) {
		context.dataStore[Key.PresentMethod] = settings.presentMethods
		context.dataStore[Key.ExpireTime] = settings.expireTime
		if (settings.otpWindowPos != null) context.dataStore[Key.WindowPosition] =
			settings.otpWindowPos!!.toStringSet()
		context.dataStore[Key.PrivacyAccepted] = settings.privacyAccepted
		context.dataStore[Key.SavedOtpCodesCount] = settings.savedOtpCodesCount
	}
}
