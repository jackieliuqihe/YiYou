package com.yiyouapp;

import org.puremvc.java.interfaces.INotification;

import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.baidu.kirin.StatUpdateAgent;
import com.yiyouapp.controls.Updater;
import com.yiyouapp.proxy.LoginProxy;
import com.yiyouapp.utils.DialogUtil;
import com.yiyouapp.utils.EmotionUtil;
import com.yiyouapp.utils.NotificationUtil;
import com.yiyouapp.MediatorActivity;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.Menu;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

public class StartupActivity extends MediatorActivity {
//public class StartupActivity extends Activity {
	public static final String NAME = "StartupActivity";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		// app刚启动时，设置标志
		AppConfig.is_start = true;
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_startup);

		// 初始化云消息推送
		if (!AppConfig.settings.getDeviceBind()) {
			Context context = getApplicationContext();
            PushManager.startWork(context,
                    PushConstants.LOGIN_TYPE_API_KEY,
                    AppConfig.baidu_api_key);
            // Push: 如果想基于地理位置推送，可以打开支持地理位置的推送的开关
            PushManager.enableLbs(context);
        }
		
		// 启动时检查缓存
		AppConfig.cache.sdcard.manage();
		
		((TextView)findViewById(R.id.version_info_txt)).setText("版本号: V" + AppConfig.version_name);
		
		new Handler().postDelayed(new Runnable(){
			@Override
			public void run(){
				Intent intent;
				
				if(AppConfig.account.IsLogin())
					intent = new Intent(StartupActivity.this, MainActivity.class);
				else
					intent = new Intent(StartupActivity.this, LoginActivity.class);	
				
				startActivity(intent);
				StartupActivity.this.finish();
			}
		}, 1000);
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

}
