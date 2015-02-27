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
import com.yiyouapp.value.UserNews;
import com.yiyouapp.value.ReqData;
import com.yiyouapp.value.UserInfo;
import com.yiyouapp.value.Work;

public class UserNewsProxy extends NeedLoginProxy {

	enum NewsListAction
	{
		NewsList,
		UnviewNewsCount,
		SetNewsViewed
	}
	
	public static String NAME = "UserNewsProxy";

	public static String GET_NEWS_LIST_COMPLETE = "get_news_list_complete";
	public static String GET_NEWS_LIST_FAILED = "get_news_list_failed";

	public static String GET_UNVIEW_NEWS_COUNT_COMPLETE = "get_unview_news_count_complete";
	
	private NewsListAction action_;
	
	public UserNewsProxy() {
		super(NAME);
	}

	/*
	 * 获取用户消息
	 */
	public void getNewsList(ReqData req)
	{
		action_ = NewsListAction.NewsList;
		
		super.setReqData(req);
		super.login(req);
	}

	public void getUnviewNewsCount(ReqData req)
	{
		action_ = NewsListAction.UnviewNewsCount;
		
		super.setReqData(req);
		super.login(req);
	}

	public void setNewsViewed(ReqData req)
	{
		action_ = NewsListAction.SetNewsViewed;
		
		super.setReqData(req);
		super.login(req);
	}

	private void doGetNewsList(ReqData req)
	{
		RequestParams params = new RequestParams();
		params.add("user_news_id", String.valueOf(req.input));
		
		post(StringUtil.getURL("user", "get_user_news"), params);
	}

	private void doGetUnviewNewsCount(ReqData req)
	{
		post(StringUtil.getURL("user", "get_unview_nc"), null);
	}

	private void doSetNewsViewed(ReqData req)
	{
		post(StringUtil.getURL("user", "set_news_view"), (RequestParams)req.input);
	}

	@Override
	protected void onSuccessResult(ReqData req, String reason, String data)
	{
		switch(action_)
		{
		case NewsList:
			ArrayList<UserNews> news = WorkUtil.jsonToNewsList(data);
			req.output = news;
	    	
	    	sendNotification(GET_NEWS_LIST_COMPLETE, req);
			break;
		case UnviewNewsCount:
			int count = 0;
			if(!StringUtil.isStringEmpty(data))
				count = Integer.parseInt(data);

			req.output = count;
	    	sendNotification(GET_UNVIEW_NEWS_COUNT_COMPLETE, req);
			break;
		case SetNewsViewed:
			break;
		}
	}

	protected void onLoginSuccess(ReqData req)
	{
		switch(action_)
		{
		case NewsList:
			doGetNewsList(req);
			break;
		case UnviewNewsCount:
			doGetUnviewNewsCount(req);
			break;
		case SetNewsViewed:
			doSetNewsViewed(req);
			break;
		}
	}

	@Override
	protected void onResult(ReqData req, NetState state, String data)
	{
		super.onResult(req, state, data);
		
		switch(state)
		{
		case Fail:
		case Cancel:
			sendNotification(GET_NEWS_LIST_FAILED, req);
			break;
		}
	}

}
