package com.yiyouapp;

import java.util.ArrayList;

import org.puremvc.java.interfaces.INotification;
import org.puremvc.java.patterns.mediator.Mediator;

import com.baidu.mobstat.StatService;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.yiyouapp.adapters.FriendListAdapter;
import com.yiyouapp.adapters.TrainUserListAdapter;
import com.yiyouapp.adapters.StudioListAdapter;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class UserListActivity extends MediatorActivity implements OnClickListener {

	public static final String NAME = "UserListActivity";
	
	public static final String[] channel2_items = {"ÐÂ»­ÓÑ", "¸½½ü»­ÓÑ", "ÕÒÀÏÊ¦", "ÕÒ»­ÊÒ", "ÕÒÑ§Éú"};
	
	private int itemType_ = 0;

	private PullToRefreshListView pullListView_;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_list);

		Button btn = (Button)findViewById(R.id.back_btn);
		btn.setOnClickListener((OnClickListener) this);

		TextView tv = (TextView)findViewById(R.id.top_title_bar_title);
    	pullListView_ = (PullToRefreshListView)findViewById(R.id.user_list);

    	initLoading((ViewGroup)findViewById(R.id.root_container), pullListView_);
    	loading_.setLoading();
    	
		itemType_ = getIntent().getIntExtra(AppConfig.INTENT_CHANNEL2_TYPE, 0);
		if(itemType_ >= 0 && itemType_ < channel2_items.length)
		{
			tv.setText(channel2_items[itemType_]);
			
			UserListMediator med = new UserListMediator(channel2_items[itemType_], pullListView_);
			
			med.setItemType(itemType_);
			
	        facade.registerMediator(med);
		}
	}

	@Override
	protected String getName() {
		return NAME;
	}

	@Override
	public String[] listNotificationInterests() {
		return new String[]{UserListMediator.USER_LIST_INIT_COMPLETE,
				UserListMediator.USER_LIST_INIT_FAILED};
	}

	@Override
	public void handleNotification(INotification notification) {
		String noteName = notification.getName();
		
		if(noteName.equals(UserListMediator.USER_LIST_INIT_COMPLETE))
		{
			loading_.setCompleted();
			pullListView_.getRefreshableView().setVisibility(View.VISIBLE);
		}
		else if(noteName.equals(UserListMediator.USER_LIST_INIT_FAILED))
		{
			loading_.setFailed();
			pullListView_.getRefreshableView().setVisibility(View.GONE);
		}
	}

    @Override
	protected void onDestroy() {
    	facade.removeMediator(channel2_items[itemType_]);
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
		
		UserListMediator med = ((UserListMediator)facade.retrieveMediator(channel2_items[itemType_]));
		if(med != null)
			med.getUserList(0);
	}

	@Override
	public void onClick(View v) {
		switch(v.getId())
		{
		case R.id.back_btn:
			this.finish();
			break;
		}
	}

}
