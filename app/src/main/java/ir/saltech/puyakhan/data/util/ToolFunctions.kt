package ir.saltech.puyakhan.data.util

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.ui.unit.Dp
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import ir.saltech.puyakhan.R
import ir.saltech.puyakhan.data.model.OtpCode
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

private const val APPLICATION_DATASTORE = "app_data"

private const val DATASTORE_TAG = "APP_DATA_STORE"

internal val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = APPLICATION_DATASTORE)

internal suspend operator fun <T> DataStore<Preferences>.get(key: Preferences.Key<T>): T? {
	Log.w(DATASTORE_TAG, "Loading app settings key $key...")
	return data.map { preferences ->
		preferences[key]
	}.first()
}

internal suspend operator fun <T> DataStore<Preferences>.set(key: Preferences.Key<T>, value: T) {
	Log.w(DATASTORE_TAG, "Saving app settings key $key...")
	edit { preferences ->
		preferences[key] = value
	}
}

internal operator fun Int.div(dp: Dp): Float {
	return this / dp.value
}

internal operator fun Float.minus(dp: Dp): Float {
	return this - dp.value
}

internal infix fun Long.past(much: Long): Long {
	return this - much
}

internal infix operator fun Long.div(l: Long): Float {
	return (this.toDouble() / l.toDouble()).toFloat()
}

@OptIn(ExperimentalContracts::class)
inline fun repeatForever(action: () -> Unit) {
	contract { callsInPlace(action) }

	while (true) {
		action()
	}
}

private fun getShareOtpCodeText(context: Context, code: OtpCode): String {
	return when {
		code.bank != null && code.price != null -> context.getString(R.string.share_otp_code_text_from_bank_with_price, code.price, code.bank, code.otp)
		code.bank != null -> context.getString(R.string.share_otp_code_text_from_bank, code.bank, code.otp)
		code.price != null -> context.getString(R.string.share_otp_code_text_with_price, code.price, code.otp)
		else -> context.getString(R.string.share_otp_code_text, code.otp)
	}
}

internal fun shareSelectedOtpCode(context: Context, code: OtpCode) {
	val shareIntent = Intent(Intent.ACTION_SEND)
	shareIntent.type = "text/plain"
	shareIntent.putExtra(
		Intent.EXTRA_TEXT, getShareOtpCodeText(context, code)
	)
	context.startActivity(
		Intent.createChooser(
			shareIntent, context.getString(R.string.send_otp_to)
		).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
	)
}
