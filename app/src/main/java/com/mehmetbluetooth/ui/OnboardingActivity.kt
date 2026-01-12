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
    }

    private fun handleContinueClick() {
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
    }

    private fun startBluetoothMeshService() {
        val serviceIntent = Intent(this, BluetoothMeshService::class.java)
        startService(serviceIntent)
    }

    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}
