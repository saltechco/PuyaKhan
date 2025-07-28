package ir.saltech.puyakhan.ui.view.model

import android.app.Application
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import ir.saltech.puyakhan.App
import ir.saltech.puyakhan.data.model.OtpCode
import ir.saltech.puyakhan.data.util.MAX_OTP_SMS_EXPIRATION_TIME
import ir.saltech.puyakhan.data.util.OtpProcessor
import ir.saltech.puyakhan.data.util.past
import ir.saltech.puyakhan.data.util.repeatWhile
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

private const val TAG = "OtpCodesVM"

internal class OtpCodesVM(application: Application) : AndroidViewModel(application) {
	private val _otpCodes = MutableStateFlow(mutableStateListOf<OtpCode>())
	val otpCodes: StateFlow<MutableList<OtpCode>> = _otpCodes.asStateFlow()
	var appSettings: App.Settings? = null
	var initProgressShow = true

	init {
		loadAppSettings()
	}

	init {
		loadOtpCodes()
		setTimeElapsedCounter()
	}

	private fun loadOtpCodes() {
		viewModelScope.launch {
			OtpProcessor.getOtpCodes(getApplication()).collect { newCodes ->
				_otpCodes.update { currentCodes ->
					val currentIds = currentCodes.map { code -> code.id }.toSet()
					val codesToAdd = newCodes.filter { newCode -> newCode.id !in currentIds }

					if (codesToAdd.isNotEmpty()) {
						(currentCodes + codesToAdd).toMutableStateList()
					} else {
						currentCodes
					}
				}
			}
		}
	}

	private fun setTimeElapsedCounter() {
		viewModelScope.launch {
			repeatWhile(isActive) {
				val currentTime = System.currentTimeMillis()
				_otpCodes.update {
					it.apply {
						forEach { code ->
							code.elapsedTime = currentTime past code.sms.sentTime
							Log.d(TAG, "elapsedTime is ${code.elapsedTime} | ${code.elapsedTime > 1L}")
						}
						if (all { code -> code.elapsedTime >= MAX_OTP_SMS_EXPIRATION_TIME }) {
							clear()
							launch {
								try {
									OtpProcessor.clearOtpCodes(getApplication())
								} catch (e: Exception) {
									Log.e(TAG, "Failed to clear expired OTP codes.", e)
								}
							}
						}
					}.toMutableStateList()
				}
				delay(1000)
			}
		}
	}

	fun loadAppSettings() {
		viewModelScope.launch {
			appSettings = App.getSettings(getApplication())
		}
	}

	fun saveAppSettings() {
		viewModelScope.launch {
			App.setSettings(getApplication(), appSettings ?: return@launch)
		}
	}
}