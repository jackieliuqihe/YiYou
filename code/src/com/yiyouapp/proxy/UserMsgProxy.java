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
import com.yiyouapp.value.ChatMsg;
import com.yiyouapp.value.KeyValue;
import com.yiyouapp.value.ReqData;
import com.yiyouapp.value.Work;

public class UserMsgProxy extends NeedLoginProxy {

	public static class UserMsgInput
	{
		public long sending_time;
		
		public boolean latest;
		public int user_id;
		public int user_msg_id;
		public String msg;
	}
	
	public static String NAME = "UserMsgProxy";

	public static String SEND_MSG_COMPLETE = "send_msg_complete";
	public static String SEND_MSG_FAILED = "send_msg_failed";
	
	public static String GET_MSG_LIST_COMPLETE = "get_msg_list_complete";
	public static String GET_MSG_LIST_FAILED = "get_msg_list_failed";

	private String reqAction_;
	
	private RequestParams params_;
	
	public UserMsgProxy() {
		super(NAME);
	}

	/*
	 * 获取用户留言
	 */
	public void getMsgList(ReqData req)
	{
		params_ = new RequestParams();
		
		UserMsgInput input = (UserMsgInput)req.input;

		params_.add("l", String.valueOf(input.latest ? 1 : 0));
		params_.add("id", String.valueOf(input.user_msg_id));
		params_.add("t", String.valueOf(input.user_id));
		
		reqAction_ = "get_user_msg";
		super.setReqData(req);
		super.login(req);
	}

	public void sendMsg(ReqData req)
	{
		params_ = new RequestParams();
		
		UserMsgInput input = (UserMsgInput)req.input;

		params_.add("id", String.valueOf(input.user_msg_id));
		params_.add("msg", String.valueOf(input.msg));
		params_.add("t", String.valueOf(input.user_id));
		
		reqAction_ = "add_user_msg";
		super.setReqData(req);
		super.login(req);
	}

	private void doGetList(ReqData req)
	{
		post(StringUtil.getURL("user", reqAction_), params_);
	}

	@Override
	protected void onSuccessResult(ReqData req, String reason, String data)
	{
		if(reqAction_.equals("get_user_msg"))
		{
			req.output = data;
	    	
	    	sendNotification(GET_MSG_LIST_COMPLETE, req);
		}
		else
		{
			req.output = data;
	    	sendNotification(SEND_MSG_COMPLETE, req);
		}
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
			if(reqAction_.equals("get_user_msg"))
				sendNotification(GET_MSG_LIST_FAILED, req);
			else
				sendNotification(SEND_MSG_FAILED, req);
			break;
		}
	}

}
