package com.yiyouapp;

import java.util.ArrayList;

import org.puremvc.java.interfaces.INotification;

import com.baidu.mobstat.StatService;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.loopj.android.http.RequestParams;
import com.yiyouapp.adapters.FriendListAdapter;
import com.yiyouapp.adapters.UserNewsListAdapter;
import com.yiyouapp.adapters.TrainUserListAdapter;
import com.yiyouapp.adapters.StudioListAdapter;
import com.yiyouapp.adapters.UserNewsListAdapter.INewsAction;
import com.yiyouapp.cmd.GetImageCmd;
import com.yiyouapp.cmd.UserNewsCmd;
import com.yiyouapp.cmd.WorkListCmd;
import com.yiyouapp.proxy.LogoutProxy;
import com.yiyouapp.proxy.NetImageProxy;
import com.yiyouapp.proxy.UserNewsProxy;
import com.yiyouapp.proxy.UserListProxy;
import com.yiyouapp.proxy.NetImageProxy.ImageType;
import com.yiyouapp.proxy.NetImageProxy.InputData;
import com.yiyouapp.proxy.NetImageProxy.OutputData;
import com.yiyouapp.proxy.UserListProxy.UserListInput;
import com.yiyouapp.utils.ImageUtil;
import com.yiyouapp.utils.NotificationUtil;
import com.yiyouapp.utils.StringUtil;
import com.yiyouapp.value.UserNews;
import com.yiyouapp.value.ReqData;
import com.yiyouapp.value.UserInfo;
import com.yiyouapp.value.Work;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.format.DateUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class UserNewsListActivity extends MediatorActivity implements OnRefreshListener<ListView>, OnClickListener, INewsAction {

	public static final String NAME = "MsgUsersListActivity";
	
	private PullToRefreshListView pullListView_;
	private UserNewsListAdapter adapter_;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_news_list);

		Button btn = (Button)findViewById(R.id.back_btn);
		btn.setOnClickListener((OnClickListener) this);

		pullListView_ = (PullToRefreshListView)findViewById(R.id.msg_list);

		initLoading((ViewGroup)findViewById(R.id.root_container), pullListView_);
		loading_.setLoading();
		pullListView_.getRefreshableView().setVisibility(View.GONE);
		
        pullListView_.setMode(Mode.PULL_FROM_END);
        pullListView_.setOnRefreshListener(this);
        
        pullListView_.setOnScrollListener(new OnScrollListener(){
    		
    		@Override
    	    public void onScrollStateChanged(AbsListView view, int scrollState) {
    	        switch (scrollState) {
    	            case OnScrollListener.SCROLL_STATE_IDLE:
    	            	loadThumbs();
    	                break;
    	            case OnScrollListener.SCROLL_STATE_FLING:
    	                break;
    	            case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
    	                break;
    	            }
    	    }
    	  
    	    @Override
    	    public void onScroll(AbsListView view, int firstVisibleItem,
    	           int visibleItemCount, int totalItemCount) {
    	    }
    	});
        /*pullListView_.SetOnFinishListener(new OnFinishListener(){

			@Override
			public void onFinish(XListView lv) {
				super.onFinish(lv);
				
				loadThumbs();
			}
        	
        });*/
	}

	@Override
	protected String getName() {
		// TODO Auto-generated method stub
		return NAME;
	}

	@Override
	public String[] listNotificationInterests() {
		return new String[]{UserNewsProxy.GET_NEWS_LIST_COMPLETE,
				UserNewsProxy.GET_NEWS_LIST_FAILED};
	}

	@Override
	public void handleNotification(INotification notification) {
		ReqData req = (ReqData)notification.getBody();

		if(req.sender.equals(this))
		{
			String noteName = notification.getName();
			if(noteName.equals(UserNewsProxy.GET_NEWS_LIST_COMPLETE))
			{
				pullListView_.onRefreshComplete();
				
				ArrayList<UserNews> news = (ArrayList<UserNews>)req.output;

				int count = news.size();
				if(count == 0)
				{
					if(adapter_ == null)
					{
						loading_.setCompleted();
						loading_.setNote("暂无消息");
						pullListView_.getRefreshableView().setVisibility(View.VISIBLE);
					}
					
					return;
				}

				long user_news_id = (Long)req.input;
				if(user_news_id == 0)
				{
					if(adapter_ == null)
					{
						loading_.setCompleted();
						pullListView_.getRefreshableView().setVisibility(View.VISIBLE);
						
				        adapter_ = new UserNewsListAdapter(this, news, this);
				        pullListView_.setAdapter(adapter_);
				        return;
					}

					int adapterCount = adapter_.getCount();
					for(int i = count; i < adapterCount; i++)
						news.add(adapter_.getItem(i));
					
					adapter_.clear();
					adapter_.addAll(news);
				}
				else if(user_news_id > 0)
				{
					for(int i = 0; i < count; i++)
					{
						adapter_.insert(news.get(i), 0);
					}
				}
				else
				{
					for(int i = 0; i < count; i++)
					{
						adapter_.add(news.get(i));
					}
				}
				
				adapter_.notifyDataSetChanged();
				loadThumbs();
			}
			else if(noteName.equals(NetImageProxy.GET_IMAGE_COMPLETE))
			{
				// 设置头像已经更新完
				InputData input = (InputData)req.input;;
				//AppConfig.avatars_update.setUpdate(input.key);
				OutputData output = (OutputData)req.output;

				output.bitmap = ImageUtil.decodingBytes(output.data);
				
				if(output.bitmap != null)
				{
	    			// 缓存起来
	    			AppConfig.cache.setImage(input.key, output.bitmap, output.data);
				
					// 释放不使用的空间
					output.data = null;
					
					ListView lv = pullListView_.getRefreshableView();
					int first = lv.getFirstVisiblePosition();
	            	View view = lv.getChildAt(input.position - first);
	            	if(view == null)
	            		return;
	            	
	            	ImageView iv = (ImageView)view.findViewById(R.id.user_avatar);
	
	            	if(iv == null)
	            		return;
	            	
	            	iv.setImageBitmap(output.bitmap);
				}
            	
			}
			else if(noteName.equals(UserNewsProxy.GET_NEWS_LIST_FAILED))
			{
				pullListView_.onRefreshComplete();
				loading_.setFailed();
				pullListView_.getRefreshableView().setVisibility(View.GONE);
			}
		}
	}

	@Override
	public void onRefresh(PullToRefreshBase<ListView> refreshView) {
		
		if(adapter_ == null)
		{
			pullListView_.onRefreshComplete();
			return;
		}
		
		ListView listView = pullListView_.getRefreshableView();
		
		String label = DateUtils.formatDateTime(listView.getContext().getApplicationContext(), System.currentTimeMillis(),
				DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

		// Update the LastUpdatedLabel 
		refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);

		if(pullListView_.getCurrentMode() == Mode.PULL_FROM_START)
		{
			if(adapter_.getCount() > 0)
				getMsgList(adapter_.getItem(0).user_news_id);
			else
				getMsgList(0);
		}
		else if(pullListView_.getCurrentMode() == Mode.PULL_FROM_END)
		{
			if(adapter_.getCount() > 0)
				getMsgList(-adapter_.getItem(adapter_.getCount() - 1).user_news_id);
			else
				getMsgList(0);
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		StatService.onResume(this);
		
		getMsgList(0);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		StatService.onPause(this);
		
		/*
		if(adapter_ != null)
		{
			int count = 0;
			for(int i = 0; i < adapter_.getCount(); i++)
			{
				if(!adapter_.getItem(i).is_view)
					count++;
			}
			
			AppConfig.newsCount = count;
		}*/
		
		AppConfig.newsCount = 0;
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		
		super.onDestroy();
	}

	// 获取最新的作品列表
	public void getMsgList(long user_news_id)
	{
		ReqData rd = ReqData.create(this, ReqData.FailHandleType.Quite);
		
		rd.input = user_news_id;
		
		sendNotification(UserNewsCmd.GET_NEWS_LIST, rd);
	}
	
	public void loadThumbs()
	{
		ListView lv = pullListView_.getRefreshableView();
		int start = lv.getFirstVisiblePosition();
		int end = lv.getLastVisiblePosition();

		if(start <= end)
		{
			ArrayList<ReqData> reqList = new ArrayList<ReqData>();
			
			for(int i = start; i <= end; i++)
			{
				UserNews msg = (UserNews) lv.getItemAtPosition(i);
				if(msg == null)
					continue;
				
				if(!StringUtil.isStringEmpty(msg.avatar64_path))
				{
					String key = StringUtil.getFileNameFromPath(msg.avatar64_path);
					if(!AppConfig.cache.isExist(key) || 
							AppConfig.avatars_update.getUpdate(key))
					{
						ReqData req = ReqData.create(this, ReqData.FailHandleType.Quite);
						InputData input = new InputData();
						
						input.url = StringUtil.getFileUrl(msg.avatar64_path);
						input.cachable = true;
						input.type = ImageType.Avatar;
						input.position = i;
						input.key = key;
						
						req.input = input;
						
						reqList.add(req);
					}
				}
			}
	
			if(reqList.size() > 0)
				sendNotification(GetImageCmd.GET_IMAGE, reqList);
		}
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

	@Override
	public void onAvatarClick(UserNews news) {
		// TODO Auto-generated method stub

		NotificationUtil.setNewsViewed(this, news);
		
		news.is_view = true;
		adapter_.notifyDataSetChanged();
		
		Intent intent = new Intent(this, UserCenterOtherActivity.class);
		
		intent.putExtra(AppConfig.INTENT_USER_ID, news.user_id);
		intent.putExtra(AppConfig.INTENT_USER_NAME, news.user_name);

		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		
		startActivity(intent);

	}

	@Override
	public void onUserInfoClick(UserNews news) {

		NotificationUtil.setNewsViewed(this, news);
		
		news.is_view = true;
		adapter_.notifyDataSetChanged();
		
		NotificationUtil.newsNavigate(this, news);
	}
	
}
