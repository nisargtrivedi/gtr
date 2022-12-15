package com.app.smartaccounting.adapter

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.recyclerview.widget.RecyclerView
import com.app.smartaccounting.R
import com.app.smartaccounting.databinding.RowTextMessageBinding
import com.app.smartaccounting.model.ChatMessage


class ChatAdapter(context: Context, jewelleryList: List<ChatMessage>) :
    RecyclerView.Adapter<ChatAdapter.ViewHolder>() {

    var jewelleryList: List<ChatMessage> = jewelleryList
    var context:Context = context


    private lateinit var binding: RowTextMessageBinding




    inner  class ViewHolder(private val rowNewListDiamondBinding: RowTextMessageBinding) : RecyclerView.ViewHolder(rowNewListDiamondBinding.root) {

        fun bindData(model: ChatMessage, position: Int) {
                if(model.chatUser){
                    rowNewListDiamondBinding.rlMain.gravity = Gravity.RIGHT
                  rowNewListDiamondBinding.cardImageOne.visibility= View.GONE
                    rowNewListDiamondBinding.cardImage.visibility= View.VISIBLE
                    rowNewListDiamondBinding.tvMessage.setBackgroundResource(R.drawable.chat_two_rect)
                }else {
                    rowNewListDiamondBinding.cardImageOne.visibility= View.VISIBLE
                    rowNewListDiamondBinding.cardImage.visibility= View.GONE
                    rowNewListDiamondBinding.tvMessage.setBackgroundResource(R.drawable.chat_one_rect)
                    rowNewListDiamondBinding.rlMain.gravity = Gravity.LEFT
                }

            rowNewListDiamondBinding.tvMessage.text =  model.chatMessage+""
        }
    }
    override fun onBindViewHolder(
        holder: ChatAdapter.ViewHolder,
        position: Int
    ) {
        val task: ChatMessage = jewelleryList.get(position)
        holder.bindData(task,position)

    }

    override fun getItemCount(): Int {
        return jewelleryList.size?:0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding = RowTextMessageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)

    }


}