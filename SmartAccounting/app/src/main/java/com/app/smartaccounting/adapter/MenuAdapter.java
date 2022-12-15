package com.app.smartaccounting.adapter;

import android.content.Context;
import android.os.Build;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.smartaccounting.R;
import com.app.smartaccounting.listeners.onImageDelete;
import com.app.smartaccounting.model.categoryModel;

import java.util.List;

public class MenuAdapter extends BaseAdapter {

    Context mContext;
    int layoutResourceId;
   List<String> list;

   onImageDelete onImageDelete;
   public void selectMenuItem(onImageDelete onImageDelete){
       this.onImageDelete = onImageDelete;
   }
    public MenuAdapter(Context mContext, List<String> list) {
        this.mContext = mContext;
        this.list = list;


    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View listItem = convertView;
        listItem = LayoutInflater.from(mContext).inflate(R.layout.row_textview ,parent, false);

        TextView textViewName = (TextView) listItem.findViewById(R.id.tvMessage);
        LinearLayout rlMain = listItem.findViewById(R.id.rlMain);

        String folder = list.get(position);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            textViewName.setText(Html.fromHtml(folder,Html.FROM_HTML_MODE_COMPACT));
        }else{
            textViewName.setText(Html.fromHtml(folder));
        }
        rlMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onImageDelete.onClick(folder,position);
            }
        });
        return listItem;
    }
}