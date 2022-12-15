package com.app.smartaccounting.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.app.smartaccounting.R
import com.app.smartaccounting.listeners.onImageDelete
import com.bumptech.glide.Glide


class DetailImageAdapter(context: Context, jewelleryList: List<String>) :
    RecyclerView.Adapter<DetailImageAdapter.ViewHolder>() {

    var jewelleryList: List<String> = jewelleryList
    var context:Context = context

    lateinit var ondelete: onImageDelete

    fun ondeleteImage(onImageDelete: onImageDelete){
        ondelete = onImageDelete
    }


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {


        var imgJewellery: ImageView
        var imgClose:ImageView

        init {
            imgJewellery = view.findViewById(R.id.img)
            imgClose = view.findViewById(R.id.imgClose)
        }
    }

    override fun onBindViewHolder(
        holder: DetailImageAdapter.ViewHolder,
        position: Int
    ) {
        val task: String = jewelleryList.get(position)

        if(!task!!.isNullOrEmpty()) {
            if(task.endsWith("png") || task.endsWith("jpg") || task.endsWith("jpeg")) {
                Glide.with(holder.imgJewellery.context)
                    .load(task!!)
                    .into(holder.imgJewellery)
            }else{
                Glide.with(holder.imgJewellery.context)
                    .load(R.drawable.pdf_image)
                    .into(holder.imgJewellery);
            }
           // holder.imgJewellery.setImageURI(task as Uri)
        }else{
            Glide.with(holder.imgJewellery.context)
                .load(R.drawable.noimage)
                .into(holder.imgJewellery);
        }

        holder.imgClose.setOnClickListener {
            ondelete.onClick(task,position)
        }
    }

    override fun getItemCount(): Int {
        return jewelleryList.size?:0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView: View = LayoutInflater.from(context)
            .inflate(R.layout.row_image_two, parent, false)
        return ViewHolder(itemView)

    }


}