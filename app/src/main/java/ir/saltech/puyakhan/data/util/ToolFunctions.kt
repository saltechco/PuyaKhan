package ir.saltech.puyakhan.data.util

import android.content.Context
import android.util.Log
import androidx.compose.ui.unit.Dp
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

private const val APPLICATION_DATASTORE = "app_data"

private const val DATASTORE_TAG = "APP_DATA_STORE"

internal val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = APPLICATION_DATASTORE)

internal operator fun <T> DataStore<Preferences>.get(key: Preferences.Key<T>): T? {
	var dataFlow: T? = null
	runBlocking {
		launch {
			dataFlow = data.map { preferences ->
				preferences[key]
			}.first()
			Log.w(DATASTORE_TAG, "Loading app settings...")
		}
	}
	return dataFlow
}

internal operator fun <T> DataStore<Preferences>.set(key: Preferences.Key<T>, value: T) {
	runBlocking {
		launch {
			edit { preferences ->
				preferences[key] = value
			}
			Log.w(DATASTORE_TAG, "Saving app settings...")
		}
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
