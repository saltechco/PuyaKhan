package ir.saltech.puyakhan.ui.view.model

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ir.saltech.puyakhan.data.model.App
import ir.saltech.puyakhan.data.model.OtpCode
import ir.saltech.puyakhan.data.util.MAX_OTP_SMS_EXPIRATION_TIME
import ir.saltech.puyakhan.data.util.OtpProcessor
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal class OtpCodesVM : ViewModel() {

	private val _otpCodes = MutableStateFlow(mutableStateListOf<OtpCode>())
	val otpCodes: StateFlow<MutableList<OtpCode>> = _otpCodes.asStateFlow()

	private fun startExpirationTimer() {
		viewModelScope.launch {
			while (true) {
				val currentTime = System.currentTimeMillis()
//				val updatedCodes = _otpCodes.value.filter { code ->
//					currentTime - code.sentTime < appSettings.expireTime
//				}.apply {
//					this.forEachIndexed { index, code ->
//						code.elapsedTime = currentTime - code.sentTime
////						otpCodes.value[index].elapsedTime = currentTime - code.sentTime
//					}
//				}
				val updatedCodes = _otpCodes.value.apply {
					this.forEachIndexed { index, code ->
						code.elapsedTime = currentTime - code.sentTime
					}
				}

				_otpCodes.update {
					updatedCodes.apply { if (all { it.elapsedTime >= MAX_OTP_SMS_EXPIRATION_TIME }) clear() }
						.toMutableStateList()
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
}