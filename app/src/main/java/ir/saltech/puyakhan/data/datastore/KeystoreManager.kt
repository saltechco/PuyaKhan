package ir.saltech.puyakhan.data.datastore

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

private const val KEYSTORE_ALIAS = "otp_key"
private const val GCM_IV_LENGTH = 12 // GCM recommended IV size
private const val GCM_TAG_LENGTH = 128

class KeystoreManager {
	private val keyStore = KeyStore.getInstance("AndroidKeyStore").apply {
		load(null)
	}

	private fun getKey(): SecretKey {
		val existingKey = keyStore.getEntry(KEYSTORE_ALIAS, null) as? KeyStore.SecretKeyEntry
		return existingKey?.secretKey ?: createKey()
	}

	private fun createKey(): SecretKey {
		val keyGenParameterSpec = KeyGenParameterSpec.Builder(
			KEYSTORE_ALIAS, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
		).setBlockModes(KeyProperties.BLOCK_MODE_GCM)
			.setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE).setKeySize(256).build()

		val keyGenerator = KeyGenerator.getInstance(
			KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore"
		)
		keyGenerator.init(keyGenParameterSpec)
		return keyGenerator.generateKey()
	}

	fun encrypt(data: ByteArray): ByteArray {
		val cipher = Cipher.getInstance("AES/GCM/NoPadding")
		cipher.init(Cipher.ENCRYPT_MODE, getKey())
		val iv = cipher.iv
		val encryptedData = cipher.doFinal(data)
		return iv + encryptedData
	}

	fun decrypt(encryptedData: ByteArray): ByteArray {
		val iv = encryptedData.copyOfRange(0, GCM_IV_LENGTH)
		val data = encryptedData.copyOfRange(GCM_IV_LENGTH, encryptedData.size)
		val cipher = Cipher.getInstance("AES/GCM/NoPadding")
		val spec = GCMParameterSpec(GCM_TAG_LENGTH, iv)
		cipher.init(Cipher.DECRYPT_MODE, getKey(), spec)
		return cipher.doFinal(data)
	}
}
