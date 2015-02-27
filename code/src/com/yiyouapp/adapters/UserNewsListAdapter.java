package com.yiyouapp.adapters;

import java.util.Date;
import java.util.List;

import com.yiyouapp.AppConfig;
import com.yiyouapp.R;
import com.yiyouapp.utils.SizeUtil;
import com.yiyouapp.utils.StringUtil;
import com.yiyouapp.utils.WorkUtil;
import com.yiyouapp.value.UserNews;
import com.yiyouapp.value.TypeDef;
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

public class UserNewsListAdapter extends ArrayAdapter<UserNews> {

	public interface INewsAction
	{
		public void onAvatarClick(UserNews news);
		public void onUserInfoClick(UserNews news);
	}
	
	public final class ViewCache
	{
		public ImageView userAvatar;
	    public TextView userName;
	    
	    public TextView message;
	    public TextView time;
	    
	    public View userInfo;

		public ImageView dot;
	}

	private INewsAction action_;
	
	public UserNewsListAdapter(Context context, List<UserNews> objects, INewsAction action) {
		super(context, 0, objects);
		action_ = action;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Activity activity = (Activity) getContext();

        // Inflate the views from XML
        View rowView = convertView;
        ViewCache viewCache;
        
        if (rowView == null) {
            LayoutInflater inflater = activity.getLayoutInflater();
            rowView = inflater.inflate(R.layout.item_msg_user, null);
            
            viewCache = new ViewCache();

            viewCache.userAvatar = (ImageView)rowView.findViewById(R.id.user_avatar);
            viewCache.userName = (TextView)rowView.findViewById(R.id.user_name);

            viewCache.message = (TextView)rowView.findViewById(R.id.message);

            viewCache.time = (TextView)rowView.findViewById(R.id.time);

            viewCache.userInfo = rowView.findViewById(R.id.user_info_ll);

            viewCache.dot = (ImageView)rowView.findViewById(R.id.dot);
            
            viewCache.userAvatar.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View view) {
					action_.onAvatarClick((UserNews)view.getTag());
				}
            	
            });

            rowView.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View view) {
					ViewCache cache = (ViewCache) view.getTag();
					action_.onUserInfoClick((UserNews)cache.userInfo.getTag());
				}
            	
            });

            rowView.setTag(viewCache);
        } else {
            viewCache = (ViewCache) rowView.getTag();
        }
        
        UserNews msg = getItem(position);

        String fileName = StringUtil.getFileNameFromPath(msg.avatar64_path);
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
        
        viewCache.userName.setText(msg.user_name);
        viewCache.message.setText(TypeDef.getNewsDesc(msg.news_type));

        viewCache.time.setText(WorkUtil.getTimeDesc(msg.elapsed_secs));
        
        if(msg.is_view)
        	viewCache.dot.setVisibility(View.GONE);
        else
        	viewCache.dot.setVisibility(View.VISIBLE);
        
        viewCache.userInfo.setTag(msg);
        viewCache.userAvatar.setTag(msg);
        
        return rowView;
	}

}
