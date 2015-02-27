package com.yiyouapp;

import java.util.ArrayList;
import java.util.List;

import org.puremvc.java.interfaces.INotification;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.mobstat.StatService;
import com.yiyouapp.adapters.SingleSelectorAdapter;
import com.yiyouapp.cmd.AccountCmd;
import com.yiyouapp.cmd.RegCmd;
import com.yiyouapp.proxy.RegProxy;
import com.yiyouapp.proxy.RegProxy.Mission;
import com.yiyouapp.utils.AccountUtil;
import com.yiyouapp.utils.Location;
import com.yiyouapp.utils.SDCardUtil;
import com.yiyouapp.utils.StringUtil;
import com.yiyouapp.value.KeyValue;
import com.yiyouapp.value.ReqData;
import com.yiyouapp.value.TypeDef;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class RegStep4Activity extends MediatorActivity implements OnClickListener {

	public static final String NAME = "RegStep4Activity";

	private Button next_btn_;
	
	private String pass_ = "";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_reg_step4);

		findViewById(R.id.back_btn).setOnClickListener((OnClickListener)this);

		next_btn_ = (Button)findViewById(R.id.next_btn);
		next_btn_.setOnClickListener((OnClickListener)this);

		// 重新获取位置，避免用户位置移动
		Location.startGetLocation();
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
			if(proxy.getMission() == Mission.Register)
			{
				Intent intent = new Intent(this, RegSuccessActivity.class);
				
				this.startActivity(intent);

				// 设置登录的手机号和密码
				AppConfig.settings.setMobile(AppConfig.settings.getRegMobile());
				AppConfig.settings.setPass(pass_);

				// 获取用户账户信息
				sendNotification(AccountCmd.GET_ACCOUNT_INFO, ReqData.create(this, ReqData.FailHandleType.Quite));
				
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

			pass_ = ((TextView)findViewById(R.id.reg_pass)).getText().toString();
			String name = ((TextView)findViewById(R.id.reg_nickname)).getText().toString();
			String sex = "M";

			if(!StringUtil.isPass(pass_))
			{
				Toast.makeText(this, "您输入的密码格式不对，要求6到20个字符", Toast.LENGTH_SHORT).show();
				break;
			}

			if(!StringUtil.isName(name))
			{
				Toast.makeText(this, "您输入的用户名格式不对，要求中文、字母或数字", Toast.LENGTH_SHORT).show();
				break;
			}
			
			if(((RadioGroup)findViewById(R.id.sex)).getCheckedRadioButtonId() == R.id.sex_female)
				sex = "F";
			
			KeyValue[] values = new KeyValue[8];

			for(int i = 0; i < values.length; i++)
				values[i] = new KeyValue();
			
			values[0].key = "type";
			values[0].value = AppConfig.settings.getUserType();

			values[1].key = "pass";
			values[1].value = pass_;

			values[2].key = "name";
			values[2].value = name;

			values[3].key = "sex";
			values[3].value = sex;

			values[4].key = "address";
			values[4].value = AppConfig.address;

			values[5].key = "city";
			values[5].value = AppConfig.city;

			values[6].key = "longitude";
			values[6].value = String.valueOf((int)(AppConfig.longitude *  1000000));

			values[7].key = "latitude";
			values[7].value = String.valueOf((int)(AppConfig.latitude *  1000000));
			
			showProgress("正在为您注册...");

			ReqData req = ReqData.create(this, ReqData.FailHandleType.Retry);
			req.input = values;
			sendNotification(RegCmd.REGISTER, req, "4");
			
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
