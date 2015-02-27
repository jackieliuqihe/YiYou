package com.yiyouapp.proxy;

import java.io.File;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.puremvc.java.patterns.proxy.Proxy;

import android.graphics.BitmapFactory;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.BinaryHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.yiyouapp.AppConfig;
import com.yiyouapp.cmd.NetErrorCmd;
import com.yiyouapp.cmd.NetInfoCmd;
import com.yiyouapp.proxy.NetImageProxy.InputData;
import com.yiyouapp.proxy.NetImageProxy.OutputData;
import com.yiyouapp.proxy.NetTextProxy.NetState;
import com.yiyouapp.utils.AccountUtil;
import com.yiyouapp.utils.Location;
import com.yiyouapp.utils.SDCardUtil;
import com.yiyouapp.utils.StringUtil;
import com.yiyouapp.utils.WorkUtil;
import com.yiyouapp.value.KeyValue;
import com.yiyouapp.value.ReqData;
import com.yiyouapp.value.UserInfo;
import com.yiyouapp.value.Work;

public class AccountProxy extends NeedLoginProxy {

	public enum InfoType
	{
		SetDevInfo,
		SetInfo, // 设置信息
		GetInfo, // 获取信息
		GetAvatar, // 获取头像
	}
	
	public static String NAME = "AccountProxy";

	public static String GET_ACCOUNT_INFO_COMPLETE = "get_account_info_complete";
	public static String SET_ACCOUNT_INFO_COMPLETE = "set_account_info_complete";

	private InfoType infoType_;

	private AsyncHttpClient avatar_client_ = null;
	
	public AccountProxy() {
		super(NAME);
	}

	/*
	 * 获取自己的用户信息
	 */
	public void getAccountInfo(ReqData req)
	{
		infoType_ = InfoType.GetInfo;
		super.setReqData(req);
		super.login(req);
	}

	public void setAccountInfo(ReqData req)
	{
		infoType_ = InfoType.SetInfo;
		super.setReqData(req);
		super.login(req);
	}

	public void setDeviceInfo()
	{
		infoType_ = InfoType.SetDevInfo;
		
		RequestParams params = new RequestParams();
		params.add("uid", AppConfig.settings.getDeviceUserID());
		params.add("cid", AppConfig.settings.getDeviceChannelID());
		
		post(StringUtil.getURL("user", "set_self_dev"), params);
	}
	
	private void doSetInfo(ReqData req)
	{
		post(StringUtil.getURL("user", "set_self_info"), (RequestParams)req.input);
	}

	private void doGetInfo(ReqData req)
	{
		post(StringUtil.getURL("user", "get_self_info"), null);
	}

	public void doGetAvatar(ReqData req)
	{
		if(StringUtil.isStringEmpty(AppConfig.account.info.avatar_path) ||
				SDCardUtil.isFileExist(AccountUtil.getCurrAccountDir() + AppConfig.FILENAME_AVATAR))
			return;
		
		if(avatar_client_ == null)
		{
			avatar_client_ = new AsyncHttpClient();
	        avatar_client_.setCookieStore(AppConfig.cookie);
		}
		
		BinaryHttpResponseHandler handler = new BinaryHttpResponseHandler() {

			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
				//super.onSuccess(statusCode, headers, responseBody);
				if(statusCode / 100 == 2)
					SDCardUtil.writeFile(AccountUtil.getCurrAccountDir() + AppConfig.FILENAME_AVATAR, responseBody);
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
				//super.onFailure(statusCode, headers, responseBody, error);

			}
	    };

	    String avatar256 = AppConfig.account.info.avatar_path.replace("*", "256");
		
	    avatar_client_.get(StringUtil.getFileUrl(avatar256), null, handler);
	}

	@Override
	protected void onSuccessResult(ReqData req, String reason, String data)
	{
		switch(infoType_)
		{
		case SetInfo:
    		sendNotification(SET_ACCOUNT_INFO_COMPLETE, req);
    		break;
		case GetInfo:
			AppConfig.account.info = WorkUtil.jsonToUserInfo(data);
			if(!AppConfig.account.IsInfoValid())
			{
				this.setInfo("未能获取用户信息");
				sendNotification(NetInfoCmd.NET_INFO, req);
				break;
			}

			// 获取头像
			doGetAvatar(req);
			
	        req.output = AppConfig.account.info;
	        
	        AppConfig.account.saveAccount();
    		
    		sendNotification(GET_ACCOUNT_INFO_COMPLETE, req);
	    	break;
		case GetAvatar:
    		break;
		}
	}

	@Override
	protected void onLoginSuccess(ReqData req)
	{
		switch(infoType_)
		{
		case SetInfo:
			doSetInfo(req);
    		break;
		case GetInfo:
			doGetInfo(req);
	    	break;
		}
	}

}
