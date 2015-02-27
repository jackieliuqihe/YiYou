package com.yiyouapp;

import java.io.Serializable;
import java.util.ArrayList;

import org.puremvc.java.interfaces.INotification;

import com.baidu.mobstat.StatService;
import com.loopj.android.http.RequestParams;
import com.yiyouapp.cmd.GetImageCmd;
import com.yiyouapp.cmd.VisitUserCmd;
import com.yiyouapp.controls.TouchImageView;
import com.yiyouapp.proxy.NetImageProxy;
import com.yiyouapp.proxy.NetImageProxy.OutputData;
import com.yiyouapp.proxy.VisitUserProxy;
import com.yiyouapp.proxy.NetImageProxy.ImageType;
import com.yiyouapp.proxy.NetImageProxy.InputData;
import com.yiyouapp.utils.ImageUtil;
import com.yiyouapp.utils.StringUtil;
import com.yiyouapp.value.ReqData;
import com.yiyouapp.value.UserInfo;
import com.yiyouapp.value.Work;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

public class WorkDetailActivity extends MediatorActivity implements OnClickListener {

	public static final String NAME = "WorkDetailActivity";

	private TouchImageView imageView_;
	private LinearLayout progressbar_;
	
	private Work work_;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_work_detail);
		
		imageView_ = (TouchImageView)findViewById(R.id.work_image);
		progressbar_  = (LinearLayout)findViewById(R.id.progress_ll);
		
		imageView_.setOnClickListener(this);

		Bundle bundle = getIntent().getExtras();
		Serializable ser = bundle.getSerializable("work");
		
		work_ = (Work)ser;
		
		if(work_ != null)
		{
			ArrayList<ReqData> reqList = new ArrayList<ReqData>();
			
			String key = StringUtil.getFileNameFromPath(work_.file_path[0]);
			if(!AppConfig.cache.isExist(key))
			{
				ReqData req = ReqData.create(this, ReqData.FailHandleType.Notify);
				InputData input = new InputData();
				
				input.url = StringUtil.getFileUrl(work_.file_path[0]);
				input.cachable = true;
				input.type = ImageType.BitImage;
				input.key = key;
				
				req.input = input;
				
				reqList.add(req);
				
				sendNotification(GetImageCmd.GET_IMAGE, reqList);
			}
			else
			{
	        	Uri uri = AppConfig.cache.sdcard.getExistFileUri(StringUtil.getFileNameFromPath(work_.file_path[0]));
	        	imageView_.setImageURI(uri);
	        	
				setLoading(false);
			}
		}
	}

	@Override
	protected String getName() {
		// TODO Auto-generated method stub
		return NAME;
	}

	@Override
	public String[] listNotificationInterests() {
		return new String[]{NetImageProxy.GET_IMAGE_COMPLETE};
	}

	@Override
	public void handleNotification(INotification notification) {
		ReqData req = (ReqData)notification.getBody();

		if(req.sender.equals(this))
		{
			String noteName = notification.getName();
			if(noteName.equals(NetImageProxy.GET_IMAGE_COMPLETE))
			{
				InputData input = (InputData)req.input;
				OutputData output = (OutputData)req.output;
				output.bitmap = ImageUtil.decodingBytes(output.data);
				
				if(output.bitmap != null)
				{
	    			// »º´æÆðÀ´
	    			AppConfig.cache.setImage(input.key, output.bitmap, output.data);
	    			output.data = null;
	    			
					float zoom = (float)output.bitmap.getWidth() / (float)AppConfig.screen_width;
					zoom *= 1.5f;
					
					if(zoom < 2.0f)
						zoom = 2.0f;
	
					imageView_.setImageBitmap(output.bitmap);
					imageView_.setMaxZoom(zoom);

					setLoading(false);
				}
			}
		}
	}

	private void setLoading(boolean loading)
	{
		if(loading)
		{
			imageView_.setVisibility(View.GONE);
			progressbar_.setVisibility(View.VISIBLE);
		}
		else
		{
			imageView_.setVisibility(View.VISIBLE);
			progressbar_.setVisibility(View.GONE);
		}
	}
	
	@Override
	public void onClick(View v) {

		switch(v.getId())
		{
		case R.id.work_image:
			
			break;
		}
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		StatService.onPause(this);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		StatService.onResume(this);
	}

}
