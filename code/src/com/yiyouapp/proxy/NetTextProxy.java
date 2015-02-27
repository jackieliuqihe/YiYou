package com.yiyouapp.proxy;

import org.apache.http.Header;
import org.puremvc.java.patterns.proxy.Proxy;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.yiyouapp.AppConfig;
import com.yiyouapp.utils.StringUtil;
import com.yiyouapp.value.ReqData;

public class NetTextProxy extends ReqDataProxy implements IRetry {

	protected enum NetState
	{
		Normal,
		Fail,
		Cancel
	}
	
	class CustomResponseHandler extends TextHttpResponseHandler {
		
		public boolean loading = true;
		public ReqData req;
		
		@Override
		public void onFailure(int statusCode, Header[] headers, String responseBody,
				Throwable arg3) {
		}
		
		@Override
		public void onSuccess(int statusCode, Header[] headers, String responseBody) {
		}
	}
	
	private RequestParams prev_params_;
	private String prev_url_;

	private AsyncHttpClient client_ = null;
	private CustomResponseHandler handler_ = null;
	private RequestHandle req_handle_ = null;
	
	public NetTextProxy(String name) {
		super(name);
	}

	public void post(String url, RequestParams params)
	{
		prev_url_ = url;
		prev_params_ = params;
		
		cancelRequest();
		
		if(client_ == null)
		{
			client_ = new AsyncHttpClient();
	        client_.setCookieStore(AppConfig.cookie);
		}
		
		//if(handler_ == null)
		{
			handler_ = new CustomResponseHandler() {

				@Override
				public void onCancel() {
					// TODO Auto-generated method stub
					loading = false;
					
					super.onCancel();
					onResult(req, NetState.Cancel, "cancel");
				}

				@Override
				public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
					// TODO Auto-generated method stub
					super.onSuccess(statusCode, headers, responseBody);

					loading = false;
					
					if((statusCode / 100) == 2)
					{
						onResult(req, NetState.Normal, StringUtil.removeBOM(new String(responseBody)));
					}
					else
					{
						onResult(req, NetState.Fail, new String(responseBody));
					}
				}

				@Override
				public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
					// TODO Auto-generated method stub
					super.onFailure(statusCode, headers, responseBody, error);
					
					if(loading)
					{
						loading = false;
						onResult(req, NetState.Fail, "fail");
					}
				}

		    };
		}
		
		handler_.loading = true;
		handler_.req = super.getReqData();
		
        req_handle_ = client_.post(url, params, handler_);
	}

	public void cancelRequest()
	{
		if(req_handle_ != null)
		{
			if(handler_.loading)
			{
				req_handle_.cancel(true);
			}
		}
	}
	
	protected void onDataProgress(int bytesWritten, int totalSize)
	{
	}
	
	protected void onResult(ReqData req, NetState state, String data)
	{
	}

	@Override
	public void retry() 
	{
		post(prev_url_, prev_params_);		
	}
}
