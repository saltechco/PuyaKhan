package ir.saltech.puyakhan.ui.view.model

import android.app.Application
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import ir.saltech.puyakhan.data.model.App
import ir.saltech.puyakhan.data.model.OtpCode
import ir.saltech.puyakhan.data.util.MAX_OTP_SMS_EXPIRATION_TIME
import ir.saltech.puyakhan.data.util.OtpProcessor
import ir.saltech.puyakhan.data.util.repeatForever
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal class OtpCodesVM(application: Application) : AndroidViewModel(application) {
	private val _otpCodes = MutableStateFlow(mutableStateListOf<OtpCode>())
	val otpCodes: StateFlow<MutableList<OtpCode>> = _otpCodes.asStateFlow()
	var appSettings: App.Settings? = null

	init {
		loadAppSettings()
	}

	private fun startExpirationTimer() {
		viewModelScope.launch {
			repeatForever {
				val currentTime = System.currentTimeMillis()
				_otpCodes.update {
					it.apply {
						forEach { code ->
							code.elapsedTime = currentTime - code.sentTime
						}
						if (all { code -> code.elapsedTime >= MAX_OTP_SMS_EXPIRATION_TIME }) clear()
					}.toMutableStateList()
				}
				delay(1000)
			}
		}
	}

	fun setOtpListener() {
		viewModelScope.launch {
			OtpProcessor.setListener(object : OtpProcessor.OtpReceivedListener {
				override fun onReceived(
					otp: OtpCode,
				) {
					Log.i("OtpCodesVM", "onReceived -> setOtpListener")
					_otpCodes.update { _otpCodes.value.apply { add(otp) } }
					Log.i(
						"OtpCodesVM",
						"onReceived -> setOtpListener -> otpCode: ${_otpCodes.value}"
					)
				}
			})
			startExpirationTimer()
		}
	}

	fun loadPreviousOtpCodes() {
		viewModelScope.launch {
			_otpCodes.update { OtpProcessor.otpCodesList.toMutableStateList() }
			Log.i("OtpCodesVM", "loadPreviousOtpCodes -> codes $_otpCodes")
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