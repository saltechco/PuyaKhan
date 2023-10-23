package ir.saltech.puyakhan.data.model

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import ir.saltech.puyakhan.data.util.dataStore
import ir.saltech.puyakhan.data.util.get
import ir.saltech.puyakhan.data.util.set
import ir.saltech.puyakhan.ui.view.component.manager.OTP_SMS_EXPIRATION_TIME

object App {
	enum class Page {
		Main, Settings
	}

	data class Settings(
		var presentMethods: Set<String> = mutableSetOf(PresentMethod.Otp.Notify),
		var expireTime: Long = OTP_SMS_EXPIRATION_TIME,
		var otpWindowPos: WindowPosition? = null,
		var disclaimerAccepted: Boolean = false
	)

	object Key {
		val PresentMethod = stringSetPreferencesKey("present_method")
		val ExpireTime = longPreferencesKey("expire_time")
		val WindowPosition = stringSetPreferencesKey("window_position")
		val DisclaimerAccepted = booleanPreferencesKey("disclaimer_accepted")
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
			const val Copy = "copy"
			const val Notify = "notify"
			const val Select = "select"
		}
	}

	fun getSettings(context: Context): Settings {
		return Settings(
			context.dataStore[Key.PresentMethod] ?: mutableSetOf(PresentMethod.Otp.Notify),
			context.dataStore[Key.ExpireTime] ?: OTP_SMS_EXPIRATION_TIME,
			context.dataStore[Key.WindowPosition]?.let { WindowPosition.fromStringSet(it) },
			context.dataStore[Key.DisclaimerAccepted] ?: false
		)
	}

	fun setSettings(context: Context, settings: Settings) {
		context.dataStore[Key.PresentMethod] = settings.presentMethods
		context.dataStore[Key.ExpireTime] = settings.expireTime
		if (settings.otpWindowPos != null) context.dataStore[Key.WindowPosition] =
			settings.otpWindowPos!!.toStringSet()
		context.dataStore[Key.DisclaimerAccepted] = settings.disclaimerAccepted
	}
}
