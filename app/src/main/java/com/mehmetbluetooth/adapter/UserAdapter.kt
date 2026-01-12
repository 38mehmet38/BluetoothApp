package com.mehmetbluetooth.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mehmetbluetooth.R
import com.mehmetbluetooth.databinding.ItemUserBinding
import com.mehmetbluetooth.model.User

class UserAdapter(
    private val users: MutableList<User> = mutableListOf(),
    private val onUserClick: (User) -> Unit
) : RecyclerView.Adapter<UserAdapter.ViewHolder>() {

    class ViewHolder(private val binding: ItemUserBinding, private val onUserClick: (User) -> Unit) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(user: User) {
            binding.tvUserName.text = user.name
            binding.tvUserStatus.text = if (user.isOnline) "Çevrimiçi" else "Çevrimdışı"
            binding.ivStatus.setImageResource(
                if (user.isOnline) R.drawable.status_online else R.drawable.status_offline
            )
            binding.root.setOnClickListener { onUserClick(user) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, onUserClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(users[position])
    }

    override fun getItemCount(): Int = users.size

    fun updateUsers(newUsers: List<User>) {
        users.clear()
        users.addAll(newUsers)
        notifyDataSetChanged()
    }

    fun addUser(user: User) {
        val index = users.indexOfFirst { it.macAddress == user.macAddress }
        if (index >= 0) {
            users[index] = user
            notifyItemChanged(index)
        } else {
            users.add(user)
            notifyItemInserted(users.size - 1)
        }
    }

    fun removeUser(user: User) {
        val index = users.indexOfFirst { it.macAddress == user.macAddress }
        if (index >= 0) {
            users.removeAt(index)
            notifyItemRemoved(index)
        }
    }

    fun clear() {
        users.clear()
        notifyDataSetChanged()
    }
}
