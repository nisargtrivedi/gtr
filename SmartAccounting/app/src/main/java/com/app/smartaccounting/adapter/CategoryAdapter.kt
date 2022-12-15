package com.app.smartaccounting.adapter

import android.content.Context
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
import com.app.smartaccounting.databinding.RowReceivedBinding
import com.app.smartaccounting.databinding.RowTextMessageBinding
import com.app.smartaccounting.listeners.onCategoryClick
import com.app.smartaccounting.model.ChatMessage
import com.app.smartaccounting.model.ReceivedMessage
import com.app.smartaccounting.model.categoryModel
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class CategoryAdapter(context: Context, jewelleryList: List<categoryModel>) :
    RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {

    var jewelleryList: List<categoryModel> = jewelleryList
    var context:Context = context

    lateinit var categoryClick: onCategoryClick

    private lateinit var binding: RowCategoryBinding

    fun onCategoryClickListeners(onCategoryClick: onCategoryClick){
        this.categoryClick = onCategoryClick
    }



    inner  class ViewHolder(private val rowNewListDiamondBinding: RowCategoryBinding) : RecyclerView.ViewHolder(rowNewListDiamondBinding.root) {

        fun bindData(model: categoryModel, position: Int) {
            if(model.isSelected==0){
                rowNewListDiamondBinding.tvMessage.setTextColor(ContextCompat.getColor(context,R.color.white))
                rowNewListDiamondBinding.tvMessage.setBackgroundResource(R.drawable.received_message_category)
            }else{
                rowNewListDiamondBinding.tvMessage.setBackgroundResource(R.drawable.received_message_category_white)
                rowNewListDiamondBinding.tvMessage.setTextColor(ContextCompat.getColor(context,R.color.sign_up_color))
            }
                rowNewListDiamondBinding.tvMessage.text =  model.Name+""

            rowNewListDiamondBinding.rlMain.setOnClickListener {
                categoryClick.onClick(model)
            }
        }
    }
    override fun onBindViewHolder(
        holder: CategoryAdapter.ViewHolder,
        position: Int
    ) {
        val task: categoryModel = jewelleryList.get(position)
        holder.bindData(task,position)

    }

    override fun getItemCount(): Int {
        return jewelleryList.size?:0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding = RowCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)

    }



}