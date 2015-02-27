package com.yiyouapp.proxy;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.puremvc.java.patterns.proxy.Proxy;

import android.graphics.BitmapFactory;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.yiyouapp.AppConfig;
import com.yiyouapp.cmd.AccountCmd;
import com.yiyouapp.cmd.NetErrorCmd;
import com.yiyouapp.cmd.NetInfoCmd;
import com.yiyouapp.utils.StringUtil;
import com.yiyouapp.value.KeyValue;
import com.yiyouapp.value.ReqData;

public class NeedLoginProxy extends NetTextProxy {

	private boolean is_loginning = false;
	
	public NeedLoginProxy(String name) {
		super(name);
	}

	/*
	 * 登录操作
	 */
	protected void login(ReqData req)
	{
		if(!AppConfig.account.session_is_login)
		{
			RequestParams params = new RequestParams();
			params.add("mobile", AppConfig.settings.getMobile());
			params.add("pass", AppConfig.settings.getPass());
			
			is_loginning = true;
	        post(StringUtil.getURL("login", "login"), params);
		}
		else
		{
			is_loginning = false;
			onLoginSuccess(req);
		}
	}

	protected boolean isLoginning()
	{
		return is_loginning;
	}
	
	@Override
	protected void onResult(ReqData req, NetState state, String data)
	{
		switch(state)
		{
		case Normal:

			boolean succ = true;
			String reason = "";
			String info = "";
			
			try {
				JSONObject obj = new JSONObject(data);
				
				reason = obj.getString("r");
				info = obj.getString("i");
				
				if(!obj.getString("s").equals("true"))
				{
					if(reason.equals("notlogin"))
					{
						// 尝试登录
						AppConfig.account.session_is_login = false;
						login(req);
						return;
					}
					
					succ = false;
				}
				
				setInfo(info);
			} catch (JSONException e) {
				setInfo("抱歉，发生网络数据错误");
				succ = false;
			}

			if(succ)
			{
				if(is_loginning)
				{
					is_loginning = false;
					
					// 提交用户设备id
					sendNotification(AccountCmd.SET_DEVICE_INFO);
					
					AppConfig.account.session_is_login = true;
					onLoginSuccess(req);
				}
				else
				{
					onSuccessResult(req, reason, info);
				}
			}
			else
			{
				sendNotification(NetInfoCmd.NET_INFO, req);
			}
			
			break;
		case Fail:
			sendNotification(NetErrorCmd.NET_ERROR, req);
			break;
		case Cancel:
			break;
		}
	}

	protected void onSuccessResult(ReqData req, String reason, String data)
	{
		
	}
	
	protected void onLoginSuccess(ReqData req)
	{
		
	}
}
