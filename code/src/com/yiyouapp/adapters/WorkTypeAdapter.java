package com.yiyouapp.adapters;

import com.yiyouapp.R;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.TextView;

public class WorkTypeAdapter extends BaseAdapter {

    static class ViewHolder { 
    	public ImageView left;
        public CheckedTextView right;
    }
    
    private Context context_;
	private LayoutInflater inflater_;
	private String[] data_array_;
	private int selectItem_ = -1;
	
	public WorkTypeAdapter(Context context, String[] data_array) {  
		inflater_ = LayoutInflater.from(context);
		data_array_ = data_array;
		
		context_ = context;
    }
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return data_array_.length;
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return data_array_[arg0];
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		ViewHolder holder = null;
        if (convertView == null) 
        {  
        	holder = new ViewHolder();
        	
            convertView = inflater_.inflate(R.layout.item_work_type, null);

            holder.left = (ImageView) convertView.findViewById(R.id.icon);
            holder.right = (CheckedTextView) convertView.findViewById(R.id.text1);
            
            convertView.setTag(holder);
        }
        else
        {
        	holder = (ViewHolder)convertView.getTag();
        }

		int resId = context_.getResources().getIdentifier("icon_work_type" + (position + 1), 
				"drawable", context_.getPackageName());

		holder.left.setImageResource(resId);
        holder.right.setText((String) data_array_[position]);

        if (position == selectItem_) {  
            convertView.setBackgroundColor(0xFF3caaf9);
            holder.right.setChecked(true);
            holder.right.setTextColor(Color.WHITE);
        }   
        else {  
            convertView.setBackgroundColor(Color.WHITE);
            holder.right.setChecked(false);
            holder.right.setTextColor(Color.BLACK);
        }     
        
        return convertView;
	}

	public void setSelectItem(int selectItem) {  
        selectItem_ = selectItem;
    }

	public int getSelectItem() {  
        return selectItem_;
    }
}
