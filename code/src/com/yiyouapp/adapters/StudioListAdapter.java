package com.yiyouapp.adapters;

import java.util.Date;
import java.util.List;

import com.yiyouapp.R;
import com.yiyouapp.utils.SizeUtil;
import com.yiyouapp.value.UserInfo;
import com.yiyouapp.value.Work;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class StudioListAdapter extends ArrayAdapter<UserInfo> {

	public final class ViewCache
	{
		public ImageView userAvatar;
	    public TextView userName;
	    
	    public TextView userLocation;
	    
	    public TextView trainInfo;
	    public TextView worksCount;

	    public TextView userDesc;
	}

	public StudioListAdapter(Context context, int resource, List<UserInfo> objects) {
		super(context, resource, objects);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Activity activity = (Activity) getContext();

        // Inflate the views from XML
        View rowView = convertView;
        ViewCache viewCache;
        
        if (rowView == null) {
            LayoutInflater inflater = activity.getLayoutInflater();
            rowView = inflater.inflate(R.layout.item_studio, null);
            
            viewCache = new ViewCache();

            viewCache.userAvatar = (ImageView)rowView.findViewById(R.id.user_avatar);
            viewCache.userName = (TextView)rowView.findViewById(R.id.user_name);

            viewCache.userLocation = (TextView)rowView.findViewById(R.id.user_location);

            viewCache.trainInfo = (TextView)rowView.findViewById(R.id.train_info);
            viewCache.worksCount = (TextView)rowView.findViewById(R.id.works_count);

            viewCache.userDesc = (TextView)rowView.findViewById(R.id.user_desc);
            
            rowView.setTag(viewCache);
        } else {
            viewCache = (ViewCache) rowView.getTag();
        }
        
        UserInfo user = getItem(position);

        //viewCache.userAvatar = ;
        viewCache.userName.setText(user.user_name);

        viewCache.userLocation.setText(user.user_city);

        viewCache.userDesc.setText(user.user_desc);
        //viewCache.worksCount.setText(String.valueOf(user.works_count) + "¸ö×÷Æ·");
        
        return rowView;
	}

}
