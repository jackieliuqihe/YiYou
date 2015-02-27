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
import com.yiyouapp.cmd.NetErrorCmd;
import com.yiyouapp.cmd.NetInfoCmd;
import com.yiyouapp.utils.StringUtil;
import com.yiyouapp.value.KeyValue;
import com.yiyouapp.value.ReqData;

public class LogoutProxy extends NetTextProxy {

	public static String NAME = "LogoutProxy";

	public static String LOGOUT_COMPLETE = "logout_complete";

	public LogoutProxy() {
		super(NAME);
	}

	/*
	 * 退出登录操作
	 */
	public void logout(ReqData req)
	{
		super.setReqData(req);
		post(StringUtil.getURL("login", "logout"), null);
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
					succ = false;
				}
				
				setInfo(info);
			} catch (JSONException e) {
				setInfo("抱歉，发生网络数据错误");
				succ = false;
			}

			if(succ)
			{
				sendNotification(LOGOUT_COMPLETE, req);
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

}
