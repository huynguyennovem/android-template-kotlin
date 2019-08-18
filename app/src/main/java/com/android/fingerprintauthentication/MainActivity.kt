package com.android.fingerprintauthentication

import android.Manifest
import android.annotation.SuppressLint
import android.app.KeyguardManager
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.fingerprint.FingerprintManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.getSystemService
import androidx.core.hardware.fingerprint.FingerprintManagerCompat
import java.security.AlgorithmParameterGenerator
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import androidx.core.content.ContextCompat.getSystemService
import android.os.Build


class MainActivity : AppCompatActivity() {

    lateinit var fingerprintManager: FingerprintManager
    lateinit var keyguardManager: KeyguardManager

    lateinit var keyStore: KeyStore
    lateinit var keyGenerator: KeyGenerator
    val KEY_NAME = "my_key"

    lateinit var cipher: Cipher
    lateinit var cryptoObject: FingerprintManager.CryptoObject

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // check support fingerprint authen
        // Check if we're running on Android 6.0 (M) or higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //Fingerprint API only available on from Android 6.0 (M)
            val fingerprintManager = getSystemService(Context.FINGERPRINT_SERVICE) as FingerprintManager
            if (!fingerprintManager.isHardwareDetected) {
                // Device doesn't support fingerprint authentication
                Toast.makeText(this, "Device doesn't support fingerprint authentication", Toast.LENGTH_SHORT).show()
            } else if (!fingerprintManager.hasEnrolledFingerprints()) {
                // User hasn't enrolled any fingerprints to authenticate with
                Toast.makeText(this, "User hasn't enrolled any fingerprints to authenticate with", Toast.LENGTH_SHORT)
                    .show()
            } else {
                // Everything is ready for fingerprint authentication
                Toast.makeText(this, "Everything is ready for fingerprint authentication", Toast.LENGTH_SHORT).show()
            }
        }

        if (checkLockScreen()) {
            generateKey()
            if (initCipher()) {
                cipher.let {
                    cryptoObject = FingerprintManager.CryptoObject(it)
                }
                val helper = FingerprintHelper(this)
                if (fingerprintManager != null && cryptoObject != null) {
                    helper.startAuth(fingerprintManager, cryptoObject)
                }
            }
        }
    }

    fun checkLockScreen(): Boolean {
        fingerprintManager = getSystemService(Context.FINGERPRINT_SERVICE) as FingerprintManager
        keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager

        if (!keyguardManager.isKeyguardSecure) {
            Toast.makeText(
                this,
                "Lock screen security not enabled",
                Toast.LENGTH_LONG
            ).show()
            return false
        }
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.USE_BIOMETRIC
            ) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            Toast.makeText(
                this,
                "Permission not enabled (Biometric)",
                Toast.LENGTH_LONG
            ).show()
            return false
        }
        if (fingerprintManager.hasEnrolledFingerprints() == false) {
            Toast.makeText(
                this,
                "No fingerprint registered, please register",
                Toast.LENGTH_LONG
            ).show()
            return false
        }
        return true
    }

    fun generateKey() {
        keyStore = KeyStore.getInstance("AndroidKeyStore")
        keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")
        keyStore.load(null)
        keyGenerator.init(
            KeyGenParameterSpec.Builder(KEY_NAME, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
                .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                .setUserAuthenticationRequired(true)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                .build()
        )
        keyGenerator.generateKey()
    }

    fun initCipher(): Boolean {
        cipher = Cipher.getInstance(
            KeyProperties.KEY_ALGORITHM_AES + "/"
                    + KeyProperties.BLOCK_MODE_CBC + "/"
                    + KeyProperties.ENCRYPTION_PADDING_PKCS7
        )
        keyStore.load(null)
        val key = keyStore.getKey(KEY_NAME, null) as SecretKey
        cipher.init(Cipher.ENCRYPT_MODE, key)
        return true
    }
}
