package com.yiyouapp;

import java.io.File;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;
import org.puremvc.java.interfaces.INotification;

import com.baidu.mobstat.StatService;
import com.yiyouapp.cmd.AccountCmd;
import com.yiyouapp.cmd.GetImageCmd;
import com.yiyouapp.cmd.VisitUserCmd;
import com.yiyouapp.proxy.AccountProxy;
import com.yiyouapp.proxy.NetImageProxy;
import com.yiyouapp.proxy.RegProxy;
import com.yiyouapp.proxy.NetImageProxy.ImageType;
import com.yiyouapp.proxy.NetImageProxy.InputData;
import com.yiyouapp.proxy.NetImageProxy.OutputData;
import com.yiyouapp.proxy.RegProxy.Mission;
import com.yiyouapp.proxy.VisitUserProxy;
import com.yiyouapp.utils.AccountUtil;
import com.yiyouapp.utils.ImageUtil;
import com.yiyouapp.utils.SizeUtil;
import com.yiyouapp.utils.StringUtil;
import com.yiyouapp.utils.WorkUtil;
import com.yiyouapp.value.ReqData;
import com.yiyouapp.value.UserInfo;
import com.yiyouapp.value.UserInfo.UserLatestWorkThumb;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class UserCenterSelfActivity extends MediatorActivity implements OnClickListener {

	public static final String NAME = "UserCenterSelfActivity";

	private ImageView avatarView_;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_center_self);

		Button btn = (Button)findViewById(R.id.back_btn);
		btn.setOnClickListener((OnClickListener) this);

		btn = (Button)findViewById(R.id.edit_btn);
		btn.setOnClickListener((OnClickListener) this);

		findViewById(R.id.personal_works_rl).setOnClickListener(this);
		//findViewById(R.id.recommend_studio_rl).setOnClickListener(this);
		//findViewById(R.id.message_rl).setOnClickListener(this);

		ImageView iv = (ImageView)findViewById(R.id.center_bg);

        LayoutParams para;
        para = iv.getLayoutParams();

        para.width = AppConfig.screen_width;
        para.height = (int)((float)para.width * 0.521);
        iv.setLayoutParams(para);

		avatarView_ = (ImageView)findViewById(R.id.user_avatar);
		
		// 获取用户积分
		sendNotification(VisitUserCmd.GET_USER_SCORE, ReqData.create(this, ReqData.FailHandleType.Quite));
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		StatService.onPause(this);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		StatService.onResume(this);

		if(!StringUtil.isStringEmpty(AppConfig.account.info.user_type))
			showUserInfo();
		else
			sendNotification(AccountCmd.GET_ACCOUNT_INFO, ReqData.create(this, ReqData.FailHandleType.Quite));

		AccountUtil.setAvatar(avatarView_);
	}

	@Override
	protected String getName() {
		// TODO Auto-generated method stub
		return NAME;
	}

	@Override
	public String[] listNotificationInterests() {
		return new String[]{AccountProxy.GET_ACCOUNT_INFO_COMPLETE,
				VisitUserProxy.GET_USER_SCORE_COMPLETE,
				NetImageProxy.GET_IMAGE_COMPLETE};
	}

	@Override
	public void handleNotification(INotification notification) {
		ReqData req = (ReqData)notification.getBody();

		if(req.sender.equals(this))
		{
			String noteName = notification.getName();
			if(noteName.equals(AccountProxy.GET_ACCOUNT_INFO_COMPLETE))
			{
				showUserInfo();
			}
			else if(noteName.equals(VisitUserProxy.GET_USER_SCORE_COMPLETE))
			{
				String data = (String)req.output;

		    	try
		    	{
		            JSONObject obj = new JSONObject(data);
		            JSONObject jsonObj = obj.getJSONObject("thumbs");
		            
		        	JSONArray arr = jsonObj.getJSONArray("thumbs");

		        	ArrayList<UserLatestWorkThumb> thumbs = new ArrayList<UserLatestWorkThumb>();
		        	
		    		for(int i = 0; i < arr.length(); i++)
		    		{
		    			JSONObject thumbObj = (JSONObject)arr.get(i);
		    			UserLatestWorkThumb thumb = new UserLatestWorkThumb();

		    			thumb.thumb_path = (String)thumbObj.getString("file_path");
		    			thumb.thumb_ratio = (float)thumbObj.getDouble("thumb_ratio");
		    			
		    			thumb.thumb_path = thumb.thumb_path.replace("*", "t1");
		    			
		    			thumbs.add(thumb);
		    		}

		            AppConfig.account.info.work_count = jsonObj.getInt("work_count");
					AppConfig.account.info.score = obj.getInt("score");
		    		AppConfig.account.info.thumbs = thumbs;
		    		
					AppConfig.account.saveAccount();

					TextView scoreTV = (TextView)findViewById(R.id.score);
					scoreTV.setText(String.valueOf(AppConfig.account.info.score) + " 积分");
					
					showUserInfo();
					
			    } catch (Exception e) {
				}
			}
			else if(noteName.equals(NetImageProxy.GET_IMAGE_COMPLETE))
			{
				// 设置头像已经更新完
				InputData input = (InputData)req.input;
				//AppConfig.avatars_update.setUpdate(input.key);
				OutputData output = (OutputData)req.output;

				LinearLayout ll = (LinearLayout)findViewById(R.id.personal_works);
				
				output.bitmap = ImageUtil.decodingBytes(output.data);
				
				if(output.bitmap != null)
				{
	    			// 缓存起来
	    			AppConfig.cache.setImage(input.key, output.bitmap, output.data);
				
					// 释放不使用的空间
					output.data = null;

					ImageView iv = (ImageView)ll.getChildAt(input.position);
	            	if(iv == null)
	            		return;
	            	
	            	iv.setImageBitmap(output.bitmap);
				}
            	
			}
		}
	}

	private void showUserInfo()
	{
		UserInfo info = AppConfig.account.info;
		
		TextView userName = (TextView)findViewById(R.id.top_title_bar_title);
		if(!StringUtil.isStringEmpty(info.user_name))
			userName.setText(info.user_name);
		
		ImageView sexType = (ImageView)findViewById(R.id.sex_type);
		sexType.setVisibility(View.VISIBLE);
		if(!info.sex.equals("F"))
        	sexType.setImageResource(R.drawable.icon_male);
        else
        	sexType.setImageResource(R.drawable.icon_female);
		
		TextView userType = (TextView)findViewById(R.id.user_type);
		if(info.user_type.length() == 6)
			userType.setText(WorkUtil.getUserTypeNameFromValue(info.user_type));
		else if(AppConfig.settings.getUserType().length() == 6)
			userType.setText(WorkUtil.getUserTypeNameFromValue(AppConfig.settings.getUserType()));

		TextView location = (TextView)findViewById(R.id.user_location);
		if(!StringUtil.isStringEmpty(info.user_city))
			location.setText(info.user_city);

		TextView user_desc = (TextView)findViewById(R.id.sign);
		if(!StringUtil.isStringEmpty(info.user_desc))
			user_desc.setText(info.user_desc);
		else
			user_desc.setText("有点懒，未填写！");
		
		TextView trainCity = (TextView)findViewById(R.id.want_train_city);
		if(!StringUtil.isStringEmpty(info.train_city))
			trainCity.setText(info.train_city);
		else
			trainCity.setText("有点懒，未填写！");

		TextView contacts = (TextView)findViewById(R.id.mobile);
		if(!StringUtil.isStringEmpty(info.contacts))
			contacts.setText(info.contacts);
		else
			contacts.setText("有点懒，未填写！");

		TextView school = (TextView)findViewById(R.id.school);
		if(!StringUtil.isStringEmpty(info.school))
			school.setText(info.school);
		else
			school.setText("有点懒，未填写！");

		TextView regTime = (TextView)findViewById(R.id.reg_time);
		if(!StringUtil.isStringEmpty(info.reg_time))
			regTime.setText(StringUtil.formatDate(info.reg_time));

		TextView score = (TextView)findViewById(R.id.score);
		score.setText(String.valueOf(info.score) + " 积分");
		
		if(info.work_count > 0)
		{
			// 显示用户作品
			TextView worksCount = (TextView)findViewById(R.id.personal_works_count);
			worksCount.setVisibility(View.VISIBLE);
			worksCount.setText(String.valueOf(info.work_count));
			
			// 作品缩略图
			findViewById(R.id.personal_works_nocontent).setVisibility(View.GONE);
			
			LinearLayout ll = (LinearLayout)findViewById(R.id.personal_works);
			ll.setVisibility(View.VISIBLE);
			
			int count = info.thumbs.size();
			if(count > 4)
				count = 4;

			// thumb
			ArrayList<ReqData> reqList = new ArrayList<ReqData>();
			int ivCount = ll.getChildCount();
			
			for(int i = 0; i < count; i++)
			{
				UserLatestWorkThumb thumb = info.thumbs.get(count - i - 1);
				
				ImageView iv = (ImageView)ll.getChildAt(ivCount - i - 1);
				iv.setVisibility(View.VISIBLE);

		        LayoutParams para = iv.getLayoutParams();

		        para.width = (int)((float)140 * thumb.thumb_ratio);
		        para.height = 140;
		        
		        iv.setLayoutParams(para);

				String key = StringUtil.getFileNameFromPath(thumb.thumb_path);
				if(!AppConfig.cache.isExist(key))
				{
					ReqData req = ReqData.create(this, ReqData.FailHandleType.Quite);
					InputData input = new InputData();
					
					input.url = StringUtil.getFileUrl(thumb.thumb_path);
					input.cachable = true;
					input.type = ImageType.Thumbnail;
					input.position = ivCount - i - 1;
					input.key = key;
					
					req.input = input;
					
					reqList.add(req);
				}
				else
				{
			        Bitmap avatarImage = AppConfig.cache.mem.getImage(key);
			        if(avatarImage != null)
			        {
			        	iv.setImageBitmap(avatarImage);
			        }
			        else
			        {
			        	iv.setImageBitmap(null);
			        	
			        	Uri uri = AppConfig.cache.sdcard.getExistFileUri(key);
			        	if(uri != null)
			        		iv.setImageURI(uri);
			        }
				}
			}

			if(reqList.size() > 0)
				sendNotification(GetImageCmd.GET_IMAGE, reqList);
		}
	}
	
	@Override
	public void onClick(View v) {
		
		Intent intent;
		
		switch(v.getId())
		{
		case R.id.back_btn:
			this.finish();
			break;
		case R.id.edit_btn:

			intent = new Intent(this, UserEditInfoActivity.class);
			startActivity(intent);

			break;
		case R.id.personal_works_rl:

			if(AppConfig.account.info == null || AppConfig.account.info.work_count <= 0)
				break;
			
			intent = new Intent(this, WorkListActivity.class);
			intent.putExtra(AppConfig.INTENT_USER_ID, AppConfig.account.info.user_id);
			intent.putExtra(AppConfig.INTENT_USER_NAME, "我的作品");
			intent.putExtra(AppConfig.INTENT_COLLECT, false);
			
			startActivity(intent);
			
			break;
			/*
		case R.id.recommend_studio_rl:
			break;
		case R.id.message_rl:
			
			intent = new Intent(this, MsgUsersListActivity.class);
			startActivity(intent);

			break;*/
		}
	}

}
