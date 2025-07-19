package ir.saltech.puyakhan.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import ir.saltech.puyakhan.data.model.OtpCode

private val Context.otpDataStore: DataStore<List<OtpCode>> by dataStore(
    fileName = "otp_codes.pb",
    serializer = OtpCodeSerializer(KeystoreManager())
)

class OtpDataStore(context: Context) {
    private val dataStore = context.otpDataStore

    fun getOtpCodes() = dataStore.data

    suspend fun addOtpCode(otpCode: OtpCode) {
        dataStore.updateData { currentCodes ->
            currentCodes.toMutableList().apply {
                add(otpCode)
            }
        }
    }

    suspend fun removeOtpCode(otpCode: OtpCode) {
        dataStore.updateData { currentCodes ->
            currentCodes.toMutableList().apply {
                remove(otpCode)
            }
        }
    }

    suspend fun clearAll() {
        dataStore.updateData {
            emptyList()
        }
    }
}
