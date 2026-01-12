package com.mehmetbluetooth.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.mehmetbluetooth.R
import com.mehmetbluetooth.adapter.UserAdapter
import com.mehmetbluetooth.model.CallType
import com.mehmetbluetooth.model.User
import com.mehmetbluetooth.util.Constants

class PrivateChatFragment : Fragment() {

    private lateinit var rvUsers: RecyclerView
    private lateinit var userAdapter: UserAdapter
    private var users = mutableListOf<User>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_private_chat, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rvUsers = view.findViewById(R.id.rvUsers)
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        userAdapter = UserAdapter(users) { user ->
            showUserActionSheet(user)
        }
        rvUsers.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = userAdapter
        }
    }

    private fun showUserActionSheet(user: User) {
        val context = requireContext()
        val dialog = BottomSheetDialog(context)
        val view = LayoutInflater.from(context).inflate(R.layout.bottom_sheet_user_actions, null)

        val btnMessage = view.findViewById<MaterialButton>(R.id.btnMessage)
        val btnAudioCall = view.findViewById<MaterialButton>(R.id.btnAudioCall)
        val btnVideoCall = view.findViewById<MaterialButton>(R.id.btnVideoCall)

        btnMessage.setOnClickListener {
            startDirectMessage(user)
            dialog.dismiss()
        }

        btnAudioCall.setOnClickListener {
            initiateCall(user, CallType.AUDIO)
            dialog.dismiss()
        }

        btnVideoCall.setOnClickListener {
            initiateCall(user, CallType.VIDEO)
            dialog.dismiss()
        }

        dialog.setContentView(view)
        dialog.show()
    }

    private fun startDirectMessage(user: User) {
        val intent = Intent(requireContext(), DirectMessageActivity::class.java)
        intent.putExtra(Constants.EXTRA_USER, user)
        startActivity(intent)
    }

    private fun initiateCall(user: User, callType: CallType) {
        val intent = Intent(requireContext(), CallScreenActivity::class.java)
        intent.putExtra(Constants.EXTRA_USER, user)
        intent.putExtra("call_type", callType.ordinal)
        startActivity(intent)
    }

    fun updateUsersList(newUsers: List<User>) {
        users.clear()
        users.addAll(newUsers)
        userAdapter.updateUsers(newUsers)
    }

    fun addUser(user: User) {
        userAdapter.addUser(user)
    }

    fun removeUser(user: User) {
        userAdapter.removeUser(user)
    }
}
