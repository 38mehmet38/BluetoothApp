package com.mehmetbluetooth.ui

import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.mehmetbluetooth.R
import com.mehmetbluetooth.model.CallType
import com.mehmetbluetooth.model.User
import com.mehmetbluetooth.util.Constants

class CallScreenActivity : AppCompatActivity() {

    private lateinit var tvCallerName: TextView
    private lateinit var tvCallStatus: TextView
    private lateinit var btnAcceptCall: Button
    private lateinit var btnDeclineCall: Button
    private var currentUser: User? = null
    private var callType: CallType = CallType.VIDEO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_call_screen)

        // Get data from intent
        currentUser = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra(Constants.EXTRA_USER, User::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getSerializableExtra(Constants.EXTRA_USER) as? User
        }

        callType = when (intent.getIntExtra("call_type", 1)) {
            0 -> CallType.AUDIO
            1 -> CallType.VIDEO
            else -> CallType.VIDEO
        }

        tvCallerName = findViewById(R.id.tvCallerName)
        tvCallStatus = findViewById(R.id.tvCallStatus)
        btnAcceptCall = findViewById(R.id.btnAcceptCall)
        btnDeclineCall = findViewById(R.id.btnDeclineCall)

        setupUI()
        setupClickListeners()
    }

    private fun setupUI() {
        tvCallerName.text = currentUser?.name ?: "Bilinmeyen"
        tvCallStatus.text = if (callType == CallType.VIDEO) "Görüntülü Arıyor" else "Sesli Arıyor"
    }

    private fun setupClickListeners() {
        btnAcceptCall.setOnClickListener {
            acceptCall()
        }

        btnDeclineCall.setOnClickListener {
            declineCall()
        }
    }

    private fun acceptCall() {
        // Navigate to VideoCallActivity or AudioCallActivity
        val intent = if (callType == CallType.VIDEO) {
            android.content.Intent(this, VideoCallActivity::class.java)
        } else {
            android.content.Intent(this, VideoCallActivity::class.java) // For audio, can use same activity
        }
        intent.putExtra(Constants.EXTRA_USER, currentUser)
        intent.putExtra("call_type", callType.ordinal)
        startActivity(intent)
        finish()
    }

    private fun declineCall() {
        // Send decline message via Bluetooth
        finish()
    }
}
