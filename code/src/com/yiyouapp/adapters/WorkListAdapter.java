package com.yiyouapp.adapters;

import java.util.Date;
import java.util.List;

import com.yiyouapp.AppConfig;
import com.yiyouapp.R;
import com.yiyouapp.utils.Location;
import com.yiyouapp.utils.SizeUtil;
import com.yiyouapp.utils.StringUtil;
import com.yiyouapp.utils.WorkUtil;
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
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;

public class WorkListAdapter extends ArrayAdapter<Work> {

	public interface IWorkAction
	{
		// 通知作品收藏的改变
		public void onCollectChanged(Work work, boolean checked);
		// 通知作品赞的改变
		public void onPriseChanged(Work work, boolean checked);
		// 点击分析按钮
		public void onShareClick(Work work);
		// 点击评论按钮
		public void onCommentClick(Work work);
		// 点击作品缩略图
		public void onThumbClick(Work work);
		// 点击作品的用户头像
		public void onAvatarClick(Work work);
	}
	
	public final class ViewCache
	{
		public View userInfoRL;
		
		public ImageView workThumb;
		
	    public TextView workDesc;
	    public ImageView userAvatar;
	    
	    public TextView userName;
	    public TextView uploadTime;
	    
	    public ImageView sexType;
	    public TextView userType;

	    public TextView userLocation;
	    public TextView userDistance;

	    public TextView approve;
	    public TextView collect;
	    public TextView comment;

	    public Button shareBtn;
	    public Button commentBtn;
	    
	    public CheckBox priseBtn;
	    public CheckBox collectBtn;
	}

	private IWorkAction action_;
	private boolean hideHeader_;
	
