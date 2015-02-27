package com.yiyouapp.adapters;

import java.util.Date;
import java.util.List;

import com.yiyouapp.AppConfig;
import com.yiyouapp.R;
import com.yiyouapp.utils.SizeUtil;
import com.yiyouapp.utils.StringUtil;
import com.yiyouapp.utils.WorkUtil;
import com.yiyouapp.value.UserInfo;
import com.yiyouapp.value.Work;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class FriendListAdapter extends ArrayAdapter<UserInfo> {

	public final class ViewCache
	{
		public ImageView userAvatar;
	    public TextView userName;
	    
	    public ImageView sexType;
	    public TextView userType;

	    public TextView userLocation;
	    public TextView userDistance;
	    
	    public TextView trainInfo;
	    //public TextView worksCount;

	    public TextView userDesc;
	}

	public FriendListAdapter(Context context, int resource, List<UserInfo> objects) {
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
            rowView = inflater.inflate(R.layout.item_friend, null);
            
            viewCache = new ViewCache();

            viewCache.userAvatar = (ImageView)rowView.findViewById(R.id.user_avatar);
            viewCache.userName = (TextView)rowView.findViewById(R.id.user_name);

            viewCache.sexType = (ImageView)rowView.findViewById(R.id.sex_type);
            viewCache.userType = (TextView)rowView.findViewById(R.id.user_type);

            viewCache.userLocation = (TextView)rowView.findViewById(R.id.user_location);
            viewCache.userDistance = (TextView)rowView.findViewById(R.id.user_distance);

            //viewCache.worksCount = (TextView)rowView.findViewById(R.id.works_count);

            viewCache.userDesc = (TextView)rowView.findViewById(R.id.user_desc);
            
            rowView.setTag(viewCache);
        } else {
            viewCache = (ViewCache) rowView.getTag();
        }
        
        UserInfo user = getItem(position);

        viewCache.userName.setText(user.user_name);

        String fileName = StringUtil.getFileNameFromPath(user.avatar64_path);
        Bitmap avatarImage = AppConfig.cache.mem.getImage(fileName);
        if(avatarImage != null)
        {
        	viewCache.userAvatar.setImageBitmap(avatarImage);
        }
        else
        {
        	viewCache.userAvatar.setImageBitmap(null);
        	
        	Uri uri = AppConfig.cache.sdcard.getExistFileUri(fileName);
        	if(uri != null)
        		viewCache.userAvatar.setImageURI(uri);
        	else
                viewCache.userAvatar.setImageResource(R.drawable.default_avatar_100);
        }
        
        if(user.sex.equals("M"))
        	viewCache.sexType.setImageResource(R.drawable.icon_male);
        else
        	viewCache.sexType.setImageResource(R.drawable.icon_female);
        
        viewCache.userType.setText(WorkUtil.getUserTypeNameFromValue(user.user_type));

        if(user.has_location)
        {
        	viewCache.userDistance.setVisibility(View.VISIBLE);
        	viewCache.userDistance.setText(StringUtil.formatDistance(user.distance));
        }
        else
        {
        	viewCache.userDistance.setVisibility(View.GONE);
        }

        viewCache.userLocation.setText(user.user_city);

        viewCache.userDesc.setText(user.user_desc);
        //viewCache.worksCount.setText(String.valueOf(user.works_count) + "¸ö×÷Æ·");
        
        return rowView;
	}

}
