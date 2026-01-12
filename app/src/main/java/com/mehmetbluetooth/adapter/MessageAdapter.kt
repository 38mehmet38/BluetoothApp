package com.mehmetbluetooth.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mehmetbluetooth.databinding.ItemMessageBinding
import com.mehmetbluetooth.model.Message
import java.text.SimpleDateFormat
import java.util.*

class MessageAdapter(
    private val messages: MutableList<Message> = mutableListOf()
) : RecyclerView.Adapter<MessageAdapter.ViewHolder>() {

    class ViewHolder(private val binding: ItemMessageBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(message: Message) {
            binding.tvSenderName.text = message.senderName
            binding.tvMessageContent.text = message.content
            binding.tvTimestamp.text = formatTime(message.timestamp)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemMessageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(messages[position])
    }

    override fun getItemCount(): Int = messages.size

    fun addMessage(message: Message) {
        messages.add(message)
        notifyItemInserted(messages.size - 1)
    }

    fun addMessages(newMessages: List<Message>) {
        val startIndex = messages.size
        messages.addAll(newMessages)
        notifyItemRangeInserted(startIndex, newMessages.size)
    }

    fun clear() {
        messages.clear()
        notifyDataSetChanged()
    }

    companion object {
        private fun formatTime(timestamp: Long): String {
            val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
            return sdf.format(Date(timestamp))
        }
    }
}
