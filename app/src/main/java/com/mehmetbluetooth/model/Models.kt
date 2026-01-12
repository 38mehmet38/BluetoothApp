package com.mehmetbluetooth.model

import java.io.Serializable

data class User(
    val id: String = "",
    val name: String = "",
    val macAddress: String = "",
    val isOnline: Boolean = false,
    val signalStrength: Int = -100,
    val lastSeen: Long = System.currentTimeMillis()
) : Serializable

data class Message(
    val id: String = "",
    val senderId: String = "",
    val senderName: String = "",
    val recipientId: String? = null, // null for group chat
    val content: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val isGroup: Boolean = true
) : Serializable

data class CallRequest(
    val id: String = "",
    val callerId: String = "",
    val callerName: String = "",
    val receiverId: String = "",
    val callType: CallType = CallType.VIDEO, // VIDEO or AUDIO
    val timestamp: Long = System.currentTimeMillis(),
    val status: CallStatus = CallStatus.PENDING // PENDING, ACCEPTED, DECLINED, ENDED
) : Serializable

enum class CallType {
    AUDIO, VIDEO
}

enum class CallStatus {
    PENDING, ACCEPTED, DECLINED, ENDED, IN_PROGRESS
}

data class BluetoothDevice(
    val name: String,
    val address: String,
    val rssi: Int = -100,
    val isConnected: Boolean = false,
    val lastUpdate: Long = System.currentTimeMillis()
) : Serializable

data class MeshPacket(
    val sourceId: String = "",
    val destinationId: String = "",
    val hopLimit: Int = 10,
    val ttl: Int = 64,
    val payload: ByteArray = byteArrayOf(),
    val messageType: MessageType = MessageType.CHAT,
    val timestamp: Long = System.currentTimeMillis()
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is MeshPacket) return false
        if (!payload.contentEquals(other.payload)) return false
        return true
    }

    override fun hashCode(): Int {
        return payload.contentHashCode()
    }
}

enum class MessageType {
    CHAT, CALL_REQUEST, CALL_ACCEPT, CALL_DECLINE, CALL_END,
    AUDIO_DATA, VIDEO_DATA, HEARTBEAT, ROUTE_DISCOVERY
}
