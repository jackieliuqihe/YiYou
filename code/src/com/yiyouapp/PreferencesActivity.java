package com.yiyouapp;

import org.puremvc.java.interfaces.INotification;

import com.baidu.mobstat.StatService;
import com.yiyouapp.cmd.LoginCmd;
import com.yiyouapp.proxy.LoginProxy;
import com.yiyouapp.proxy.LogoutProxy;
import com.yiyouapp.proxy.RegProxy;
import com.yiyouapp.utils.SDCardUtil;
import com.yiyouapp.value.ReqData;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class PreferencesActivity extends MediatorActivity implements OnClickListener {

	public static final String NAME = "PreferencesActivity";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_preferences);

		findViewById(R.id.back_btn).setOnClickListener((OnClickListener) this);
		findViewById(R.id.exit_login_btn).setOnClickListener((OnClickListener) this);

		facade.registerProxy(new LogoutProxy());
		facade.registerCommand(LoginCmd.LOGOUT, new LoginCmd());
	}

	@Override
	protected void onDestroy() {
		facade.removeProxy(LogoutProxy.NAME);
		facade.removeCommand(LoginCmd.LOGOUT);
		
		super.onDestroy();
	}

	@Override
	protected String getName() {
		// TODO Auto-generated method stub
		return NAME;
	}

	@Override
	public String[] listNotificationInterests() {
		return new String[]{LogoutProxy.LOGOUT_COMPLETE};
	}

	@Override
	public void handleNotification(INotification notification) {
		String noteName = notification.getName();
		
		if(noteName.equals(LogoutProxy.LOGOUT_COMPLETE))
		{
			AppConfig.settings.setPass("");
			AppConfig.account.clearInfo();

			// ÍË³öÖ÷Ò³
			Activity act = (Activity)facade.retrieveMediator(MainActivity.NAME);
			if(act != null)
				act.finish();
			
			Intent intent = new Intent(this, LoginActivity.class);
		
			startActivity(intent);
			
			finish();
		}
	}

	@Override
	public void onClick(View v)
	{	
		switch(v.getId())
		{
		case R.id.back_btn:
			this.finish();
			break;
		case R.id.exit_login_btn:
			sendNotification(LoginCmd.LOGOUT, ReqData.create(this, ReqData.FailHandleType.Retry));
			break;
		}
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
	}

}
