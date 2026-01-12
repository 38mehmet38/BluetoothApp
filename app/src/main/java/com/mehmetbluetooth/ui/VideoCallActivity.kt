package com.mehmetbluetooth.ui

import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import android.view.TextureView
import com.mehmetbluetooth.R
import com.mehmetbluetooth.model.CallType
import com.mehmetbluetooth.model.User
import com.mehmetbluetooth.util.Constants

class VideoCallActivity : AppCompatActivity() {

    private lateinit var tvRemoteVideo: TextureView
    private lateinit var tvLocalVideo: TextureView
    private lateinit var btnMute: Button
    private lateinit var btnCamera: Button
    private lateinit var btnEndCall: Button
    
    private var currentUser: User? = null
    private var callType: CallType = CallType.VIDEO
    private var isMuted = false
    private var isCameraOn = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_call)

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

        tvRemoteVideo = findViewById(R.id.tvRemoteVideo)
        tvLocalVideo = findViewById(R.id.tvLocalVideo)
        btnMute = findViewById(R.id.btnMute)
        btnCamera = findViewById(R.id.btnCamera)
        btnEndCall = findViewById(R.id.btnEndCall)

        setupUI()
        setupClickListeners()
        initializeCamera()
    }

    private fun setupUI() {
        // Hide camera view if audio call
        if (callType == CallType.AUDIO) {
            tvLocalVideo.visibility = android.view.View.GONE
            tvRemoteVideo.visibility = android.view.View.GONE
        }
    }

    private fun setupClickListeners() {
        btnMute.setOnClickListener {
            toggleMute()
        }

        btnCamera.setOnClickListener {
            toggleCamera()
        }

        btnEndCall.setOnClickListener {
            endCall()
        }
    }

    private fun initializeCamera() {
        // Initialize camera for video call
        Toast.makeText(this, "Görüşme Başladı", Toast.LENGTH_SHORT).show()
    }

    private fun toggleMute() {
        isMuted = !isMuted
        btnMute.text = if (isMuted) "🔇" else "🔊"
        Toast.makeText(this, if (isMuted) "Mikrofon Kapalı" else "Mikrofon Açık", Toast.LENGTH_SHORT).show()
    }

    private fun toggleCamera() {
        isCameraOn = !isCameraOn
        btnCamera.text = if (isCameraOn) "📷" else "📹"
        Toast.makeText(this, if (isCameraOn) "Kamera Açık" else "Kamera Kapalı", Toast.LENGTH_SHORT).show()
    }

    private fun endCall() {
        // Send end call message and finish
        Toast.makeText(this, "Görüşme Sona Erdi", Toast.LENGTH_SHORT).show()
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        // Cleanup camera and audio resources
    }
}
