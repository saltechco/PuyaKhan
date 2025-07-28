package ir.saltech.puyakhan

import android.app.Application
import android.content.Context
import android.os.Handler
import android.os.Parcelable
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import ir.saltech.puyakhan.data.util.MAX_OTP_SMS_EXPIRATION_TIME
import ir.saltech.puyakhan.data.util.dataStore
import ir.saltech.puyakhan.data.util.get
import ir.saltech.puyakhan.data.util.set
import kotlinx.parcelize.Parcelize

class ApplicationLoader : Application() {
	companion object {
		lateinit var applicationHandler: Handler
		lateinit var applicationContext: Context
		internal var isActivityLaunched = false
		private lateinit var applicationLoader: ApplicationLoader
	}

	override fun onCreate() {
		super.onCreate()
		applicationLoader = this
		Companion.applicationContext = applicationContext
		applicationHandler = Handler(applicationContext.mainLooper)
	}
}

object App {
	enum class Page {
		Main, Settings
	}

	@Parcelize
	data class Settings(
		var presentMethods: Set<String> = mutableSetOf(PresentMethod.Otp.NOTIFY),
		var expireTime: Long = MAX_OTP_SMS_EXPIRATION_TIME,
		var otpWindowPos: WindowPosition? = null,
		@Deprecated("Privacy now added into SettingsView, so ignoring it.")
		var privacyAccepted: Boolean = false,
	) : Parcelable

	object Key {
		const val OTP_CODE_INTENT = "otp_code_intent"
		const val OTP_ID_INTENT = "otp_id_intent"
		const val OTP_CODE_COPY = "otp_code_copy"
		val PresentMethod = stringSetPreferencesKey("present_method")
		val ExpireTime = longPreferencesKey("expire_time")
		val WindowPosition = stringSetPreferencesKey("window_position")
		val PrivacyAccepted = booleanPreferencesKey("privacy_accepted")
	}

	@Parcelize
	data class WindowPosition(
		var x: Int, var y: Int,
	) : Parcelable {
		companion object {
			fun fromStringSet(set: Set<String>): WindowPosition {
				return if (set.size != 2) {
					WindowPosition(0, 0)
				} else {
					WindowPosition(set.elementAt(0).toInt(), set.elementAt(1).toInt())
				}
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
			context.dataStore[Key.PrivacyAccepted] ?: false
		)
	}

	suspend fun setSettings(context: Context, settings: Settings) {
		context.dataStore[Key.PresentMethod] = settings.presentMethods
		context.dataStore[Key.ExpireTime] = settings.expireTime
		context.dataStore[Key.WindowPosition] = settings.otpWindowPos?.toStringSet() ?: setOf("0", "0")
		context.dataStore[Key.PrivacyAccepted] = settings.privacyAccepted
	}
}
