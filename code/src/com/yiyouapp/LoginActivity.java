package com.yiyouapp;

import java.io.File;

import org.puremvc.java.interfaces.IMediator;
import org.puremvc.java.interfaces.INotification;

import com.baidu.mobstat.StatService;
import com.loopj.android.http.RequestParams;
import com.yiyouapp.cmd.AccountCmd;
import com.yiyouapp.cmd.LoginCmd;
import com.yiyouapp.cmd.RegCmd;
import com.yiyouapp.proxy.LoginProxy;
import com.yiyouapp.proxy.RegProxy;
import com.yiyouapp.proxy.RegProxy.Mission;
import com.yiyouapp.utils.AccountUtil;
import com.yiyouapp.utils.ImageUtil;
import com.yiyouapp.utils.Location;
import com.yiyouapp.value.ReqData;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class LoginActivity extends MediatorActivity implements OnClickListener {

	public static final String NAME = "LoginActivity";
	
	private Button login_btn_;
	
	private ImageView avatarView_;
	private EditText loginMobile_;
	private EditText loginPass_;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		login_btn_ = (Button)findViewById(R.id.login_btn);
		login_btn_.setOnClickListener((OnClickListener) this);

		findViewById(R.id.register_btn).setOnClickListener((OnClickListener) this);

		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		
		avatarView_ = (ImageView)findViewById(R.id.login_avatar);
		loginMobile_ = (EditText)findViewById(R.id.login_mobile);
		loginPass_ = (EditText)findViewById(R.id.login_pass);
	}

	// 是否是登录后进的页
	@Override
	protected boolean isLoginedPage()
	{
		return false;
	}

	@Override
	protected String getName() {
		// TODO Auto-generated method stub
		return NAME;
	}

	@Override
	public String[] listNotificationInterests() {
		return new String[]{LoginProxy.LOGIN_COMPLETE};
	}

	@Override
	public void handleNotification(INotification notification) {
		String noteName = notification.getName();
		
		if(noteName.equals(LoginProxy.LOGIN_COMPLETE))
		{
			AppConfig.settings.setMobile(loginMobile_.getText().toString());
			AppConfig.settings.setPass(loginPass_.getText().toString());
			
			Intent intent = new Intent(this, MainActivity.class);
		
			startActivity(intent);
			
			hideProgress();
		}
	}

	@Override
	public void showProgress(String info) {
		login_btn_.setEnabled(false);
		super.showProgress(info);
	}

	@Override
	public void hideProgress() {
		super.hideProgress();
		login_btn_.setEnabled(true);
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
		
		AccountUtil.setAvatar(avatarView_);
		
		loginMobile_.setText(AppConfig.settings.getMobile());
		loginPass_.setText(AppConfig.settings.getPass());
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		
		if (keyCode == KeyEvent.KEYCODE_BACK) 
		{
	        Intent intent = new Intent(Intent.ACTION_MAIN);
	        
	        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);      
	        intent.addCategory(Intent.CATEGORY_HOME);
	        
	        startActivity(intent);
	        
			AppConfig.Uninitialize();
	        return true;
	    }
		
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onClick(View v) {
		
		Intent intent;
		
		switch(v.getId())
		{
		case R.id.login_btn:

			showProgress("正在登录...");

			ReqData req = ReqData.create(this, ReqData.FailHandleType.Retry);
			
			RequestParams params = new RequestParams();
			params.add("mobile", loginMobile_.getText().toString());
			params.add("pass", loginPass_.getText().toString());
			
			req.input = params;
			
			sendNotification(LoginCmd.LOGIN, req);

			break;
		case R.id.register_btn:

			intent = new Intent(this, RegStep1Activity.class);
		
			startActivity(intent);

			break;
		}
	}

}
