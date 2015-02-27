package com.yiyouapp.proxy;

import java.io.File;
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
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.yiyouapp.AppConfig;
import com.yiyouapp.cmd.NetErrorCmd;
import com.yiyouapp.cmd.NetInfoCmd;
import com.yiyouapp.proxy.NetTextProxy.NetState;
import com.yiyouapp.utils.Location;
import com.yiyouapp.utils.StringUtil;
import com.yiyouapp.utils.WorkUtil;
import com.yiyouapp.value.KeyValue;
import com.yiyouapp.value.ReqData;
import com.yiyouapp.value.UserInfo;
import com.yiyouapp.value.Work;

public class UserListProxy extends NeedLoginProxy {

	public static class UserListInput
	{
		public int user_id;
		public int user_type;
		
		public String est_dist;
	}
	
	public static String NAME = "UserListProxy";

	public static String GET_USER_LIST_COMPLETE = "get_user_list_complete";
	public static String GET_USER_LIST_FAILED = "get_user_list_failed";

	private String reqAction;
	
	public UserListProxy() {
		super(NAME);
	}

	/*
	 * 获取用户列表
	 */
	public void getUserList(ReqData req)
	{
		reqAction = "get_user_list";
		
		super.setReqData(req);
		super.login(req);
	}

	public void getNearUsers(ReqData req)
	{
		reqAction = "get_near_users";
		
		super.setReqData(req);
		super.login(req);
	}

	private void doGetList(ReqData req)
	{
		UserListInput in = (UserListInput)req.input;

		RequestParams params = new RequestParams();
		params.add("user_type", String.valueOf(in.user_type));
		params.add("user_id", String.valueOf(in.user_id));
		
		if(reqAction.equals("get_near_users"))
		{
			params.add("lat", String.valueOf((int)(AppConfig.latitude *  1000000)));
			params.add("lon", String.valueOf((int)(AppConfig.longitude *  1000000)));
			params.add("est_dist", in.est_dist);
		}
		
		post(StringUtil.getURL("user", reqAction), params);
	}

	@Override
	protected void onSuccessResult(ReqData req, String reason, String data)
	{
		ArrayList<UserInfo> users = WorkUtil.jsonToBriefUserInfo(data);

		req.output = users;
    	
    	sendNotification(GET_USER_LIST_COMPLETE, req);
	}

	@Override
	protected void onLoginSuccess(ReqData req)
	{
		doGetList(req);
	}

	@Override
	protected void onResult(ReqData req, NetState state, String data)
	{
		super.onResult(req, state, data);
		
		switch(state)
		{
		case Fail:
		case Cancel:
			sendNotification(GET_USER_LIST_FAILED, req);
			break;
		}
	}

}
