package com.yiyouapp.controls;

import java.util.HashMap;

import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.net.Uri;
import android.util.Log;

import com.baidu.kirin.CheckUpdateListener;
import com.baidu.kirin.KirinConfig;
import com.baidu.kirin.PostChoiceListener;
import com.baidu.kirin.StatUpdateAgent;
import com.baidu.kirin.objects.KirinCheckState;
import com.yiyouapp.AppConfig;
import com.yiyouapp.utils.DialogUtil;

public class Updater implements CheckUpdateListener, PostChoiceListener {

	private static Updater inst_;
	
	// 检查更新
	public static void checkUpdate()
	{
		Activity ctx = AppConfig.current_activity;
		if(ctx == null)
			return;
		
		if(inst_ == null)
			inst_ = new Updater();
		
		//StatUpdateAgent.setTestMode();
		StatUpdateAgent.checkUpdate(ctx, AppConfig.is_start, inst_);
	}
	
	@Override
	public void PostUpdateChoiceResponse(JSONObject response) {
		// TODO Auto-generated method stub
	}

	/**
	 * 检查更新的结果
	 */
	@Override
	public void checkUpdateResponse(KirinCheckState state,
			HashMap<String, String> dataContainer) {
		// TODO Auto-generated method stub
		if (state == KirinCheckState.ALREADY_UP_TO_DATE) 
		{
			// KirinAgent.postUserChoice(getApplicationContext(), choice);
		} 
		else if(state == KirinCheckState.ERROR_CHECK_VERSION)
		{
		} 
		else if(state == KirinCheckState.NEWER_VERSION_FOUND)
		{
			String isForce = dataContainer.get("updatetype");
			String noteInfo = dataContainer.get("note");
			String publicTime = dataContainer.get("time");
			String appUrl = dataContainer.get("appurl");
			String appName = dataContainer.get("appname");
			String newVersionName = dataContainer.get("version");
			String newVersionCode = dataContainer.get("buildid");
			String attachInfo = dataContainer.get("attach");

			showNewerVersionFoundDialog(appUrl, noteInfo);
		}
	}

	private void showNewerVersionFoundDialog(final String appUrl, String content) 
	{
		final Activity ctx = AppConfig.current_activity;
		if(ctx == null)
			return;
		
        AlertDialog.Builder builder = new Builder(ctx);
        
        builder.setMessage(content);  
        
        builder.setPositiveButton("现在升级", new OnClickListener() {           
            @Override  
            public void onClick(DialogInterface dialog, int which) {

            	StatUpdateAgent.postUserChoice(ctx, KirinConfig.CONFIRM_UPDATE, inst_);
            	
				Intent intent=new Intent();
				intent.setAction("android.intent.action.VIEW");
				
				Uri CONTENT_URI_BROWSERS = Uri.parse(appUrl);
				
				intent.setData(CONTENT_URI_BROWSERS);
				intent.setClassName("com.android.browser", "com.android.browser.BrowserActivity");
				
				ctx.startActivity(intent);
				
                dialog.dismiss();
            }  
        });  
        
        builder.setNegativeButton("暂不升级", new OnClickListener() {             
            @Override  
            public void onClick(DialogInterface dialog, int which) {
            	StatUpdateAgent.postUserChoice(ctx, KirinConfig.LATER_UPDATE, inst_);
            	
                dialog.dismiss();                 
            }  
        });
        
        Dialog noticeDialog = builder.create();  
        noticeDialog.show();
	}
}