	public WorkListAdapter(Context context, int resource, List<Work> objects, 
			IWorkAction workAction, boolean hideHeader) {
		super(context, resource, objects);
		action_ = workAction;
		hideHeader_ = hideHeader;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Activity activity = (Activity) getContext();

        // Inflate the views from XML
        View rowView = convertView;
        ViewCache viewCache;
        
        if (rowView == null) {
            LayoutInflater inflater = activity.getLayoutInflater();
            rowView = inflater.inflate(R.layout.item_work, null);
            
            viewCache = new ViewCache();

            viewCache.userInfoRL = rowView.findViewById(R.id.user_info_rl);
            //if(hideHeader_)
            //	viewCache.userInfoRL.setVisibility(View.GONE);
            
            viewCache.workThumb = (ImageView)rowView.findViewById(R.id.work_thumb);
            viewCache.workDesc = (TextView)rowView.findViewById(R.id.work_desc);

            viewCache.userAvatar = (ImageView)rowView.findViewById(R.id.user_avatar);
            viewCache.uploadTime = (TextView)rowView.findViewById(R.id.upload_time);

            viewCache.sexType = (ImageView)rowView.findViewById(R.id.sex_type);
            viewCache.userType = (TextView)rowView.findViewById(R.id.user_type);
            viewCache.userName = (TextView)rowView.findViewById(R.id.user_name);

            viewCache.userLocation = (TextView)rowView.findViewById(R.id.user_location);
            viewCache.userDistance = (TextView)rowView.findViewById(R.id.user_distance);

            viewCache.approve = (TextView)rowView.findViewById(R.id.approve);
            viewCache.collect = (TextView)rowView.findViewById(R.id.collect);
            viewCache.comment = (TextView)rowView.findViewById(R.id.comment);

            viewCache.shareBtn = (Button)rowView.findViewById(R.id.share_btn);
            viewCache.commentBtn = (Button)rowView.findViewById(R.id.comment_btn);
            
            viewCache.priseBtn = (CheckBox)rowView.findViewById(R.id.approve_btn);
            viewCache.collectBtn = (CheckBox)rowView.findViewById(R.id.collect_btn);

            viewCache.workThumb.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View view) {
					action_.onThumbClick((Work)view.getTag());
				}
            	
            });

            viewCache.userInfoRL.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View view) {
					action_.onAvatarClick((Work)view.getTag());
				}
            	
            });

            viewCache.shareBtn.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View view) {
					action_.onShareClick((Work)view.getTag());
				}
            	
            });

            viewCache.commentBtn.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View view) {
					action_.onCommentClick((Work)view.getTag());
				}
            	
            });

            viewCache.comment.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View view) {
					action_.onCommentClick((Work)view.getTag());
				}
            	
            });

            viewCache.priseBtn.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View view) {
					Work work = (Work)view.getTag();
					CheckBox cb = (CheckBox)view;
					boolean checked = cb.isChecked();
					
					if(!checked)
					{
						cb.setChecked(true);
						return;
					}
					
					if(work.i_prise != checked)
					{
						work.i_prise = checked;
						action_.onPriseChanged(work, checked);
					}
				}
            	
            });

            viewCache.collectBtn.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View view) {
					Work work = (Work)view.getTag();
					CheckBox cb = (CheckBox)view;
					boolean checked = cb.isChecked();
					if(work.i_collect != checked)
					{
						work.i_collect = checked;
						action_.onCollectChanged(work, checked);
					}
				}
            	
            });

            rowView.setTag(viewCache);
        } else {
            viewCache = (ViewCache) rowView.getTag();
        }
        
        Work work = getItem(position);

        viewCache.workDesc.setText(work.work_desc);
        viewCache.userName.setText(work.user_name);

        int secs = (int)(new Date(System.currentTimeMillis()).getTime() - work.device_time.getTime()) / 1000;
        if(secs < 0)
        	secs = 0;
        viewCache.uploadTime.setText(WorkUtil.getTimeDesc(work.elapsed_secs + secs));

        if(work.sex.equals("M"))
        	viewCache.sexType.setImageResource(R.drawable.icon_male);
        else
        	viewCache.sexType.setImageResource(R.drawable.icon_female);
        
        viewCache.userType.setText(WorkUtil.getUserTypeNameFromValue(work.user_type));

        if(work.work_desc.equals(""))
        	viewCache.workDesc.setVisibility(View.GONE);
        else
        	viewCache.workDesc.setVisibility(View.VISIBLE);
        
        viewCache.userLocation.setText(work.city);
        if(work.has_location)
        {
        	viewCache.userDistance.setVisibility(View.VISIBLE);
        	viewCache.userDistance.setText(StringUtil.formatDistance(work.distance));
        }
        else
        {
        	viewCache.userDistance.setVisibility(View.GONE);
        }

        viewCache.approve.setText(String.valueOf(work.prise_count) + " 人赞");
        viewCache.collect.setText(String.valueOf(work.collect_count) + " 人收藏");
        viewCache.comment.setText(String.valueOf(work.comment_count) + " 评论");

        viewCache.collectBtn.setChecked(work.i_collect);
        viewCache.priseBtn.setChecked(work.i_prise);
        
        String fileName = StringUtil.getFileNameFromPath(work.thumb_path[0]);
        Bitmap thumbImage = AppConfig.cache.mem.getImage(fileName);
        if(thumbImage != null)
        {
        	viewCache.workThumb.setImageBitmap(thumbImage);
        }
        else
        {
        	viewCache.workThumb.setImageBitmap(null);
        	
        	Uri uri = AppConfig.cache.sdcard.getExistFileUri(fileName);
        	if(uri != null)
        		viewCache.workThumb.setImageURI(uri);
        }
        
        fileName = StringUtil.getFileNameFromPath(work.avatar_path);
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
        
        LayoutParams para;
        para = viewCache.workThumb.getLayoutParams();

        para.width = AppConfig.screen_width - SizeUtil.dp2px(16);
        para.height = (int)((float)para.width / work.thumb1_ratio);
        viewCache.workThumb.setLayoutParams(para);

        // 设置tag
        viewCache.workThumb.setTag(work);
        viewCache.userInfoRL.setTag(work);
        
        viewCache.priseBtn.setTag(work);
        viewCache.shareBtn.setTag(work);
        
        viewCache.commentBtn.setTag(work);
        viewCache.collectBtn.setTag(work);

        viewCache.comment.setTag(work);
        
        return rowView;
	}

}
