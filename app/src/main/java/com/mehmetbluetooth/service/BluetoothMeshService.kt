package com.mehmetbluetooth.service

import android.app.Service
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import com.mehmetbluetooth.model.Message
import com.mehmetbluetooth.model.MeshPacket
import com.mehmetbluetooth.model.MessageType
import com.mehmetbluetooth.model.User
import com.mehmetbluetooth.util.Constants
import com.mehmetbluetooth.util.PreferenceManager
import kotlinx.coroutines.*

class BluetoothMeshService : Service() {

    private val tag = "BluetoothMeshService"
    private val binder = LocalBinder()
    private val scope = CoroutineScope(Dispatchers.Default + Job())
    
    private lateinit var prefs: PreferenceManager
    private lateinit var bluetoothAdapter: BluetoothAdapter
    private var receiver: BluetoothReceiver? = null
    private var discoveredDevices = mutableMapOf<String, User>()
    private var meshRouting = mutableMapOf<String, String>() // Source -> Next Hop

    inner class LocalBinder : Binder() {
        fun getService(): BluetoothMeshService = this@BluetoothMeshService
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(tag, "Service Created")
        
        prefs = PreferenceManager(this)
        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = bluetoothManager.adapter ?: return
        
        setupBroadcastReceiver()
        startBluetoothDiscovery()
    }

    private fun setupBroadcastReceiver() {
        receiver = BluetoothReceiver(this)
        val filter = IntentFilter().apply {
            addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED)
            addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
            addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(receiver, filter, Context.RECEIVER_EXPORTED)
        } else {
            @Suppress("DEPRECATION")
            registerReceiver(receiver, filter)
        }
        
        // Enable advertising
        startBluetoothAdvertising()
    }

    private fun startBluetoothDiscovery() {
        scope.launch {
            try {
                if (bluetoothAdapter.isDiscovering) {
                    bluetoothAdapter.cancelDiscovery()
                }
                
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    // Android 12+ requires BLUETOOTH_SCAN permission
                    bluetoothAdapter.startDiscovery()
                } else {
                    bluetoothAdapter.startDiscovery()
                }
                Log.d(tag, "Bluetooth Discovery Started")
            } catch (e: SecurityException) {
                Log.e(tag, "Discovery error: ${e.message}")
            }
        }
    }

    private fun startBluetoothAdvertising() {
        scope.launch {
            try {
                val userName = prefs.getUserName()
                Log.d(tag, "Starting advertising as: $userName")
            } catch (e: Exception) {
                Log.e(tag, "Advertising error: ${e.message}")
            }
        }
    }

    fun sendMessage(message: Message) {
        scope.launch {
            try {
                val packet = MeshPacket(
                    sourceId = prefs.getUserId(),
                    destinationId = message.recipientId ?: "broadcast",
                    payload = serializeMessage(message).toByteArray(),
                    messageType = MessageType.CHAT,
                    hopLimit = Constants.MESH_HOP_LIMIT
                )
                
                // Route the packet through mesh
                routeMeshPacket(packet)
            } catch (e: Exception) {
                Log.e(tag, "Send message error: ${e.message}")
            }
        }
    }

    private fun routeMeshPacket(packet: MeshPacket) {
        Log.d(tag, "Routing mesh packet from ${packet.sourceId} to ${packet.destinationId}")
        
        // If destination is in range, send directly
        discoveredDevices[packet.destinationId]?.let {
            broadcastPacket(packet)
            return
        }
        
        // Otherwise, use mesh routing (if available)
        val nextHop = meshRouting[packet.destinationId]
        if (nextHop != null && packet.hopLimit > 0) {
            val relayPacket = packet.copy(
                hopLimit = packet.hopLimit - 1,
                sourceId = packet.sourceId
            )
            broadcastPacket(relayPacket)
        }
    }

    private fun broadcastPacket(packet: MeshPacket) {
        // In real scenario, would send via Bluetooth GATT
        Log.d(tag, "Broadcasting packet: $packet")
    }

    fun addDiscoveredDevice(user: User) {
        discoveredDevices[user.id] = user
        val intent = Intent(Constants.ACTION_DEVICE_DISCOVERED).apply {
            putExtra(Constants.EXTRA_USER, user)
        }
        sendBroadcast(intent)
    }

    fun removeDiscoveredDevice(userId: String) {
        discoveredDevices.remove(userId)?.let { user ->
            val intent = Intent(Constants.ACTION_DEVICE_DISCONNECTED).apply {
                putExtra(Constants.EXTRA_USER, user)
            }
            sendBroadcast(intent)
        }
    }

    fun getDiscoveredDevices(): List<User> = discoveredDevices.values.toList()

    private fun serializeMessage(message: Message): String {
        return "${message.senderId}|${message.senderName}|${message.content}|${message.timestamp}"
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(tag, "Service Started")
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = binder

    override fun onDestroy() {
        super.onDestroy()
        Log.d(tag, "Service Destroyed")
        receiver?.let { unregisterReceiver(it) }
        scope.cancel()
    }
}
