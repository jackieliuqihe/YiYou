package com.yiyouapp;

import org.puremvc.java.interfaces.INotification;

import com.baidu.mobstat.StatService;
import com.yiyouapp.cmd.RegCmd;
import com.yiyouapp.proxy.RegProxy;
import com.yiyouapp.proxy.RegProxy.Mission;
import com.yiyouapp.utils.StringUtil;
import com.yiyouapp.utils.XTimer;
import com.yiyouapp.utils.XTimer.OnTimerListener;
import com.yiyouapp.value.ReqData;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class RegStep2Activity extends MediatorActivity implements OnClickListener, OnTimerListener {

	public static final String NAME = "RegStep2Activity";

	private Button next_btn_;
	private TextView require_code_tv_;
	
	private XTimer timer_;

	private int count_ = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_reg_step2);

		findViewById(R.id.back_btn).setOnClickListener((OnClickListener)this);

		next_btn_ = (Button)findViewById(R.id.next_btn);
		next_btn_.setOnClickListener((OnClickListener)this);

		require_code_tv_ = (TextView)findViewById(R.id.require_code);
		
		require_code_tv_.setOnClickListener((OnClickListener)this);
		
		timer_ = new XTimer(this, 1000, 1000);
		startCounter();
	}
	
	// �Ƿ��ǵ�¼�����ҳ
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
			if(proxy.getMission() == Mission.VerifyCode)
			{
				Intent intent = new Intent(this, RegStep3Activity.class);
				
				this.startActivity(intent);

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

			TextView tv = (TextView)findViewById(R.id.reg_code);
			String code = tv.getText().toString();

			if(!StringUtil.isVerifyCode(code))
			{
				Toast.makeText(this, "���������֤�벻��ȷ", Toast.LENGTH_SHORT).show();
				break;
			}

			showProgress("����У����֤�룬���Ժ�...");

			ReqData req = ReqData.create(this, ReqData.FailHandleType.Retry);
			req.input = code;
			sendNotification(RegCmd.REGISTER, req, "2");
			
			break;
		case R.id.require_code:
			
			if(count_ > 0)
				break;
			
			new AlertDialog.Builder(this)
			.setMessage("ȷ�����������ֻ���֤����")  
			.setPositiveButton("��" , new DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface dialog,int id) {

					startCounter();
					
					ReqData req = ReqData.create(this, ReqData.FailHandleType.Retry);
					req.input = AppConfig.settings.getRegMobile();
					sendNotification(RegCmd.REGISTER, req, "21");
					
				}
			
			})  
			.setNegativeButton("��" , null)
			.show();
			
			break;
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		timer_.stop();
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

	private void startCounter()
	{
		timer_.start();
		
		count_ = 70;
		require_code_tv_.setText("��ȴ�" + String.valueOf(count_) + "��");
		require_code_tv_.getPaint().setFlags(require_code_tv_.getPaint().getFlags() & (~Paint.UNDERLINE_TEXT_FLAG));
		require_code_tv_.getPaint().setAntiAlias(true);
	}
	
	@Override
	public void onTimer(XTimer timer) {
		// TODO Auto-generated method stub
		if(count_ > 1)
		{
			require_code_tv_.setText("��ȴ�" + String.valueOf(--count_) + "��");
		}
		else
		{
			count_ = 0;
			timer_.stop();
			require_code_tv_.setText("����������֤��");
			require_code_tv_.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
			require_code_tv_.getPaint().setAntiAlias(true);
		}
	}

}
