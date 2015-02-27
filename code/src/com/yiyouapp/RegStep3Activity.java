package com.yiyouapp;

import org.puremvc.java.interfaces.INotification;

import com.baidu.mobstat.StatService;
import com.yiyouapp.adapters.SingleSelectorAdapter;
import com.yiyouapp.value.TypeDef;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class RegStep3Activity extends MediatorActivity implements OnClickListener {

	public static final String NAME = "RegStep3Activity";
	
	private ListView user_type_lv_;
	private SingleSelectorAdapter adapter_;

	private Button next_btn_;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_reg_step3);

		findViewById(R.id.back_btn).setOnClickListener((OnClickListener)this);

		next_btn_ = (Button)findViewById(R.id.next_btn);
		next_btn_.setOnClickListener((OnClickListener)this);

		// 选择列表
        adapter_ = new SingleSelectorAdapter(this, TypeDef.userType_names);
        // new ArrayAdapter<String>(this, R.layout.single_select_item, TypeDef.usertype_names);//
        
        user_type_lv_ = (ListView)findViewById(R.id.user_type_list);
        
        user_type_lv_.setAdapter(adapter_);
        user_type_lv_.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        user_type_lv_.setOnItemClickListener(new OnItemClickListener() {  
  
            @Override  
            public void onItemClick(AdapterView<?> parent, View arg1, int pos, long id) {
                adapter_.setSelectItem(pos);  
                adapter_.notifyDataSetInvalidated();  
            }  
  
        });
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
		return new String[]{};
	}

	@Override
	public void handleNotification(INotification notification) {
		String noteName = notification.getName();
		
	}
	
	@Override
	public void onClick(View v) {
		switch(v.getId())
		{
		case R.id.back_btn:
			this.finish();
			break;
		case R.id.next_btn:

			int checked = user_type_lv_.getCheckedItemPosition();
			
			if(checked < 0)
			{
				new AlertDialog.Builder(this)
				.setMessage("您未选择用户角色？")  
				.setPositiveButton("确定" , null)
				.show();
				
				break;
			}
			
			Intent intent = new Intent(this, RegStep4Activity.class);
		
			AppConfig.settings.setUserType(TypeDef.userType_values[checked]);
			startActivity(intent);

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
