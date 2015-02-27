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
import com.yiyouapp.value.Work;

public class WorkProxy extends NeedLoginProxy {

	public static String NAME = "WorkProxy";

	public static String GET_WORK_COMPLETE = "get_work_complete";
	public static String GET_WORK_FAILED = "get_work_failed";

	public WorkProxy() {
		super(NAME);
	}

	/*
	 * 获取作品
	 */
	public void getWork(ReqData req)
	{
		super.setReqData(req);
		super.login(req);
	}

	private void doGetWork(ReqData req)
	{
		RequestParams params = new RequestParams();
		
		params.add("work_id", String.valueOf(req.input));
		
		post(StringUtil.getURL("works", "get_work"), params);
	}

	@Override
	protected void onSuccessResult(ReqData req, String reason, String data)
	{
		ArrayList<Work> works = WorkUtil.jsonToWorks(data);
		
		if(works.size() > 0)
		{
			req.output = works.get(0);
	    	sendNotification(GET_WORK_COMPLETE, req);
		}
		else
		{
			sendNotification(GET_WORK_FAILED, req);
		}
	}

	protected void onLoginSuccess(ReqData req)
	{
		doGetWork(req);
	}

	@Override
	protected void onResult(ReqData req, NetState state, String data)
	{
		super.onResult(req, state, data);
		
		switch(state)
		{
		case Fail:
		case Cancel:
			sendNotification(GET_WORK_FAILED, req);
			break;
		}
	}

}
