package com.app.smartaccounting.adapter

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.app.smartaccounting.R
import com.app.smartaccounting.databinding.RowImageBinding
import com.app.smartaccounting.model.ReceivedMessage
import com.app.smartaccounting.network.RetrofitBuilder
import com.bumptech.glide.Glide
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


class FilesAdapter(context: Context, jewelleryList: List<ReceivedMessage.imageFiles>) :
    RecyclerView.Adapter<FilesAdapter.ViewHolder>() {

    var jewelleryList: List<ReceivedMessage.imageFiles> = jewelleryList
    var context:Context = context


    private lateinit var binding: RowImageBinding




    inner  class ViewHolder(private val rowNewListDiamondBinding: RowImageBinding) : RecyclerView.ViewHolder(rowNewListDiamondBinding.root) {

        fun bindData(model: ReceivedMessage.imageFiles, position: Int) {

            try {
                if(model.imagePath.isNotEmpty()) {
                    if(model.imagePath.endsWith("png") || model.imagePath.endsWith("jpeg") || model.imagePath.endsWith("jpg")) {
                        Glide.with(context).load(
                            RetrofitBuilder.BASE_URL_IMAGE + model.imagePath
                        ).into(rowNewListDiamondBinding.img)
                    }else{
                        Glide.with(context).load(
                            R.drawable.pdf_image
                        ).into(rowNewListDiamondBinding.img)
                    }
                    rowNewListDiamondBinding.img.setOnClickListener {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(RetrofitBuilder.BASE_URL_IMAGE + model.imagePath))
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        intent.setPackage("com.android.chrome")
                        try {
                            context.startActivity(intent)
                        } catch (e: ActivityNotFoundException) {
                            // Chrome is probably not installed
                            // Try with the default browser
                            intent.setPackage(null)
                            context.startActivity(intent)
                        }
                    }
                }
            }catch (e:Exception){
            }
        }
    }
    override fun onBindViewHolder(
        holder: FilesAdapter.ViewHolder,
        position: Int
    ) {
        val task: ReceivedMessage.imageFiles = jewelleryList.get(position)
        holder.bindData(task,position)

    }

    override fun getItemCount(): Int {
        return jewelleryList.size?:0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding = RowImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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