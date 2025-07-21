package ir.saltech.puyakhan.data.datastore

import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.toMutableStateList
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import ir.saltech.puyakhan.data.model.OtpCode

private const val OTP_STORAGE_PB_FILENAME = "otp_storage.pb"

private val Context.otpDataStore: DataStore<MutableList<OtpCode>> by dataStore(
	fileName = OTP_STORAGE_PB_FILENAME, serializer = OtpCodeSerializer(KeystoreManager())
)

class OtpDataStore(context: Context) {
	private val dataStore = context.otpDataStore

	fun getOtpCodes() = dataStore.data

	suspend fun setOtpCodes(otpCodes: MutableList<OtpCode>) {
		dataStore.updateData {
			otpCodes.toMutableStateList()
		}
	}

	suspend fun addOtpCode(otpCode: OtpCode) {
		dataStore.updateData { currentCodes ->
			(currentCodes + otpCode).toMutableStateList()
		}
	}

	suspend fun removeOtpCode(otpCode: OtpCode) {
		dataStore.updateData { currentCodes ->
			currentCodes.filterNot { it.id == otpCode.id }.toMutableStateList()
		}
	}
		}
	}

	suspend fun clearAll() {
		dataStore.updateData {
			mutableStateListOf()
		}
	}
}
