package com.app.smartaccounting.adapter

import android.content.Context
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.recyclerview.widget.RecyclerView
import com.app.smartaccounting.R
import com.app.smartaccounting.databinding.RowReceivedBinding
import com.app.smartaccounting.databinding.RowTextMessageBinding
import com.app.smartaccounting.listeners.onFileClick
import com.app.smartaccounting.model.ChatMessage
import com.app.smartaccounting.model.ReceivedMessage
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


class ReceivedAdapter(context: Context, jewelleryList: List<ReceivedMessage>) :
    RecyclerView.Adapter<ReceivedAdapter.ViewHolder>() {

    var jewelleryList: List<ReceivedMessage> = jewelleryList
    var context:Context = context

    lateinit var onFileClick: onFileClick

    private lateinit var binding: RowReceivedBinding

    fun onFile(onFileClick: onFileClick){
        this.onFileClick = onFileClick
    }



    inner  class ViewHolder(private val rowNewListDiamondBinding: RowReceivedBinding) : RecyclerView.ViewHolder(rowNewListDiamondBinding.root) {

        fun bindData(model: ReceivedMessage, position: Int) {
            rowNewListDiamondBinding.tvBody.text =  model.MessageBody+""
            rowNewListDiamondBinding.tvTitle.text = model.MessageTitle+""
            rowNewListDiamondBinding.tvTime.text = covertTimeToText(model.chattTime)+""
            rowNewListDiamondBinding.tvCount.text=model.filesList.size.toString()+" files"

            rowNewListDiamondBinding.tvCount.setOnClickListener {
                onFileClick.onClick(model)
            }
        }
    }
    override fun onBindViewHolder(
        holder: ReceivedAdapter.ViewHolder,
        position: Int
    ) {
        val task: ReceivedMessage = jewelleryList.get(position)
        holder.bindData(task,position)

    }

    override fun getItemCount(): Int {
        return jewelleryList.size?:0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding = RowReceivedBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)

    }

    fun covertTimeToText(dataDate: String?): String? {
        var convTime: String? = null
        val prefix = ""
        val suffix = "Ago"
        try {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            val pasTime: Date = dateFormat.parse(dataDate)
            val nowTime = Date()
            val dateDiff = nowTime.time - pasTime.time
            val second: Long = TimeUnit.MILLISECONDS.toSeconds(dateDiff)
            val minute: Long = TimeUnit.MILLISECONDS.toMinutes(dateDiff)
            val hour: Long = TimeUnit.MILLISECONDS.toHours(dateDiff)
            val day: Long = TimeUnit.MILLISECONDS.toDays(dateDiff)
            if (second < 60) {
                convTime = "$second Seconds $suffix"
            } else if (minute < 60) {
                convTime = "$minute Minutes $suffix"
            } else if (hour < 24) {
                convTime = "$hour Hours $suffix"
            } else if (day >= 7) {
                convTime = if (day > 360) {
                    (day / 360).toString() + " Years " + suffix
                } else if (day > 30) {
                    (day / 30).toString() + " Months " + suffix
                } else {
                    (day / 7).toString() + " Week " + suffix
                }
            } else if (day < 7) {
                convTime = "$day Days $suffix"
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("ConvTimeE", e.message.toString())
        }
        return convTime
    }

}