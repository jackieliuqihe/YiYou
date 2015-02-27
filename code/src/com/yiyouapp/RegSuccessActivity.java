package com.yiyouapp;

import com.baidu.mobstat.StatService;
import com.yiyouapp.cmd.AccountCmd;
import com.yiyouapp.cmd.VisitUserCmd;
import com.yiyouapp.controls.PhotoObtainer;
import com.yiyouapp.utils.DialogUtil;
import com.yiyouapp.value.ReqData;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class RegSuccessActivity extends MediatorActivity implements OnClickListener {

	public static final String NAME = "RegSuccessActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_reg_success);

		Button btn = (Button)findViewById(R.id.upload_work_btn);
		btn.setOnClickListener((OnClickListener) this);

		findViewById(R.id.enter_main).setOnClickListener(this);
		findViewById(R.id.enter_space).setOnClickListener(this);

		((TextView)findViewById(R.id.mobile)).setText(AppConfig.settings.getMobile());
		((TextView)findViewById(R.id.password)).setText(AppConfig.settings.getPass());
		
	}

	@Override
	protected String getName() {
		// TODO Auto-generated method stub
		return NAME;
	}

	@Override
	public void onClick(View v) {
		
		Intent intent;
		
		switch(v.getId())
		{
		case R.id.upload_work_btn:
			DialogUtil.showPhotoSelectDialog(this);
			break;
		case R.id.enter_main:
			intent = new Intent(this, MainActivity.class);
			startActivity(intent);
			break;
		case R.id.enter_space:
			intent = new Intent(this, UserCenterSelfActivity.class);
			startActivity(intent);
			break;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		
		if (keyCode == KeyEvent.KEYCODE_BACK) 
		{
	        return true;
	    }
		
		return super.onKeyDown(keyCode, event);
	}

    @Override  
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
    	AppConfig.work_photo = PhotoObtainer.processBitmap(requestCode, resultCode, data);
    	
    	if(AppConfig.work_photo != null)
    	{
    		Intent intent = new Intent(this, UploadWorkActivity.class);
			
    		intent.putExtra(AppConfig.INTENT_HAS_WORK_PHOTO, true);
    		
			startActivity(intent);
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
