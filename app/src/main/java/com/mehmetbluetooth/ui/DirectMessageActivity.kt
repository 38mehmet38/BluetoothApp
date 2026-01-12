package com.mehmetbluetooth.ui

import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mehmetbluetooth.R
import com.mehmetbluetooth.adapter.MessageAdapter
import com.mehmetbluetooth.model.Message
import com.mehmetbluetooth.model.User
import com.mehmetbluetooth.util.Constants
import com.mehmetbluetooth.util.PreferenceManager

class DirectMessageActivity : AppCompatActivity() {

    private lateinit var toolbar: Toolbar
    private lateinit var rvDMMessages: RecyclerView
    private lateinit var etDMMessage: EditText
    private lateinit var btnDMSend: Button
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var prefs: PreferenceManager
    private var currentUser: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_direct_message)

        prefs = PreferenceManager(this)

        // Get the recipient user from intent
        currentUser = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra(Constants.EXTRA_USER, User::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getSerializableExtra(Constants.EXTRA_USER) as? User
        }

        toolbar = findViewById(R.id.toolbar)
        rvDMMessages = findViewById(R.id.rvDMMessages)
        etDMMessage = findViewById(R.id.etDMMessage)
        btnDMSend = findViewById(R.id.btnDMSend)

        setupToolbar()
        setupRecyclerView()
        setupClickListeners()
    }

    private fun setupToolbar() {
        setSupportActionBar(toolbar)
        toolbar.title = currentUser?.name ?: "Direct Message"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setupRecyclerView() {
        messageAdapter = MessageAdapter()
        rvDMMessages.apply {
            layoutManager = LinearLayoutManager(this@DirectMessageActivity).apply {
                stackFromEnd = true
            }
            adapter = messageAdapter
        }
    }

    private fun setupClickListeners() {
        btnDMSend.setOnClickListener {
            sendDirectMessage()
        }
    }

    private fun sendDirectMessage() {
        val messageText = etDMMessage.text.toString().trim()
        if (messageText.isEmpty() || currentUser == null) return

        val message = Message(
            id = java.util.UUID.randomUUID().toString(),
            senderId = prefs.getUserId(),
            senderName = prefs.getUserName(),
            recipientId = currentUser!!.id,
            content = messageText,
            timestamp = System.currentTimeMillis(),
            isGroup = false
        )

        messageAdapter.addMessage(message)
        rvDMMessages.scrollToPosition(messageAdapter.itemCount - 1)
        etDMMessage.text.clear()

        // Send via Bluetooth Mesh Service (would be implemented in real scenario)
    }
}
