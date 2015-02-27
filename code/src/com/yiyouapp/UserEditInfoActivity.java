package com.yiyouapp;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.UUID;

import org.puremvc.java.interfaces.INotification;

import com.baidu.mobstat.StatService;
import com.loopj.android.http.RequestParams;
import com.yiyouapp.cmd.AccountCmd;
import com.yiyouapp.controls.PhotoObtainer;
import com.yiyouapp.proxy.AccountProxy;
import com.yiyouapp.utils.AccountUtil;
import com.yiyouapp.utils.DialogUtil;
import com.yiyouapp.utils.ImageUtil;
import com.yiyouapp.utils.SDCardUtil;
import com.yiyouapp.utils.StringUtil;
import com.yiyouapp.utils.WorkUtil;
import com.yiyouapp.value.ReqData;
import com.yiyouapp.value.TypeDef;
import com.yiyouapp.value.UserInfo;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class UserEditInfoActivity extends MediatorActivity implements OnClickListener {

	public static final String NAME = "UserEditInfoActivity";

	private Button save_btn_;
	
	private UserInfo clone_;
	
	private Bitmap avatar_;
	private String sex_ = "M";
	private String train_city_ = "";

	private ImageView avatarView_;
	
	private EditText userName;
	private EditText contacts;
	private TextView trainCity;
	private EditText school;
	private EditText userDesc;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_edit_info);

		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		
		findViewById(R.id.back_btn).setOnClickListener((OnClickListener) this);

		save_btn_ = (Button)findViewById(R.id.save_btn);
		save_btn_.setOnClickListener((OnClickListener) this);

		findViewById(R.id.avatar_rl).setOnClickListener(this);
		findViewById(R.id.sex_rl).setOnClickListener(this);
		findViewById(R.id.want_train_city_rl).setOnClickListener(this);

		userName = (EditText)findViewById(R.id.nickname);
		contacts = (EditText)findViewById(R.id.contacts);
		trainCity = (TextView)findViewById(R.id.train_city);
		school = (EditText)findViewById(R.id.school);
		userDesc = (EditText)findViewById(R.id.sign);
		
		avatarView_ = (ImageView)findViewById(R.id.user_avatar);
		
		showUserInfo();
	}

	@Override
	protected String getName() {
		// TODO Auto-generated method stub
		return NAME;
	}

	@Override
	public String[] listNotificationInterests() {
		return new String[]{AccountProxy.SET_ACCOUNT_INFO_COMPLETE};
	}

	@Override
	public void handleNotification(INotification notification) {
		ReqData req = (ReqData)notification.getBody();

		if(req.sender.equals(this))
		{
			String noteName = notification.getName();
			if(noteName.equals(AccountProxy.SET_ACCOUNT_INFO_COMPLETE))
			{
				AppConfig.account.info = clone_;

				AppConfig.account.saveAccount();
		    	
		    	if(avatar_ != null)
		    		AccountUtil.saveAvatar(avatar_);
		    	
				this.hideProgress();
				Toast.makeText(this, "信息保存成功", Toast.LENGTH_SHORT).show();
				
				finish();
			}
		}
	}

	private void showUserInfo()
	{
		UserInfo info = AppConfig.account.info;
		if(info.user_type == null)
			return;
		
		TextView sexType = (TextView)findViewById(R.id.sex_type);
		if(!StringUtil.isStringEmpty(info.sex))
		{
			if(!info.sex.equals("F"))
				sexType.setText("男");
	        else
				sexType.setText("女");
			
			sex_ = info.sex;
		}
		
		if(!StringUtil.isStringEmpty(info.user_name))
			userName.setText(info.user_name);
		
		if(!StringUtil.isStringEmpty(info.contacts))
			contacts.setText(info.contacts);
		
		if(!StringUtil.isStringEmpty(info.train_city))
			trainCity.setText(info.train_city);
		train_city_ = info.train_city;

		if(!StringUtil.isStringEmpty(info.school))
			school.setText(info.school);

		if(!StringUtil.isStringEmpty(info.user_desc))
			userDesc.setText(info.user_desc);
	}

	private void uploadUserInfo()
	{
		UserInfo info = AppConfig.account.info;
		clone_ = info.clone();
		
		RequestParams params = new RequestParams();
		
		String value = userName.getText().toString().trim();
		if(!StringUtil.isName(value))
		{
			Toast.makeText(this, "用户名不对", Toast.LENGTH_SHORT).show();
			return;
		}
		if(!value.equals(info.user_name))
		{
			params.put("user_name", value);
			clone_.user_name = value;
		}

		value = contacts.getText().toString().trim();
		if(!value.equals(info.contacts))
		{
			params.put("contacts", value);
			clone_.contacts = value;
		}

		value = school.getText().toString().trim();
		if(!value.equals(info.school))
		{
			params.put("school", value);
			clone_.school = value;
		}
		
		value = userDesc.getText().toString().trim();
		if(!value.equals(info.user_desc))
		{
			params.put("description", value);
			clone_.user_desc = value;
		}

		if(!train_city_.equals(info.train_city))
		{
			params.put("train_city", train_city_);
			clone_.train_city = train_city_;
		}

		if(!sex_.equals(info.sex))
		{
			params.put("sex", sex_);
			clone_.sex = sex_;
		}

		if(avatar_ != null)
		{
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    	avatar_.compress(CompressFormat.JPEG, 90, baos);
	    	
			params.put("qqfile", (ByteArrayInputStream)new ByteArrayInputStream(baos.toByteArray()), UUID.randomUUID().toString() + "-avatar.jpg");
		}
		
		if(StringUtil.isStringEmpty(params.toString()))
		{
			Toast.makeText(this, "您未修改任何信息", Toast.LENGTH_SHORT).show();
			finish();
			return;
		}
		
		ReqData req = ReqData.create(this, ReqData.FailHandleType.Notify);
		req.input = params;
		
		this.showProgress("正在保存信息...");
		sendNotification(AccountCmd.SET_ACCOUNT_INFO, req);
	}

	@Override
	public void showProgress(String info) {
		save_btn_.setEnabled(false);
		super.showProgress(info);
	}

	@Override
	public void hideProgress() {
		super.hideProgress();
		save_btn_.setEnabled(true);
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

		if(avatar_ != null)
		{
			avatarView_.setImageBitmap(avatar_);
		}
		else
		{
			AccountUtil.setAvatar(avatarView_);
		}
	}

	@Override
	public void onClick(View v) {
		
		switch(v.getId())
		{
		case R.id.back_btn:
			this.finish();
			break;
		case R.id.save_btn:
			uploadUserInfo();
			break;
		case R.id.avatar_rl:
			DialogUtil.showPhotoSelectDialog(this);
			break;
		case R.id.sex_rl:

			new AlertDialog.Builder(this)
			.setSingleChoiceItems(new String[]{"男", "女"}, sex_.equals("F") ? 1 : 0, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					
					TextView sexType = (TextView)findViewById(R.id.sex_type);
					if(which == 0)
					{
						sexType.setText("男");
						sex_ = "M";
					}
					else
					{
						sexType.setText("女");
						sex_ = "F";
					}

					dialog.dismiss();
				}
			})
			.show(); 

			break;
		case R.id.want_train_city_rl:
			break;
		}
	}

    @Override  
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
    	if(requestCode != 3)
    	{
    		switch(requestCode)
    		{
    		case PhotoObtainer.GET_IMAGE:
    	    	Uri uri = PhotoObtainer.processUri(requestCode, resultCode, data);
    	    	if(uri != null)
    	    	{
        			PhotoObtainer.doCropImage(uri);
    	    	}
    	    	else
    	    	{
    	    		Toast.makeText(this, "您未拍照或选择图片", Toast.LENGTH_SHORT).show();
    	    	}
    			break;
    		case PhotoObtainer.CROP_IMAGE:
    			Bitmap bmp = PhotoObtainer.processBitmap(requestCode, resultCode, data);
    	    	if(bmp != null)
    	    	{
    	    		avatar_ = bmp;
    	    		
		    		ImageView userAvatar = (ImageView)findViewById(R.id.user_avatar);
		    		
		    		userAvatar.setImageBitmap(bmp);
    	    	}
    	    	else
    	    	{
    	    		Toast.makeText(this, "您未裁剪头像", Toast.LENGTH_SHORT).show();
    	    	}
    			break;
    		}
    	}
    }

}
