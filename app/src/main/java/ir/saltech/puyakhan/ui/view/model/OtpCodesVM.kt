package ir.saltech.puyakhan.ui.view.model

import android.app.Application
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import ir.saltech.puyakhan.data.model.OtpCode
import ir.saltech.puyakhan.data.util.OtpProcessor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

internal class OtpCodesVM(application: Application) : AndroidViewModel(application) {

	private val _otpCodes = MutableStateFlow(mutableListOf<OtpCode>())
	val otpCodes: StateFlow<List<OtpCode>> = _otpCodes.asStateFlow()

	init {
		loadOtpCodes()
	}

	private fun loadOtpCodes() {
		viewModelScope.launch {
			OtpProcessor.getOtpCodes(getApplication())
				.map { codes ->
					codes.filter {
						System.currentTimeMillis() - it.sentTime < it.expirationTime
					}
				}
				.collect { codes ->
					_otpCodes.value = codes.toMutableStateList()
				}
		}
	}
}