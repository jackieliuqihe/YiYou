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

public class WorkListProxy extends NeedLoginProxy {

	public static class WorksListInput
	{
		public int user_id;
		public int work_id;
		public boolean collect;
	}
	
	public static class WorksList
	{
		// 作品列表
		public ArrayList<Work> works;
	}
	
	public static String NAME = "WorkListProxy";

	public static String GET_WORKS_LIST_COMPLETE = "get_works_list_complete";
	public static String GET_WORKS_LIST_FAILED = "get_works_list_failed";

	public WorkListProxy() {
		super(NAME);
	}

	/*
	 * 上传作品
	 */
	public void getWorksList(ReqData req)
	{
		super.setReqData(req);
		super.login(req);
	}

	private void doGetList(ReqData req)
	{
		RequestParams params = new RequestParams();
		
		WorksListInput input = (WorksListInput)req.input;

		params.add("work_id", String.valueOf(input.work_id));
		params.add("user_id", String.valueOf(input.user_id));
		params.add("collect", String.valueOf(input.collect ? 1 : 0));
		
		post(StringUtil.getURL("works", "get_works_list"), params);
	}

	@Override
	protected void onSuccessResult(ReqData req, String reason, String data)
	{
		WorksList worksList = new WorksList();
		worksList.works = WorkUtil.jsonToWorks(data);

		req.output = worksList;
    	
    	sendNotification(GET_WORKS_LIST_COMPLETE, req);
	}

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
			sendNotification(GET_WORKS_LIST_FAILED, req);
			break;
		}
	}

}
