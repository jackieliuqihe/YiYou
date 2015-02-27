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
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.yiyouapp.AppConfig;
import com.yiyouapp.cmd.NetErrorCmd;
import com.yiyouapp.cmd.NetInfoCmd;
import com.yiyouapp.proxy.NetTextProxy.NetState;
import com.yiyouapp.utils.Location;
import com.yiyouapp.utils.SDCardUtil;
import com.yiyouapp.utils.StringUtil;
import com.yiyouapp.utils.WorkUtil;
import com.yiyouapp.value.KeyValue;
import com.yiyouapp.value.ReqData;
import com.yiyouapp.value.UserInfo;
import com.yiyouapp.value.Work;

public class VisitUserProxy extends NeedLoginProxy {

	public static String NAME = "VisitUserProxy";

	public static String GET_USER_INFO_COMPLETE = "get_user_info_complete";
	public static String GET_USER_INFO_FAILED = "get_user_info_failed";

	public static String SET_ATTENTION_COMPLETE = "set_attention_complete";
	public static String SET_ATTENTION_FAILED = "set_attention_failed";

	public static String GET_USER_SCORE_COMPLETE = "get_user_score_complete";
	
	private String reqPage_ = "";
	private String reqAction_ = "";
	
	public VisitUserProxy() {
		super(NAME);
	}

	/*
	 *  查看用户中心
	 */
	public void visitUserCenter(ReqData req)
	{
		reqPage_ = "user";
		reqAction_ = "visit_user_center";
		
		super.setReqData(req);
		super.login(req);
	}

	/*
	 *  获取用户积分
	 */
	public void getUserScore(ReqData req)
	{
		reqPage_ = "user";
		reqAction_ = "get_score";
		
		super.setReqData(req);
		super.login(req);
	}

	public void setAttention(ReqData req)
	{
		reqPage_ = "user";
		reqAction_ = "attention";
		
		super.setReqData(req);
		super.login(req);
	}

	private void doVisitUser(ReqData req)
	{
		post(StringUtil.getURL(reqPage_, reqAction_), (RequestParams)req.input);
	}

	@Override
	protected void onSuccessResult(ReqData req, String reason, String data)
	{
		if(reqAction_.equals("visit_user_center"))
		{
	        req.output = WorkUtil.jsonToUserInfo(data);
	        
			sendNotification(GET_USER_INFO_COMPLETE, req);
		}
		else if(reqAction_.equals("get_score"))
		{
    		req.output = data;
	        
			sendNotification(GET_USER_SCORE_COMPLETE, req);
		}
		else if(reqAction_.equals("attention"))
		{
			sendNotification(SET_ATTENTION_COMPLETE, req);
		}
	}

	@Override
	protected void onLoginSuccess(ReqData req)
	{
		doVisitUser(req);
	}

	@Override
	protected void onResult(ReqData req, NetState state, String data)
	{
		super.onResult(req, state, data);
		
		switch(state)
		{
		case Fail:
		case Cancel:
			if(reqAction_.equals("visit_user_center"))
				sendNotification(GET_USER_INFO_FAILED, req);
			else if(reqAction_.equals("attention"))
				sendNotification(SET_ATTENTION_FAILED, req);
			break;
		}
	}

}
