package ir.saltech.puyakhan.data.util

import android.content.Context
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

private const val APPLICATION_DATASTORE = "app_data"

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = APPLICATION_DATASTORE)

operator fun <T> DataStore<Preferences>.get(key: Preferences.Key<T>): T? {
	var dataFlow: T? = null
	runBlocking {
		launch {
			dataFlow = data.map { preferences ->
				preferences[key]
			}.first()
			Log.w("TAG", "Loading app settings...")
		}
	}
	return dataFlow
}

operator fun <T> DataStore<Preferences>.set(key: Preferences.Key<T>, value: T) {
	runBlocking {
		launch {
			edit { preferences ->
				preferences[key] = value
			}
			Log.w("TAG", "Setting app settings...")
		}
	}
}

fun until(end: Long, start: Long = System.currentTimeMillis(), action: (Long) -> Unit) {
	runBlocking {
		launch {
			repeat((end - start).toInt()) {
				action(start + it.toLong())
				delay(1000)
			}
		}
	}
}

operator fun Int.div(dp: Dp): Float {
	return this / dp.value
}

operator fun Float.minus(dp: Dp): Float {
	return this - dp.value
}

infix fun Long.past(much: Long): Long {
	return this - much
}

infix operator fun Long.div(l: Long): Float {
	return (this.toDouble() / l.toDouble()).toFloat()
}

@Composable
fun LockedDirection(direction: LayoutDirection = LayoutDirection.Ltr, content: @Composable () -> Unit) {
	CompositionLocalProvider(LocalLayoutDirection provides direction) {
		content()
	}
}

