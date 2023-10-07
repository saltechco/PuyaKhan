package ir.saltech.puyakhan.data.model

import android.content.Context
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import ir.saltech.puyakhan.data.util.dataStore
import ir.saltech.puyakhan.data.util.get
import ir.saltech.puyakhan.data.util.set
import ir.saltech.puyakhan.ui.view.components.manager.OTP_SMS_EXPIRATION_TIME

object App {
	data class Settings(
		val presentMethod: Int = PresentMethod.Otp.Notify,
		val expireTime: Long = OTP_SMS_EXPIRATION_TIME,
	)

	object Keys {
		val presentMethod = intPreferencesKey("present_method")
		val expireTime = longPreferencesKey("expire_time")
	}

	fun getSettings(context: Context): Settings {
		return Settings(
			context.dataStore[Keys.presentMethod] ?: PresentMethod.Otp.Notify,
			context.dataStore[Keys.expireTime] ?: OTP_SMS_EXPIRATION_TIME
		)
	}

	fun setSettings(context: Context, settings: Settings = Settings()) {
		context.dataStore[Keys.presentMethod] = settings.presentMethod
		context.dataStore[Keys.expireTime] = settings.expireTime
	}
}

sealed class PresentMethod {
	object Otp {
		val Copy: Int = 0
		val Notify: Int = 1
		val Select: Int = 2
		val CopyNotify: Int = 3
	}
}
