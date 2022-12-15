package com.app.smartaccounting.adapter

import android.content.Context
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.SpinnerAdapter
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import coil.ImageLoader
import coil.decode.SvgDecoder
import coil.load
import com.app.smartaccounting.R
import com.app.smartaccounting.databinding.RowCategoryBinding
import com.app.smartaccounting.databinding.RowHomeCategoryBinding
import com.app.smartaccounting.databinding.RowReceivedBinding
import com.app.smartaccounting.databinding.RowTextMessageBinding
import com.app.smartaccounting.listeners.onCategoryClick
import com.app.smartaccounting.model.ChatMessage
import com.app.smartaccounting.model.ReceivedMessage
import com.app.smartaccounting.model.categoryModel
import com.app.smartaccounting.network.RetrofitBuilder
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class HomeCategoryAdapter(context: Context, jewelleryList: List<categoryModel>) :
    RecyclerView.Adapter<HomeCategoryAdapter.ViewHolder>() {

    var jewelleryList: List<categoryModel> = jewelleryList
    var context:Context = context

    lateinit var categoryClick: onCategoryClick

    private lateinit var binding: RowHomeCategoryBinding

    fun onCategoryClickListeners(onCategoryClick: onCategoryClick){
        this.categoryClick = onCategoryClick
    }



    inner  class ViewHolder(private val rowNewListDiamondBinding: RowHomeCategoryBinding) : RecyclerView.ViewHolder(rowNewListDiamondBinding.root) {

        fun bindData(model: categoryModel, position: Int) {
                rowNewListDiamondBinding.tvMessage.text =  model.Name+""
            if(!model.unread_messages.isNullOrEmpty()){
                if(!model.unread_messages.equals("0")) {
                    rowNewListDiamondBinding.tvCount.setText(model.unread_messages)
                    rowNewListDiamondBinding.tvCount.visibility = View.VISIBLE
                }else{
                    rowNewListDiamondBinding.tvCount.visibility = View.GONE
                }
            }
            if(model.icon!=null && model.icon.isNotEmpty()){
                rowNewListDiamondBinding.imgCategory.loadImage(RetrofitBuilder.BASE_URL_IMAGE+model.icon,R.drawable.img_category)
            }

            rowNewListDiamondBinding.rlMain.setOnClickListener {
                categoryClick.onClick(model)
            }
        }
    }
    override fun onBindViewHolder(
        holder: HomeCategoryAdapter.ViewHolder,
        position: Int
    ) {
        val task: categoryModel = jewelleryList.get(position)
        holder.bindData(task,position)

    }

    override fun getItemCount(): Int {
        return jewelleryList.size?:0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding = RowHomeCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)

    }



    fun ImageView.loadImage(imageUri: String, placeholder: Int? = null) {

        val imageLoader = ImageLoader.Builder(this.context)
            .componentRegistry { add(SvgDecoder(this@loadImage.context)) }
            .build()

        load(uri = imageUri, imageLoader = imageLoader) {
            crossfade(true)
            placeholder(placeholder ?: R.drawable.img_category)
        }
    }
}