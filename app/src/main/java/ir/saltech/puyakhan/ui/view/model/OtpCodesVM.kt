package ir.saltech.puyakhan.ui.view.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ir.saltech.puyakhan.data.model.OtpCode

internal class OtpCodesVM : ViewModel() {

	private val _otpCodes = MutableLiveData<List<OtpCode>>()
	val otpCodes: LiveData<List<OtpCode>> = _otpCodes

	fun onOtpCodesChanged(otpCodes: List<OtpCode>) {
		_otpCodes.value = otpCodes
	}
}