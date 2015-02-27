package com.yiyouapp.adapters;

import com.yiyouapp.R;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.TextView;

public class SingleSelectorAdapter extends BaseAdapter {

	private LayoutInflater inflater_;
	private String[] data_array_;
	private int selectItem_ = -1;
	
	public SingleSelectorAdapter(Context context, String[] data_array) {  
		inflater_ = LayoutInflater.from(context);
		data_array_ = data_array;
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
		
		CheckedTextView tv = null;
        if (convertView == null) 
        {  
            convertView = inflater_.inflate(R.layout.item_single_select, null);

            tv = (CheckedTextView)convertView;
            
            convertView.setTag(tv);           
        } 
        else 
        {  
        	tv = (CheckedTextView) convertView.getTag();  
        }

        tv.setText((String) data_array_[position]);

        if (position == selectItem_) {  
            convertView.setBackgroundColor(0xFF3caaf9);
        }   
        else {  
            convertView.setBackgroundColor(Color.TRANSPARENT);
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
