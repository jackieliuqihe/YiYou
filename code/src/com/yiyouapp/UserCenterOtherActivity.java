package com.yiyouapp;

import java.io.Serializable;
import java.util.ArrayList;

import org.puremvc.java.interfaces.INotification;

import com.baidu.mobstat.StatService;
import com.loopj.android.http.RequestParams;
import com.yiyouapp.cmd.GetImageCmd;
import com.yiyouapp.cmd.VisitUserCmd;
import com.yiyouapp.cmd.RegCmd;
import com.yiyouapp.proxy.NetImageProxy;
import com.yiyouapp.proxy.VisitUserProxy;
import com.yiyouapp.proxy.NetImageProxy.ImageType;
import com.yiyouapp.proxy.NetImageProxy.InputData;
import com.yiyouapp.proxy.NetImageProxy.OutputData;
import com.yiyouapp.utils.ImageUtil;
import com.yiyouapp.utils.Location;
import com.yiyouapp.utils.SizeUtil;
import com.yiyouapp.utils.StringUtil;
import com.yiyouapp.utils.WorkUtil;
import com.yiyouapp.value.TypeDef;
import com.yiyouapp.value.UserInfo;
import com.yiyouapp.value.ReqData;
import com.yiyouapp.value.UserInfo.UserLatestWorkThumb;
import com.yiyouapp.value.Work;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class UserCenterOtherActivity extends MediatorActivity implements OnClickListener {

	public static final String NAME = "UserCenterOtherActivity";

	private UserInfo userInfo_;
	
	// 从之前的id传过来
	private int user_id_;
	private String username_;
	
	private TextView attention_;
	
	private View bottom_rl_;
	private View attention_rl_;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_center_other);

		Button btn = (Button)findViewById(R.id.back_btn);
		btn.setOnClickListener((OnClickListener) this);

		attention_ = (TextView)findViewById(R.id.attention);
		bottom_rl_ = findViewById(R.id.bottom_rl);
		attention_rl_ = findViewById(R.id.attention_rl);
		
		findViewById(R.id.personal_works_rl).setOnClickListener(this);
		findViewById(R.id.attention_rl).setOnClickListener(this);
		findViewById(R.id.message_rl).setOnClickListener(this);

    	initLoading((ViewGroup)findViewById(R.id.center_rl), findViewById(R.id.center_ll));
    	loading_.setLoading();
    	bottom_rl_.setVisibility(View.GONE);
    	
		// 用户个人中心背景
		ImageView iv = (ImageView)findViewById(R.id.center_bg);

        LayoutParams para;
        para = iv.getLayoutParams();

        para.width = AppConfig.screen_width;
        para.height = (int)((float)para.width * 0.521);
        iv.setLayoutParams(para);

        // 获取用户信息
		user_id_ = getIntent().getIntExtra(AppConfig.INTENT_USER_ID, 0);
		username_ = getIntent().getStringExtra(AppConfig.INTENT_USER_NAME);
		
		ReqData req = ReqData.create(this, ReqData.FailHandleType.Quite);
		
		RequestParams params = new RequestParams();
		params.put("user_id", user_id_);
		
		req.input = params;
		
		sendNotification(VisitUserCmd.VISIT_USER_CENTER, req);
	}

	@Override
	protected String getName() {
		// TODO Auto-generated method stub
		return NAME;
	}

	@Override
	public String[] listNotificationInterests() {
		return new String[]{VisitUserProxy.GET_USER_INFO_COMPLETE,
				VisitUserProxy.GET_USER_INFO_FAILED,
				VisitUserProxy.SET_ATTENTION_COMPLETE,
				VisitUserProxy.SET_ATTENTION_FAILED,
				NetImageProxy.GET_IMAGE_COMPLETE};
	}

	@Override
	public void handleNotification(INotification notification) {
		ReqData req = (ReqData)notification.getBody();

		if(req.sender.equals(this))
		{
			String noteName = notification.getName();
			if(noteName.equals(VisitUserProxy.GET_USER_INFO_COMPLETE))
			{
				userInfo_ = (UserInfo)req.output;
				
				if(userInfo_ != null)
				{
					showUserInfo(userInfo_);
					
					// 设置加载完成
					loading_.setCompleted();
			    	bottom_rl_.setVisibility(View.VISIBLE);
				}
			}
			else if(noteName.equals(VisitUserProxy.GET_USER_INFO_FAILED))
			{
				loading_.setFailed();
			}
			else if(noteName.equals(VisitUserProxy.SET_ATTENTION_COMPLETE))
			{
				userInfo_.attention = !userInfo_.attention;

				attention_rl_.setEnabled(true);
				
				if(!userInfo_.attention)
				{
					attention_rl_.setVisibility(View.VISIBLE);
					attention_.setText("关注");
				}
				else
				{
					String desc;
					String sex = "他";
					
					if(!userInfo_.sex.equals("M"))
						sex = "她";
					
					desc = "您已经关注了" + sex + "\n可以收到" + sex + "发作品的通知啦";
					
					Toast.makeText(this, desc, Toast.LENGTH_LONG).show();
					
					attention_rl_.setVisibility(View.GONE);
					//attention_.setText("已关注");
				}
			}
			else if(noteName.equals(VisitUserProxy.SET_ATTENTION_FAILED))
			{
				attention_rl_.setEnabled(true);
			}
			else if(noteName.equals(NetImageProxy.GET_IMAGE_COMPLETE))
			{
				// 设置头像已经更新完
				InputData input = (InputData)req.input;;
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

	private void showUserInfo(UserInfo info)
	{
		// avatar
		ImageView avatarView = (ImageView)findViewById(R.id.user_avatar);
    	Uri uri = AppConfig.cache.sdcard.getExistFileUri(StringUtil.getFileNameFromPath(info.avatar64_path));
    	if(uri != null)
    		avatarView.setImageURI(uri);
    	else
    		avatarView.setImageResource(R.drawable.default_avatar_100);
    	
		TextView userName = (TextView)findViewById(R.id.top_title_bar_title);
		if(!StringUtil.isStringEmpty(info.user_name))
			userName.setText(info.user_name);

		if(!userInfo_.attention)
		{
			attention_rl_.setVisibility(View.VISIBLE);
			attention_.setText("关注");
		}
		else
		{
			attention_rl_.setVisibility(View.GONE);
			//attention_.setText("已关注");
		}
		
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

		// 用户距离
		boolean self_loc = Math.abs(AppConfig.longitude) > 0.01 && 
				Math.abs(AppConfig.latitude) > 0.01;

		TextView distance = (TextView)findViewById(R.id.user_distance);
        if(Math.abs(info.longitude) > 0.01 && Math.abs(info.latitude) > 0.01 && self_loc)
        {
            distance.setText(StringUtil.formatDistance(Location.getDistance(AppConfig.latitude, AppConfig.longitude, info.latitude, info.longitude)));
        }
		
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

		TextView scoreLabel = (TextView)findViewById(R.id.score_label);
		if(info.sex.equals("F"))
			scoreLabel.setText("她的积分");
		
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
			        	
			        	uri = AppConfig.cache.sdcard.getExistFileUri(key);
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
		case R.id.attention_rl:
			
			if(userInfo_ == null)
				break;
			
			RequestParams params = new RequestParams();

			params.put("to_user_id", userInfo_.user_id);
			params.put("attention", userInfo_.attention ? 0 : 1);
			
			ReqData req = ReqData.create(this, ReqData.FailHandleType.Notify);
			req.input = params;
			sendNotification(VisitUserCmd.SET_USER_ATTENTION, req);
			
			attention_rl_.setEnabled(false);
			
			break;
		case R.id.personal_works_rl:
			
			if(userInfo_ == null || userInfo_.work_count <= 0)
				break;
			
			intent = new Intent(this, WorkListActivity.class);
			intent.putExtra(AppConfig.INTENT_USER_ID, user_id_);
			intent.putExtra(AppConfig.INTENT_USER_NAME, username_ + "的作品");
			intent.putExtra(AppConfig.INTENT_COLLECT, false);
			
			startActivity(intent);
			
			break;
		case R.id.message_rl:

			if(userInfo_ == null)
				break;
			
			intent = new Intent(this, ChatActivity.class);

			intent.putExtra(AppConfig.INTENT_USER_ID, user_id_);
			intent.putExtra(AppConfig.INTENT_USER_NAME, username_);
			intent.putExtra(AppConfig.INTENT_AVATAR_PATH, userInfo_.avatar64_path);
			
			startActivity(intent);

			break;
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		StatService.onResume(this);

		if(!facade.hasMediator(NAME))
			facade.registerMediator(this);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		StatService.onPause(this);

		if(facade.hasMediator(NAME))
			facade.removeMediator(NAME);
	}

}
