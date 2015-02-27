
package com.yiyouapp.adapters;

import android.R.integer;
import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.net.Uri;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;

import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import com.yiyouapp.AppConfig;
import com.yiyouapp.R;
import com.yiyouapp.utils.EmotionUtil;
import com.yiyouapp.utils.StringUtil;
import com.yiyouapp.value.CommentMsg;
import com.yiyouapp.value.SendState;
import com.yiyouapp.value.Work;

public class CommentViewAdapter extends BaseAdapter {

	public interface ICommentAction
	{
		// 点击用户头像
		public void onAvatarClick(CommentMsg msg);
		// 点击内容部分
		public void onContentClick(CommentMsg msg);
	}
	
    private List<CommentMsg> coll;

    private Context ctx;
    
    private LayoutInflater inflater_;

    private ICommentAction action_;
    
    // 顶部的那一行
    private View top_line_;
    
    public CommentViewAdapter(Context context, List<CommentMsg> coll, ICommentAction action) {
        ctx = context;
        this.coll = coll;
        action_ = action;
        
        inflater_ = LayoutInflater.from(context);

		top_line_= inflater_.inflate(R.layout.item_comment_top, null);
    }

    public int getCount() {
        return coll.size() + 1;
    }

    public CommentMsg getItem(int position) {
    	if(position == 0)
    		return null;
        return coll.get(position - 1);
    }

    public long getItemId(int position) {
        return position;
    }
    
    public View getView(int position, View convertView, ViewGroup parent) {
    	
    	if(position == 0)
    	{
	    	return top_line_;
    	}
    	
    	CommentMsg entity = coll.get(position - 1);
    	
    	ViewHolder viewHolder = null;
	    if (convertView == null || convertView.getTag() == null)
	    {
	    	convertView = inflater_.inflate(R.layout.item_comment, null);

			viewHolder = new ViewHolder();
			viewHolder.sendTime = (TextView) convertView.findViewById(R.id.tv_sendtime);
			viewHolder.userName = (TextView) convertView.findViewById(R.id.tv_username);
			viewHolder.content = (TextView) convertView.findViewById(R.id.tv_chatcontent);
			viewHolder.content_rl = convertView.findViewById(R.id.content_rl);
			viewHolder.avatar = (ImageView)convertView.findViewById(R.id.iv_userhead);

			viewHolder.sendstate_fail = convertView.findViewById(R.id.tv_sendstate_fail);
			viewHolder.sendstate_sending = convertView.findViewById(R.id.tv_sendstate_sending);
	        
			viewHolder.avatar.setOnClickListener(new OnClickListener(){
	
				@Override
				public void onClick(View view) {
					action_.onAvatarClick((CommentMsg)view.getTag());
				}
					
			});

			viewHolder.content_rl.setOnClickListener(new OnClickListener(){
	
				@Override
				public void onClick(View view) {
					action_.onContentClick((CommentMsg)view.getTag());
				}
					
			});

			convertView.setTag(viewHolder);
	    }else{
	        viewHolder = (ViewHolder) convertView.getTag();
	    }

	    viewHolder.sendTime.setText(StringUtil.getBriefDateString(entity.date));
	    viewHolder.userName.setText(entity.name);
	    EmotionUtil.showEmotion(ctx, viewHolder.content, entity.text);

        String fileName = StringUtil.getFileNameFromPath(entity.avatar_path);
    	Uri uri = AppConfig.cache.sdcard.getExistFileUri(fileName);
    	if(uri != null)
    		viewHolder.avatar.setImageURI(uri);
    	else
    		viewHolder.avatar.setImageResource(R.drawable.default_avatar_100);
    
	    viewHolder.avatar.setTag(entity);
	    viewHolder.content_rl.setTag(entity);
	    
	    viewHolder.setState(entity.state);
	    
	    return convertView;
    }
    
    // 获取顶部的那行
    public View getTopLine()
    {
    	return top_line_;
    }
    
    static class ViewHolder { 
        public TextView sendTime;
        public TextView userName;
        
        public View sendstate_fail;
        public View sendstate_sending;
        
        public View content_rl;
        public TextView content;
        
        public ImageView avatar;
        
        public void setState(SendState state)
        {
        	switch(state)
        	{
        	case Finished:
        		if(sendTime.getVisibility() == View.GONE)
        			sendTime.setVisibility(View.VISIBLE);
        		
        		if(sendstate_fail.getVisibility() == View.VISIBLE)
        			sendstate_fail.setVisibility(View.GONE);
        		
        		if(sendstate_sending.getVisibility() == View.VISIBLE)
        			sendstate_sending.setVisibility(View.GONE);
        		break;
        	case Sending:
        		if(sendTime.getVisibility() == View.VISIBLE)
        			sendTime.setVisibility(View.GONE);
        		
        		if(sendstate_fail.getVisibility() == View.VISIBLE)
        			sendstate_fail.setVisibility(View.GONE);
        		
        		if(sendstate_sending.getVisibility() == View.GONE)
        			sendstate_sending.setVisibility(View.VISIBLE);
        		break;
        	case Failed:
        		if(sendTime.getVisibility() == View.VISIBLE)
        			sendTime.setVisibility(View.GONE);
        		
        		if(sendstate_fail.getVisibility() == View.GONE)
        			sendstate_fail.setVisibility(View.VISIBLE);
        		
        		if(sendstate_sending.getVisibility() == View.VISIBLE)
        			sendstate_sending.setVisibility(View.GONE);
        		break;
        	}
        }
    }

}
