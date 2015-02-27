package com.yiyouapp;

import com.baidu.mobstat.StatService;
import com.yiyouapp.adapters.SingleSelectorAdapter;
import com.yiyouapp.adapters.WorkTypeAdapter;
import com.yiyouapp.controls.PhotoObtainer;
import com.yiyouapp.utils.DialogUtil;
import com.yiyouapp.value.TypeDef;

import android.os.Bundle;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
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
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class WorkTypeActivity extends MediatorActivity implements OnClickListener {

	public static final String NAME = "WorkTypeActivity";

	private ListView work_subtype_lv_;
	private WorkTypeAdapter adapter_;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_work_type);

		Button btn = (Button)findViewById(R.id.back_btn);
		btn.setOnClickListener((OnClickListener) this);

		int work_type = getIntent().getIntExtra(AppConfig.INTENT_WORK_TYPE, TypeDef.workType_names.length - 1);
		
		setResult(3, getIntent());
		
		if(work_type < 0 || work_type >= TypeDef.workType_names.length)
		{
			finish();
			return;
		}
		
		// —°‘Ò¡–±Ì
        adapter_ = new WorkTypeAdapter(this, TypeDef.workType_names);
        
        work_subtype_lv_ = (ListView)findViewById(R.id.work_type_list);

        work_subtype_lv_.setAdapter(adapter_);
        work_subtype_lv_.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        
        work_subtype_lv_.setItemChecked(work_type, true);
        adapter_.setSelectItem(work_type);
        adapter_.notifyDataSetInvalidated();
        
        work_subtype_lv_.setOnItemClickListener(new OnItemClickListener() {  
  
            @Override  
            public void onItemClick(AdapterView<?> parent, View arg1, int pos, long id) {

                adapter_.setSelectItem(pos);  
                adapter_.notifyDataSetInvalidated();
                
            	getIntent().putExtra(AppConfig.INTENT_WORK_TYPE, (int)id);
        		setResult(3, getIntent());
            	WorkTypeActivity.this.finish();

            }
  
        });
	}

	@Override
	protected String getName() {
		// TODO Auto-generated method stub
		return NAME;
	}
	
	@Override
	public void onClick(View v)
	{	
		switch(v.getId())
		{
		case R.id.back_btn:
			this.finish();
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
