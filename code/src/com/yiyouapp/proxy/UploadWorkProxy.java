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
import com.yiyouapp.value.UserInfo;
import com.yiyouapp.value.UserInfo.UserLatestWorkThumb;

public class UploadWorkProxy extends NeedLoginProxy {

	public static String NAME = "UploadWorkProxy";

	public static String UPLOAD_WORK_PROGRESS = "upload_work_progress";
	public static String UPLOAD_WORK_COMPLETE = "upload_work_complete";

	private RequestParams params_;
	
	public UploadWorkProxy() {
		super(NAME);
	}

	/*
	 * 上传作品
	 */
	public void uploadWork(ReqData req)
	{
		super.setReqData(req);
		
		params_ = (RequestParams)req.input;
		super.login(req);
	}

	private void doUpload(ReqData req)
	{
		post(StringUtil.getURL("upload", "upload_work"), params_);
	}

	@Override
	protected void onSuccessResult(ReqData req, String reason, String data)
	{
    	req.output = data;
		sendNotification(UPLOAD_WORK_COMPLETE, req);
	}

	@Override
	protected void onLoginSuccess(ReqData req)
	{
		doUpload(req);
	}
}
