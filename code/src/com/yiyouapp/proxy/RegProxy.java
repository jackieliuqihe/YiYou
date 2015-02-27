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

public class RegProxy extends NetTextProxy {

	public enum Mission
	{
		GetCode,
		VerifyCode,
		Register
	}
	
	public static String NAME = "RegProxy";
	
	public static String REG_COMPLETE = "reg_complete";

	private Mission curr_mission_ = Mission.GetCode;
	
	public RegProxy() {
		super(NAME);
	}

	/*
	 * 获取短信验证码
	 */
	public void getCode(ReqData req)
	{
		super.setReqData(req);
		
		String mobile = (String)req.input;
		curr_mission_ = Mission.GetCode;
		
		RequestParams params = new RequestParams();
		params.add("mobile", mobile);
		
        post(StringUtil.getURL("register", "get_code"), params);
	}

	/*
	 * 验证短信验证码
	 */
	public void verifyCode(ReqData req)
	{
		super.setReqData(req);
		
		String code = (String)req.input;
		curr_mission_ = Mission.VerifyCode;
		
		RequestParams params = new RequestParams();
		params.add("code", code);
		
        post(StringUtil.getURL("register", "verify_code"), params);
	}

	/*
	 * 注册用户
	 */
	public void register(ReqData req)
	{
		super.setReqData(req);
		
		KeyValue[] values = (KeyValue[])req.input;
		
		curr_mission_ = Mission.Register;
		
		RequestParams params = new RequestParams();
		
		for(int i = 0; i < values.length; i++)
			params.add(values[i].key, (String)values[i].value);
		
        post(StringUtil.getURL("register", "register"), params);
	}

	@Override
	protected void onResult(ReqData req, NetState state, String data) 
	{
		switch(state)
		{
		case Normal:
			
			boolean succ = true;
			
			try {
				JSONObject obj = new JSONObject(data);
				if(!obj.getString("s").equals("true"))
				{
					succ = false;
				}
				setInfo(obj.getString("i"));
			} catch (JSONException e) {
				setInfo("抱歉，发生未知错误");
				succ = false;
			}
			
			if(succ)
			{
				sendNotification(REG_COMPLETE, req);
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

	public Mission getMission()
	{
		return curr_mission_;
	}
}
