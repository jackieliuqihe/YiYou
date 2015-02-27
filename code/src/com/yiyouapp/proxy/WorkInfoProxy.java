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

public class WorkInfoProxy extends NeedLoginProxy {

	public static String NAME = "WorkInfoProxy";

	private RequestParams params_;
	
	public WorkInfoProxy() {
		super(NAME);
	}

	/*
	 * 上传作品
	 */
	public void changeInfo(ReqData req)
	{
		super.setReqData(req);
		
		params_ = (RequestParams)req.input;
		super.login(req);
	}

	private void doSetProp(ReqData req)
	{
		post(StringUtil.getURL("works", "change_info"), params_);
	}

	protected void onLoginSuccess(ReqData req)
	{
		doSetProp(req);
	}
}
