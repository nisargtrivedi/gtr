package com.app.smartaccounting.adapter

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.SpinnerAdapter
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.app.smartaccounting.R
import com.app.smartaccounting.databinding.RowCategoryBinding
import com.app.smartaccounting.databinding.RowHomeFilesBinding
import com.app.smartaccounting.databinding.RowReceivedBinding
import com.app.smartaccounting.databinding.RowTextMessageBinding
import com.app.smartaccounting.listeners.onCategoryClick
import com.app.smartaccounting.listeners.onFileClick
import com.app.smartaccounting.model.ChatMessage
import com.app.smartaccounting.model.ReceivedMessage
import com.app.smartaccounting.model.categoryModel
import com.app.smartaccounting.network.RetrofitBuilder
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class HomeFilesAdapter(context: Context, jewelleryList: List<ReceivedMessage>) :
    RecyclerView.Adapter<HomeFilesAdapter.ViewHolder>() {

    var jewelleryList: List<ReceivedMessage> = jewelleryList
    var context:Context = context

    lateinit var categoryClick: onFileClick

    private lateinit var binding: RowHomeFilesBinding

    fun onCategoryClickListeners(onCategoryClick: onFileClick){
        this.categoryClick = onCategoryClick
    }



    inner  class ViewHolder(private val rowNewListDiamondBinding: RowHomeFilesBinding) : RecyclerView.ViewHolder(rowNewListDiamondBinding.root) {

        fun bindData(model: ReceivedMessage, position: Int) {

                rowNewListDiamondBinding.tvMessage.text =  model.MessageTitle+""

            if(model.chatUser){
                rowNewListDiamondBinding.tvMessage.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_send_files,0)
            }else{
                rowNewListDiamondBinding.tvMessage.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.received_files_ic,0)

            }

            rowNewListDiamondBinding.rlMain.setOnClickListener {
                categoryClick.onClick(model)
            }

        }
    }
    override fun onBindViewHolder(
        holder: HomeFilesAdapter.ViewHolder,
        position: Int
    ) {
        val task: ReceivedMessage = jewelleryList.get(position)
        holder.bindData(task,position)

    }

    override fun getItemCount(): Int {
        return jewelleryList.size?:0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding = RowHomeFilesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)

    }



}