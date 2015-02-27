package com.yiyouapp.proxy;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;
import org.puremvc.java.patterns.proxy.Proxy;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.BinaryHttpResponseHandler;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.yiyouapp.AppConfig;
import com.yiyouapp.cmd.NetErrorCmd;
import com.yiyouapp.cmd.NetInfoCmd;
import com.yiyouapp.proxy.NetTextProxy.CustomResponseHandler;
import com.yiyouapp.proxy.NetTextProxy.NetState;
import com.yiyouapp.value.ReqData;
import com.yiyouapp.value.Work;

public class NetImageProxy extends ReqDataProxy {

	protected enum NetState
	{
		Normal,
		Fail,
		Cancel
	}

	public enum ImageType
	{
		BitImage,
		Thumbnail,
		Avatar
	}
	
	public static class InputData
	{
		public String url;
		public boolean cachable;
		public String key;
		public Work work;
		// Í·Ïñ»¹ÊÇËõÂÔÍ¼
		public ImageType type;
		public int position;
		
		public boolean loading;
		private RequestHandle handle;
		private BinaryHttpResponseHandler response;
	}
	
	public static class OutputData
	{
		public byte[] data;
		public Bitmap bitmap;
	}
	
	public static String NAME = "NetImageProxy";

	public static String GET_IMAGE_COMPLETE = "get_image_complete";
	public static String GET_IMAGE_FAILED = "get_image_failed";

	private HashMap<String, ReqData> requests_ = new HashMap<String, ReqData>();
	
	private AsyncHttpClient client_ = null;
	
	public NetImageProxy() {
		super(NAME);
	}

	public void batchGet(ArrayList<ReqData> reqList)
	{
		for(int i = 0; i < reqList.size(); i++)
			get(reqList.get(i));
	}
	
	public void get(ReqData req)
	{
		req.receiver = this;
		InputData input = (InputData)req.input;
		
		if(requests_.containsKey(input.url))
		{
			Log.w(AppConfig.APP_NAME, "loading:" + input.url);
			return;
		}

		if(client_ == null)
		{
			client_ = new AsyncHttpClient();
	        client_.setCookieStore(AppConfig.cookie);
		}
		
		BinaryHttpResponseHandler handler = new BinaryHttpResponseHandler() {

			@Override
			public void onCancel() {
				// TODO Auto-generated method stub
				super.onCancel();
				onResult(NetState.Cancel, null);
				
				String url = getRequestURI().toString();
				requests_.remove(url);
			}

			@Override
			public void onProgress(int bytesWritten, int totalSize) {
				// TODO Auto-generated method stub
				super.onProgress(bytesWritten, totalSize);
				onDataProgress(bytesWritten, totalSize);
			}

			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
				//super.onSuccess(statusCode, headers, responseBody);

				String url = getRequestURI().toString();
				if(!requests_.containsKey(url))
				{
					Log.w(AppConfig.APP_NAME, "no key:" + url);
					return;
				}
				
				ReqData req = requests_.get(url);

				InputData input = (InputData)req.input;
				
				if(input.loading)
				{
					input.loading = false;
					
					if((statusCode / 100) == 2)
					{
						OutputData output = new OutputData();;
						output.data = responseBody;
						req.output = output;
						onResult(NetState.Normal, req);
					}
					else
					{
						onResult(NetState.Fail, req);
					}
				}

				requests_.remove(url);
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
				//super.onFailure(statusCode, headers, responseBody, error);

				String url = getRequestURI().toString();
				ReqData req = requests_.get(url);

				InputData input = (InputData)req.input;
				if(input.loading)
				{
					input.loading = false;
					onResult(NetState.Fail, req);
				}
				
				requests_.remove(url);
			}
	    };
		
		input.loading = true;
		input.response = handler;
		input.handle = client_.get(input.url, null, handler);
		
        requests_.put(input.url, req);
	}

	public void cancelRequest(String url)
	{
		if(requests_.containsKey(url))
		{
			InputData input = (InputData)requests_.get(url).input;
			input.loading = false;
			input.handle.cancel(true);
			requests_.remove(url);
		}
	}

	protected void onDataProgress(int bytesWritten, int totalSize)
	{
	}
	
	protected void onResult(NetState state, ReqData req)
	{
		switch(state)
		{
		case Normal:
			
			OutputData output = (OutputData)req.output;
			
			if(output.data.length == 0 || output.data.length > (8 << 20))
			{
				setInfo("Í¼Æ¬³ß´ç³¬¹ý·¶Î§");
				sendNotification(NetInfoCmd.NET_INFO, req);
			}
			else
			{
				sendNotification(GET_IMAGE_COMPLETE, req);
			}

			break;
		case Fail:
			sendNotification(NetErrorCmd.NET_ERROR, req);
		case Cancel:
			sendNotification(GET_IMAGE_FAILED, req);
			break;
		}
	}
}
