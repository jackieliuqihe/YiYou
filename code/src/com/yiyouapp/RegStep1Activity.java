package com.yiyouapp;

import org.puremvc.java.interfaces.INotification;

import com.baidu.mobstat.StatService;
import com.yiyouapp.cmd.RegCmd;
import com.yiyouapp.controls.CustomAlertDialog;
import com.yiyouapp.proxy.RegProxy;
import com.yiyouapp.proxy.RegProxy.Mission;
import com.yiyouapp.utils.StringUtil;
import com.yiyouapp.value.ReqData;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class RegStep1Activity extends MediatorActivity implements OnClickListener {

	public static final String NAME = "RegStep1Activity";
	
	private TextView mobile_tv_;
	
	private Button next_btn_;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_reg_step1);

		findViewById(R.id.back_btn).setOnClickListener((OnClickListener) this);

		next_btn_ = (Button)findViewById(R.id.next_btn);
		next_btn_.setOnClickListener((OnClickListener) this);

		mobile_tv_ = (TextView)findViewById(R.id.reg_mobile);
	}

	// 是否是登录后进的页
	@Override
	protected boolean isLoginedPage()
	{
		return false;
	}

	@Override
	protected String getName() {
		// TODO Auto-generated method stub
		return NAME;
	}

	@Override
	public String[] listNotificationInterests() {
		return new String[]{RegProxy.REG_COMPLETE};
	}

	@Override
	public void handleNotification(INotification notification) {
		String noteName = notification.getName();
		RegProxy proxy = (RegProxy)((ReqData)notification.getBody()).receiver;
		
		if(noteName.equals(RegProxy.REG_COMPLETE))
		{
			if(proxy.getMission() == Mission.GetCode && ((String)proxy.getData()).equals("1"))
			{
				Intent intent = new Intent(RegStep1Activity.this, RegStep2Activity.class);
				
				RegStep1Activity.this.startActivity(intent);

				hideProgress();
			}
		}
	}

	@Override
	public void showProgress(String info) {
		next_btn_.setEnabled(false);
		super.showProgress(info);
	}

	@Override
	public void hideProgress() {
		super.hideProgress();
		next_btn_.setEnabled(true);
	}

	@Override
	public void onClick(View v) {
		switch(v.getId())
		{
		case R.id.back_btn:
			this.finish();
			break;
		case R.id.next_btn:

			String mobile = mobile_tv_.getText().toString();
			if(!StringUtil.isMobile(mobile))
			{
				Toast.makeText(RegStep1Activity.this, "您输入的手机号不正确", Toast.LENGTH_SHORT).show();
				break;
			}
			
			/*
			new CustomAlertDialog.Builder(this)
			.setMessage("确定使用这个手机号注册吗？")
			.setPositiveButton("是" , new DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface dialog,int id) {
					RegStep1Activity.this.sendNotification(RegCommand.REGISTER, mobile_tv_.getText().toString(), "1");
				}
			
			})  
			.setNegativeButton("否" , null)
			.show();
			*/

			showProgress("正在验证手机号，请稍后...");
			
			AppConfig.settings.setRegMobile(mobile);
			
			ReqData req = ReqData.create(this, ReqData.FailHandleType.Retry);
			req.input = mobile;
			sendNotification(RegCmd.REGISTER, req, "1");
			
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
