package com.mehmetbluetooth.ui

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import androidx.viewpager2.widget.ViewPager2
import com.mehmetbluetooth.R
import com.mehmetbluetooth.adapter.MainPagerAdapter
import com.mehmetbluetooth.model.Message
import com.mehmetbluetooth.model.User
import com.mehmetbluetooth.util.Constants

class MainActivity : AppCompatActivity() {

    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager2
    private lateinit var toolbar: Toolbar
    
    private var groupChatFragment: GroupChatFragment? = null
    private var privateChatFragment: PrivateChatFragment? = null
    
    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                Constants.ACTION_MESSAGE_RECEIVED -> {
                    val message = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        intent.getSerializableExtra(Constants.EXTRA_MESSAGE, Message::class.java)
                    } else {
                        @Suppress("DEPRECATION")
                        intent.getSerializableExtra(Constants.EXTRA_MESSAGE) as? Message
                    }
                    message?.let { 
                        if (it.isGroup) {
                            groupChatFragment?.receiveMessage(it)
                        }
                    }
                }
                Constants.ACTION_DEVICE_DISCOVERED -> {
                    val user = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        intent.getSerializableExtra(Constants.EXTRA_USER, User::class.java)
                    } else {
                        @Suppress("DEPRECATION")
                        intent.getSerializableExtra(Constants.EXTRA_USER) as? User
                    }
                    user?.let { 
                        privateChatFragment?.addUser(it)
                    }
                }
                Constants.ACTION_DEVICE_CONNECTED -> {
                    val user = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        intent.getSerializableExtra(Constants.EXTRA_USER, User::class.java)
                    } else {
                        @Suppress("DEPRECATION")
                        intent.getSerializableExtra(Constants.EXTRA_USER) as? User
                    }
                    user?.let { 
                        privateChatFragment?.addUser(it)
                    }
                }
                Constants.ACTION_DEVICE_DISCONNECTED -> {
                    val user = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        intent.getSerializableExtra(Constants.EXTRA_USER, User::class.java)
                    } else {
                        @Suppress("DEPRECATION")
                        intent.getSerializableExtra(Constants.EXTRA_USER) as? User
                    }
                    user?.let { 
                        privateChatFragment?.removeUser(it)
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        tabLayout = findViewById(R.id.tabLayout)
        viewPager = findViewById(R.id.viewPager)

        setupViewPager()
        setupTabLayout()
        registerBroadcasts()
    }

    private fun setupViewPager() {
        groupChatFragment = GroupChatFragment()
        privateChatFragment = PrivateChatFragment()

        val fragments = listOf(groupChatFragment!!, privateChatFragment!!)
        val adapter = MainPagerAdapter(this, fragments)
        viewPager.adapter = adapter
    }

    private fun setupTabLayout() {
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> getString(R.string.tab_group_chat)
                1 -> getString(R.string.tab_private_chat)
                else -> ""
            }
        }.attach()
    }

    private fun registerBroadcasts() {
        val filter = IntentFilter().apply {
            addAction(Constants.ACTION_MESSAGE_RECEIVED)
            addAction(Constants.ACTION_DEVICE_DISCOVERED)
            addAction(Constants.ACTION_DEVICE_CONNECTED)
            addAction(Constants.ACTION_DEVICE_DISCONNECTED)
            addAction(Constants.ACTION_CALL_INCOMING)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(broadcastReceiver, filter, Context.RECEIVER_EXPORTED)
        } else {
            @Suppress("DEPRECATION")
            registerReceiver(broadcastReceiver, filter)
        }
    }

    fun sendGroupMessage(message: Message) {
        // Broadcast to Bluetooth Mesh Service
        val intent = Intent(Constants.ACTION_MESSAGE_RECEIVED).apply {
            putExtra(Constants.EXTRA_MESSAGE, message)
        }
        sendBroadcast(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(broadcastReceiver)
    }
}
