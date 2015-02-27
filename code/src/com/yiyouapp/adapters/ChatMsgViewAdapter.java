
package com.yiyouapp.adapters;

import android.R.integer;
import android.app.LauncherActivity.ListItem;
import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
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

import com.yiyouapp.R;
import com.yiyouapp.utils.EmotionUtil;
import com.yiyouapp.value.ChatMsg;
import com.yiyouapp.value.SendState;
import com.yiyouapp.value.Work;

public class ChatMsgViewAdapter extends BaseAdapter {

	public interface IChatAction
	{
		// 点击用户头像
		public void onAvatarClick(ChatMsg msg);
		// 点击内容
		public void onContentClick(ChatMsg msg);
	}
	
	public static interface IMsgViewType
	{
		int IMVT_COM_MSG = 0;
		int IMVT_TO_MSG = 1;
	}
	
    private List<ChatMsg> coll;

    private Context ctx;
    
    private LayoutInflater inflater_;

    private IChatAction action_;
    
    public ChatMsgViewAdapter(Context context, List<ChatMsg> coll, IChatAction action) {
        ctx = context;
        this.coll = coll;
        action_ = action;
        
        inflater_ = LayoutInflater.from(context);
    }

    public int getCount() {
        return coll.size();
    }

    public ChatMsg getItem(int position) {
        return coll.get(position);
    }

    public long getItemId(int position) {
        return position;
    }
    
	public int getItemViewType(int position) {
		// TODO Auto-generated method stub
		ChatMsg entity = coll.get(position);
	 	
	 	if (entity.isComMeg)
	 	{
	 		return IMsgViewType.IMVT_COM_MSG;
	 	}else{
	 		return IMsgViewType.IMVT_TO_MSG;
	 	}
	 	
	}

	public int getViewTypeCount() {
		// TODO Auto-generated method stub
		return 2;
	}
	
    public View getView(int position, View convertView, ViewGroup parent) {
    	
    	ChatMsg entity = coll.get(position);
    	boolean isComMsg = entity.isComMeg;
    		
    	ViewHolder viewHolder = null;
	    if (convertView == null)
	    {
	    	  viewHolder = new ViewHolder();

	    	  if(isComMsg)
			  {
				  convertView = inflater_.inflate(R.layout.chat_item_msg_text_left, null);
			  }else{
				  convertView = inflater_.inflate(R.layout.chat_item_msg_text_right, null);

				  viewHolder.sendstate_fail = convertView.findViewById(R.id.tv_sendstate_fail);
				  viewHolder.sendstate_sending = convertView.findViewById(R.id.tv_sendstate_sending);
			  }

			  viewHolder.sendTime = (TextView) convertView.findViewById(R.id.tv_sendtime);
			  viewHolder.content = (TextView) convertView.findViewById(R.id.tv_chatcontent);
			  viewHolder.avatar = (ImageView)convertView.findViewById(R.id.iv_userhead);
			  viewHolder.isComMsg = isComMsg;

			  viewHolder.avatar.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View view) {
					action_.onAvatarClick((ChatMsg)view.getTag());
				}
	            	
	          });

			  viewHolder.content.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View view) {
					action_.onContentClick((ChatMsg)view.getTag());
				}
	            	
	          });

			  convertView.setTag(viewHolder);
	    }else{
	        viewHolder = (ViewHolder) convertView.getTag();
	    }

	    if(entity.show_time)
	    {
	    	viewHolder.sendTime.setVisibility(View.VISIBLE);
	    	viewHolder.sendTime.setText(entity.date);
	    }
	    else
	    {
	    	viewHolder.sendTime.setVisibility(View.GONE);
	    }
	    
	    EmotionUtil.showEmotion(ctx, viewHolder.content, entity.text);
	    
	    if(entity.avatar != null)
	    	viewHolder.avatar.setImageURI(entity.avatar);
	    
	    viewHolder.avatar.setTag(entity);
	    viewHolder.content.setTag(entity);

	    if(!isComMsg)
	    	viewHolder.setState(entity.state);
	    
	    return convertView;
    }

    static class ViewHolder { 
        public View sendstate_fail;
        public View sendstate_sending;
        
        public TextView sendTime;
        public TextView content;
        public boolean isComMsg = true;
        
        public ImageView avatar;
        
        public void setState(SendState state)
        {
        	switch(state)
        	{
        	case Finished:
        		if(sendstate_fail.getVisibility() == View.VISIBLE)
        			sendstate_fail.setVisibility(View.INVISIBLE);
        		
        		if(sendstate_sending.getVisibility() == View.VISIBLE)
        			sendstate_sending.setVisibility(View.INVISIBLE);
        		break;
        	case Sending:
        		if(sendstate_fail.getVisibility() == View.VISIBLE)
        			sendstate_fail.setVisibility(View.INVISIBLE);
        		
        		if(sendstate_sending.getVisibility() == View.INVISIBLE)
        			sendstate_sending.setVisibility(View.VISIBLE);
        		break;
        	case Failed:
        		if(sendstate_fail.getVisibility() == View.INVISIBLE)
        			sendstate_fail.setVisibility(View.VISIBLE);
        		
        		if(sendstate_sending.getVisibility() == View.VISIBLE)
        			sendstate_sending.setVisibility(View.INVISIBLE);
        		break;
        	}
        }
    }

}
