package com.mehmetbluetooth.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mehmetbluetooth.R
import com.mehmetbluetooth.adapter.MessageAdapter
import com.mehmetbluetooth.model.Message
import com.mehmetbluetooth.util.PreferenceManager

class GroupChatFragment : Fragment() {

    private lateinit var rvMessages: RecyclerView
    private lateinit var etMessage: EditText
    private lateinit var btnSend: Button
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var prefs: PreferenceManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_group_chat, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        prefs = PreferenceManager(requireContext())

        rvMessages = view.findViewById(R.id.rvMessages)
        etMessage = view.findViewById(R.id.etMessage)
        btnSend = view.findViewById(R.id.btnSend)

        setupRecyclerView()
        setupClickListeners()
    }

    private fun setupRecyclerView() {
        messageAdapter = MessageAdapter()
        rvMessages.apply {
            layoutManager = LinearLayoutManager(requireContext()).apply {
                stackFromEnd = true
            }
            adapter = messageAdapter
        }
    }

    private fun setupClickListeners() {
        btnSend.setOnClickListener {
            sendMessage()
        }
    }

    private fun sendMessage() {
        val messageText = etMessage.text.toString().trim()
        if (messageText.isEmpty()) return

        val message = Message(
            id = java.util.UUID.randomUUID().toString(),
            senderId = prefs.getUserId(),
            senderName = prefs.getUserName(),
            content = messageText,
            timestamp = System.currentTimeMillis(),
            isGroup = true
        )

        messageAdapter.addMessage(message)
        rvMessages.scrollToPosition(messageAdapter.itemCount - 1)
        etMessage.text.clear()

        // Send via Bluetooth Mesh Service
        (activity as? MainActivity)?.sendGroupMessage(message)
    }

    fun receiveMessage(message: Message) {
        messageAdapter.addMessage(message)
        rvMessages.scrollToPosition(messageAdapter.itemCount - 1)
    }
}
