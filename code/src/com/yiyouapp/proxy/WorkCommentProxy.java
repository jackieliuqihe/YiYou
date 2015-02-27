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

public class WorkCommentProxy extends NeedLoginProxy {

	public static class WorkCommentInput
	{
		public long sending_time;
		
		public int work_id;
		public int user_id;
		public int rid;
		
		public int work_comment_id;
		public String comment;
	}
	
	public static String NAME = "WorkCommentProxy";

	public static String SEND_COMMENT_COMPLETE = "send_comment_complete";
	public static String SEND_COMMENT_FAILED = "send_comment_failed";
	
	public static String GET_COMMENT_LIST_COMPLETE = "get_comment_list_complete";
	public static String GET_COMMENT_LIST_FAILED = "get_comment_list_failed";

	private String reqAction_;
	
	private RequestParams params_;
	
	public WorkCommentProxy() {
		super(NAME);
	}

	/*
	 * 获取作品评论
	 */
	public void getCommentList(ReqData req)
	{
		params_ = new RequestParams();
		
		WorkCommentInput input = (WorkCommentInput)req.input;

		params_.add("id", String.valueOf(input.work_comment_id));
		params_.add("work_id", String.valueOf(input.work_id));
		
		reqAction_ = "get_comment";
		super.setReqData(req);
		super.login(req);
	}

	public void sendComment(ReqData req)
	{
		params_ = new RequestParams();
		
		WorkCommentInput input = (WorkCommentInput)req.input;

		params_.add("work_id", String.valueOf(input.work_id));
		params_.add("id", String.valueOf(input.work_comment_id));
		params_.add("user_id", String.valueOf(input.user_id));
		params_.add("rid", String.valueOf(input.rid));
		params_.add("cmt", String.valueOf(input.comment));
		
		reqAction_ = "add_comment";
		super.setReqData(req);
		super.login(req);
	}

	private void doGetList(ReqData req)
	{
		post(StringUtil.getURL("works", reqAction_), params_);
	}

	@Override
	protected void onSuccessResult(ReqData req, String reason, String data)
	{
		if(reqAction_.equals("get_comment"))
		{
			req.output = data;
	    	
	    	sendNotification(GET_COMMENT_LIST_COMPLETE, req);
		}
		else
		{
			req.output = data;
	    	sendNotification(SEND_COMMENT_COMPLETE, req);
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
			if(reqAction_.equals("get_comment"))
			{
				sendNotification(GET_COMMENT_LIST_FAILED, req);
			}
			else
			{
				sendNotification(SEND_COMMENT_FAILED, req);
			}
			break;
		}
	}

}
