package com.mehmetbluetooth.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.mehmetbluetooth.R
import com.mehmetbluetooth.service.BluetoothMeshService
import com.mehmetbluetooth.util.PreferenceManager
import com.mehmetbluetooth.util.generateUUID

class OnboardingActivity : AppCompatActivity() {
    
    private lateinit var etUserName: EditText
    private lateinit var btnContinue: Button
    private lateinit var prefs: PreferenceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)
        
        try {
            prefs = PreferenceManager(this)
            
            // Kullanıcı zaten uygulamaya girmişse ana sayfaya git
            if (!prefs.isFirstLaunch()) {
                navigateToMain()
                return
            }
            
            etUserName = findViewById(R.id.etUserName)
            btnContinue = findViewById(R.id.btnContinue)
            
            btnContinue.setOnClickListener {
                handleContinueClick()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Başlatma hatası: ${e.message}", Toast.LENGTH_LONG).show()
            android.util.Log.e("OnboardingActivity", "onCreate error", e)
        }
    }

    private fun handleContinueClick() {
        try {
            val userName: String? = etUserName.text.toString().trim()
            
            if (userName.isNullOrEmpty()) {
                Toast.makeText(this, "Lütfen adınızı girin", Toast.LENGTH_SHORT).show()
                return
            }
            
            if (userName?.length ?: 0 < 2) {
                Toast.makeText(this, "Ad en az 2 karakter olmalıdır", Toast.LENGTH_SHORT).show()
                return
            }
            
            if (userName?.length ?: 0 > 20) {
                Toast.makeText(this, "Ad maksimum 20 karakter olmalıdır", Toast.LENGTH_SHORT).show()
                return
            }
            
            // Verileri kaydet
            val userId = generateUUID()
            prefs.setUserName(userName ?: "")
            prefs.setUserId(userId)
            prefs.setFirstLaunch(false)
            
            // Bluetooth Mesh Service'i başlat
            startBluetoothMeshService()
            
            // Ana sayfaya git
            navigateToMain()
        } catch (e: Exception) {
            Toast.makeText(this, "Hata: ${e.message}", Toast.LENGTH_LONG).show()
            android.util.Log.e("OnboardingActivity", "Continue click error", e)
        }
    }

    private fun startBluetoothMeshService() {
        try {
            val serviceIntent = Intent(this, BluetoothMeshService::class.java)
            startService(serviceIntent)
        } catch (e: Exception) {
            android.util.Log.e("OnboardingActivity", "Service start error", e)
        }
    }

    private fun navigateToMain() {
        try {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        } catch (e: Exception) {
            Toast.makeText(this, "MainActivity açılamadı: ${e.message}", Toast.LENGTH_LONG).show()
            android.util.Log.e("OnboardingActivity", "Navigation error", e)
        }
    }
}
