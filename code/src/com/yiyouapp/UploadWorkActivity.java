package com.yiyouapp;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.UUID;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;
import org.puremvc.java.interfaces.INotification;

import com.baidu.mobstat.StatService;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.yiyouapp.cmd.RegCmd;
import com.yiyouapp.cmd.UploadWorkCmd;
import com.yiyouapp.controls.PhotoObtainer;
import com.yiyouapp.proxy.RegProxy;
import com.yiyouapp.proxy.RegProxy.Mission;
import com.yiyouapp.proxy.UploadWorkProxy;
import com.yiyouapp.utils.DialogUtil;
import com.yiyouapp.utils.Location;
import com.yiyouapp.utils.SizeUtil;
import com.yiyouapp.utils.StringUtil;
import com.yiyouapp.value.ReqData;
import com.yiyouapp.value.TypeDef;

import android.os.Bundle;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class UploadWorkActivity extends MediatorActivity implements OnClickListener {

	public static final String NAME = "UploadWorkActivity";

	private int work_type_ = 0;

	private ProgressBar progress_bar_;
	
	private Button publish_btn_;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_upload_work);

		findViewById(R.id.back_btn).setOnClickListener((OnClickListener) this);

		publish_btn_ = (Button)findViewById(R.id.ok_btn);
		publish_btn_.setOnClickListener((OnClickListener) this);

		progress_bar_ = (ProgressBar)findViewById(R.id.progressbar);
		
		ImageView iv = (ImageView)findViewById(R.id.work_thumb);
		iv.setOnClickListener((OnClickListener) this);
		
		if(getIntent().getBooleanExtra(AppConfig.INTENT_HAS_WORK_PHOTO, false) &&
			AppConfig.work_photo != null)
		{
			iv.setImageBitmap(AppConfig.work_photo);
		}

		findViewById(R.id.worktype_rl).setOnClickListener(this);

		// 重新获取位置，避免用户位置移动
		Location.startGetLocation();
	}

	@Override
	protected String getName() {
		// TODO Auto-generated method stub
		return NAME;
	}

	@Override
	public String[] listNotificationInterests() {
		return new String[]{UploadWorkProxy.UPLOAD_WORK_PROGRESS,
				UploadWorkProxy.UPLOAD_WORK_COMPLETE};
	}

	@Override
	public void handleNotification(INotification notification) {
		String noteName = notification.getName();
		ReqData req = (ReqData)notification.getBody();
		
		if(noteName.equals(UploadWorkProxy.UPLOAD_WORK_PROGRESS))
		{
			//float percent = Float.parseFloat(notification.getBody().toString());
			//int value = (int)((float)progress_bar_.getMax() * percent);
			//progress_bar_.setProgress(value);
		}
		else if(noteName.equals(UploadWorkProxy.UPLOAD_WORK_COMPLETE))
		{
    		// 释放图像内存
    		AppConfig.work_photo = null;
    		
			hideProgress();
			
			Intent intent = new Intent(this, MainActivity.class);
			startActivity(intent);

    		Toast.makeText(this, "作品发布成功", Toast.LENGTH_SHORT).show();
    		
    		// 清除自己页面
    		finish();
		}
		
	}

	@Override
	public void showProgress(String info) {
		publish_btn_.setEnabled(false);
		super.showProgress(info);
	}

	@Override
	public void hideProgress() {
		super.hideProgress();
		publish_btn_.setEnabled(true);
	}

	@Override
	public void onClick(View v)
	{	
		switch(v.getId())
		{
		case R.id.back_btn:
			this.finish();
			break;
		case R.id.ok_btn:
			uploadWork();
			break;
		case R.id.work_thumb:
			DialogUtil.showPhotoSelectDialog(this);
			break;
		case R.id.worktype_rl:
			Intent intent = new Intent(this, WorkTypeActivity.class);

			intent.putExtra(AppConfig.INTENT_WORK_TYPE, (int)work_type_);
			startActivityForResult(intent, 3);
			break;
		}
	}

    @Override  
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
    	if(requestCode != 3)
    	{
	    	Bitmap bmp = PhotoObtainer.processBitmap(requestCode, resultCode, data);
	    	
	    	if(bmp != null)
	    	{
	    		AppConfig.work_photo = bmp;
	    		ImageView iv = (ImageView)findViewById(R.id.work_thumb);
	    		
				iv.setImageBitmap(AppConfig.work_photo);
	    	}
	    	else
	    	{
	    		Toast.makeText(this, "您未拍照或选择作品", Toast.LENGTH_SHORT).show();
	    	}
    	}
    	else
    	{
    		if(data == null)
    			return;
    		
    		work_type_ = data.getIntExtra(AppConfig.INTENT_WORK_TYPE, TypeDef.workType_names.length - 1);
    		TextView tv = (TextView)findViewById(R.id.worktype);
    		tv.setText(TypeDef.workType_names[work_type_]);

    		int resId = getResources().getIdentifier("icon_work_type" + (work_type_ + 1), 
    				"drawable", getPackageName());

    		ImageView iv = (ImageView)findViewById(R.id.worktype_icon);
    		iv.setImageResource(resId);
    	}
    }

	public void uploadWork()
	{
		if(AppConfig.work_photo == null)
		{
    		Toast.makeText(this, "没有选择作品", Toast.LENGTH_SHORT).show();
			return;
		}

		showProgress("正在发布作品...");

		AppConfig.work_photo = SizeUtil.adjustBitmapInSize(AppConfig.work_photo, AppConfig.upload_image_width, AppConfig.upload_image_height);
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
    	AppConfig.work_photo.compress(CompressFormat.JPEG, 90, baos);
    	
		RequestParams params = new RequestParams();

		TextView tv = (TextView)findViewById(R.id.work_desc);
		
		params.put("qqfile", (ByteArrayInputStream)new ByteArrayInputStream(baos.toByteArray()), UUID.randomUUID().toString() + "-image.jpg");
		params.add("desc", tv.getText().toString());
		params.add("type", TypeDef.workType_values[work_type_]);
		
		params.add("address", AppConfig.address);
		params.add("city", AppConfig.city);
		params.add("longitude", String.valueOf((int)(AppConfig.longitude *  1000000)));
		params.add("latitude", String.valueOf((int)(AppConfig.latitude *  1000000)));

		ReqData req = ReqData.create(this, ReqData.FailHandleType.Notify);
		req.input = params;
		sendNotification(UploadWorkCmd.UPLOAD_WORK, req);
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
