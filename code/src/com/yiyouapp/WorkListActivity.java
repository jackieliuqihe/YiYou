package com.yiyouapp;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

import org.puremvc.java.interfaces.INotification;
import org.puremvc.java.patterns.mediator.Mediator;

import com.baidu.mobstat.StatService;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.yiyouapp.AppConfig;
import com.yiyouapp.adapters.TrainUserListAdapter;
import com.yiyouapp.adapters.StudioListAdapter;
import com.yiyouapp.adapters.WorkListAdapter;
import com.yiyouapp.cmd.AccountCmd;
import com.yiyouapp.cmd.UserNewsCmd;
import com.yiyouapp.cmd.VisitUserCmd;
import com.yiyouapp.cmd.WorkListCmd;
import com.yiyouapp.controls.NavItemMover;
import com.yiyouapp.controls.PhotoObtainer;
import com.yiyouapp.proxy.LoginProxy;
import com.yiyouapp.proxy.UserNewsProxy;
import com.yiyouapp.proxy.WorkListProxy;
import com.yiyouapp.proxy.WorkListProxy.WorksList;
import com.yiyouapp.utils.AccountUtil;
import com.yiyouapp.utils.DialogUtil;
import com.yiyouapp.utils.ImageUtil;
import com.yiyouapp.utils.SDCardUtil;
import com.yiyouapp.utils.SizeUtil;
import com.yiyouapp.utils.StringUtil;
import com.yiyouapp.utils.WorkUtil;
import com.yiyouapp.value.ReqData;
import com.yiyouapp.value.TypeDef;
import com.yiyouapp.value.Work;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AbsListView.OnScrollListener;

public class WorkListActivity extends MediatorActivity implements OnClickListener {

	public static final String NAME = "WorkListActivity";
	
	private WorkListMediator workListMediator_;
	
	private PullToRefreshListView pullListView_;
	
	private int user_id_;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_work_list);

		pullListView_ = (PullToRefreshListView)findViewById(R.id.work_list);

    	initLoading((ViewGroup)findViewById(R.id.root_container), pullListView_);
    	loading_.setLoading();
    	
		// 按钮事件
		findViewById(R.id.back_btn).setOnClickListener(this);

		String username = getIntent().getStringExtra(AppConfig.INTENT_USER_NAME);
		TextView tv = (TextView)findViewById(R.id.top_title_bar_title);
		if(!StringUtil.isStringEmpty(username))
			tv.setText(username);
		
		user_id_ = getIntent().getIntExtra(AppConfig.INTENT_USER_ID, 0);
		
        workListMediator_ = new WorkListMediator("works" + user_id_, pullListView_, 
        		user_id_, getIntent().getBooleanExtra(AppConfig.INTENT_COLLECT, false));
        facade.registerMediator(workListMediator_);
	}

	@Override
	protected String getName() {
		return NAME;
	}

	@Override
	public String[] listNotificationInterests() {
		return new String[]{WorkListMediator.WORK_LIST_INIT_COMPLETE,
				WorkListMediator.WORK_LIST_INIT_FAILED};
	}

	@Override
	public void handleNotification(INotification notification) {
		String noteName = notification.getName();
		if(noteName.equals(WorkListMediator.WORK_LIST_INIT_COMPLETE))
		{
			ReqData req = (ReqData)notification.getBody();

			Mediator med = (Mediator)req.sender;
			if(med.getMediatorName().equals("works" + user_id_))
			{
				loading_.setCompleted();
				pullListView_.getRefreshableView().setVisibility(View.VISIBLE);
			}
			
			if(pullListView_.getRefreshableView().getCount() == 0)
				loading_.setNote("暂无作品");
		}
		else if(noteName.equals(WorkListMediator.WORK_LIST_INIT_FAILED))
		{
			if(pullListView_.getRefreshableView().getCount() == 0)
			{
				loading_.setFailed();
				pullListView_.getRefreshableView().setVisibility(View.GONE);
			}
		}
	}

    @Override
	protected void onDestroy() {
    	facade.removeMediator(workListMediator_.getMediatorName());
		super.onDestroy();
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

		// 获取最新的作品列表
		workListMediator_.getWorksList(0);
	}

	@Override
	public void onClick(View v) {
		
		Intent intent;
		
		switch(v.getId())
		{
		case R.id.back_btn:
			finish();
			break;
		}
	}

}
