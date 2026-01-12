package com.mehmetbluetooth.util

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson

class PreferenceManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("MEHMETBLUETOOTH_PREFS", Context.MODE_PRIVATE)
    private val gson = Gson()

    companion object {
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_MAC_ADDRESS = "mac_address"
        private const val KEY_IS_FIRST_LAUNCH = "is_first_launch"
    }

    fun setUserName(name: String) = prefs.edit().putString(KEY_USER_NAME, name).apply()
    fun getUserName(): String = prefs.getString(KEY_USER_NAME, "") ?: ""

    fun setUserId(id: String) = prefs.edit().putString(KEY_USER_ID, id).apply()
    fun getUserId(): String = prefs.getString(KEY_USER_ID, "") ?: ""

    fun setMacAddress(mac: String) = prefs.edit().putString(KEY_MAC_ADDRESS, mac).apply()
    fun getMacAddress(): String = prefs.getString(KEY_MAC_ADDRESS, "") ?: ""

    fun setFirstLaunch(isFirst: Boolean) = prefs.edit().putBoolean(KEY_IS_FIRST_LAUNCH, isFirst).apply()
    fun isFirstLaunch(): Boolean = prefs.getBoolean(KEY_IS_FIRST_LAUNCH, true)

    fun clear() = prefs.edit().clear().apply()
}

object Constants {
    // Bluetooth
    const val MESH_SERVICE_UUID = "0000180A-0000-1000-8000-00805F9B34FB"
    const val MESH_CHARACTERISTIC_UUID = "00002A29-0000-1000-8000-00805F9B34FB"
    
    // Message Types
    const val MSG_TYPE_CHAT = 1
    const val MSG_TYPE_CALL_REQUEST = 2
    const val MSG_TYPE_CALL_ACCEPT = 3
    const val MSG_TYPE_AUDIO_DATA = 4
    const val MSG_TYPE_VIDEO_DATA = 5
    
    // Broadcast Actions
    const val ACTION_DEVICE_DISCOVERED = "com.mehmetbluetooth.ACTION_DEVICE_DISCOVERED"
    const val ACTION_MESSAGE_RECEIVED = "com.mehmetbluetooth.ACTION_MESSAGE_RECEIVED"
    const val ACTION_CALL_INCOMING = "com.mehmetbluetooth.ACTION_CALL_INCOMING"
    const val ACTION_DEVICE_CONNECTED = "com.mehmetbluetooth.ACTION_DEVICE_CONNECTED"
    const val ACTION_DEVICE_DISCONNECTED = "com.mehmetbluetooth.ACTION_DEVICE_DISCONNECTED"
    
    // Intent Extras
    const val EXTRA_DEVICE = "extra_device"
    const val EXTRA_MESSAGE = "extra_message"
    const val EXTRA_CALL = "extra_call"
    const val EXTRA_USER = "extra_user"
    
    // Mesh Parameters
    const val MESH_HOP_LIMIT = 10
    const val MESH_TTL = 64
}

fun String.toByteArray(): ByteArray = this.toByteArray(Charsets.UTF_8)

fun ByteArray.toUTF8String(): String = String(this, Charsets.UTF_8)

fun generateUUID(): String = java.util.UUID.randomUUID().toString()
