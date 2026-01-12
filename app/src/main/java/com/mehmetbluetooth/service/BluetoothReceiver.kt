package com.mehmetbluetooth.service

import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.mehmetbluetooth.model.User
import com.mehmetbluetooth.util.PreferenceManager

class BluetoothReceiver(private val service: BluetoothMeshService) : BroadcastReceiver() {

    private val tag = "BluetoothReceiver"

    override fun onReceive(context: Context?, intent: Intent?) {
        when (intent?.action) {
            BluetoothAdapter.ACTION_DISCOVERY_STARTED -> {
                Log.d(tag, "Discovery Started")
            }
            BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> {
                Log.d(tag, "Discovery Finished")
            }
            BluetoothAdapter.ACTION_SCAN_MODE_CHANGED -> {
                val scanMode = intent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE, -1)
                when (scanMode) {
                    BluetoothAdapter.SCAN_MODE_CONNECTABLE -> {
                        Log.d(tag, "Device is discoverable")
                    }
                    BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE -> {
                        Log.d(tag, "Device is discoverable")
                    }
                    BluetoothAdapter.SCAN_MODE_NONE -> {
                        Log.d(tag, "Device is not discoverable")
                    }
                }
            }
        }
    }
}
