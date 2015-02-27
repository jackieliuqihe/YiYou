package com.yiyouapp;

import java.util.ArrayList;
import java.util.LinkedList;

import org.puremvc.java.interfaces.INotification;
import org.puremvc.java.patterns.mediator.Mediator;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.loopj.android.http.RequestParams;
import com.yiyouapp.R;
import com.yiyouapp.adapters.FriendListAdapter;
import com.yiyouapp.adapters.WorkListAdapter;
import com.yiyouapp.adapters.WorkListAdapter.IWorkAction;
import com.yiyouapp.cmd.GetImageCmd;
import com.yiyouapp.cmd.NetErrorCmd;
import com.yiyouapp.cmd.UserListCmd;
import com.yiyouapp.cmd.WorkInfoCmd;
import com.yiyouapp.cmd.WorkListCmd;
import com.yiyouapp.proxy.NetImageProxy;
import com.yiyouapp.proxy.NetImageProxy.ImageType;
import com.yiyouapp.proxy.NetImageProxy.InputData;
import com.yiyouapp.proxy.NetImageProxy.OutputData;
import com.yiyouapp.proxy.UserListProxy;
import com.yiyouapp.proxy.WorkListProxy;
import com.yiyouapp.proxy.UserListProxy.UserListInput;
import com.yiyouapp.proxy.WorkListProxy.WorksList;
import com.yiyouapp.utils.ImageUtil;
import com.yiyouapp.utils.SDCardUtil;
import com.yiyouapp.utils.SizeUtil;
import com.yiyouapp.utils.StringUtil;
import com.yiyouapp.utils.WorkUtil;
import com.yiyouapp.value.ReqData;
import com.yiyouapp.value.UserInfo;
import com.yiyouapp.value.Work;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;

public class UserListMediator extends Mediator implements OnRefreshListener<ListView> {

	public static final String USER_LIST_INIT_COMPLETE = "user_list_init_complete";
	public static final String USER_LIST_INIT_FAILED = "user_list_init_failed";

	// 图片解码完成的消息
	private final static int MSG_LOADING_COMPLETE = 0;
	// 列表加载图片的消息
	private final static int MSG_LOAD_THUMBS = 1;

    private int itemType_ = 0;
    
	private ArrayAdapter<UserInfo> adapter_;
	
	private Handler handler_;
	
	public UserListMediator(String mediatorName, Object viewComponent) {
		super(mediatorName, viewComponent);

		if(handler_ == null)
			handler_ = getMessageHandler();
		
        PullToRefreshListView pullListView = getPullListView();
        ListView listView = pullListView.getRefreshableView();

        pullListView.setMode(Mode.BOTH);
        pullListView.setOnRefreshListener(this);
        listView.setOnScrollListener(new OnScrollListener(){
    		
    		@Override
    	    public void onScrollStateChanged(AbsListView view, int scrollState) {
    	        switch (scrollState) {
    	            case OnScrollListener.SCROLL_STATE_IDLE:
    	            	handler_.sendEmptyMessage(MSG_LOAD_THUMBS);
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
        /*
        listView.SetOnFinishListener(new OnFinishListener(){

			@Override
			public void onFinish(XListView lv) {
				super.onFinish(lv);
				
				loadThumbs();
			}
        	
        });*/
        
        listView.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
				if(position > 0 && position <= adapter_.getCount())
					navUserCenter(adapter_.getItem(position - 1));
			}
        	
        });
	}

	public void setItemType(int itemType)
	{
		itemType_ = itemType;
	}
	
	@Override
	public void onRegister() {
		// TODO Auto-generated method stub
		super.onRegister();
	}

	@Override
	public void onRemove() {
		super.onRemove();
	}

	@Override
	public String[] listNotificationInterests() {
		return new String[]{UserListProxy.GET_USER_LIST_COMPLETE,
				UserListProxy.GET_USER_LIST_FAILED,
				NetImageProxy.GET_IMAGE_COMPLETE};
	}

	@Override
	public void handleNotification(INotification notification) {
		ReqData req = (ReqData)notification.getBody();

		if(req.sender.equals(this))
		{
			String noteName = notification.getName();
			if(noteName.equals(UserListProxy.GET_USER_LIST_COMPLETE))
			{
				PullToRefreshListView pullListView = ((PullToRefreshListView)getViewComponent());
				
				ListView lv = pullListView.getRefreshableView();
				pullListView.onRefreshComplete();
				
				ArrayList<UserInfo> users = (ArrayList<UserInfo>)req.output;

				int count = users.size();
				if(count == 0)
				{
			        sendNotification(USER_LIST_INIT_COMPLETE, this);
					return;
				}

				UserListInput in = (UserListInput)req.input; 
				
				// 附近画友的计算方式不同
				if(itemType_ != 1)
				{
					if(in.user_id == 0)
					{
				        adapter_ = new FriendListAdapter(getPullListView().getContext(), 
				        		R.layout.item_friend, users);
				        lv.setAdapter(adapter_);
				        handler_.sendEmptyMessage(MSG_LOAD_THUMBS);
				        
				        sendNotification(USER_LIST_INIT_COMPLETE, this);
				        return;
					}
					else if(in.user_id > 0)
					{
						for(int i = 0; i < count; i++)
						{
							adapter_.insert(users.get(i), 0);
						}
					}
					else
					{
						for(int i = 0; i < count; i++)
						{
							adapter_.add(users.get(i));
						}
					}
				}
				else
				{
					if(in.user_id == 0)
					{
				        adapter_ = new FriendListAdapter(getPullListView().getContext(), 
				        		R.layout.item_friend, users);
				        lv.setAdapter(adapter_);
				        handler_.sendEmptyMessage(MSG_LOAD_THUMBS);
				        
				        sendNotification(USER_LIST_INIT_COMPLETE, this);
				        return;
					}
					else if(in.user_id > 0)
					{
						for(int i = 0; i < count; i++)
						{
							adapter_.add(users.get(i));
						}
					}
				}
				
				adapter_.notifyDataSetChanged();
				handler_.sendEmptyMessage(MSG_LOAD_THUMBS);
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
					
					ListView lv = getPullListView().getRefreshableView();
					
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
			else if(noteName.equals(UserListProxy.GET_USER_LIST_FAILED))
			{
				getPullListView().onRefreshComplete();
				sendNotification(USER_LIST_INIT_FAILED, this);
			}
		}
	}

	@Override
	public void onRefresh(PullToRefreshBase<ListView> refreshView) {
		
		PullToRefreshListView pullListView = getPullListView();
		ListView listView = pullListView.getRefreshableView();
		
		String label = DateUtils.formatDateTime(listView.getContext().getApplicationContext(), System.currentTimeMillis(),
				DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

		// Update the LastUpdatedLabel 
		refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);

		if(pullListView.getCurrentMode() == Mode.PULL_FROM_START)
		{
			// 屏蔽附近画友的下拉刷新
			if(itemType_ == 1)
			{
				handler_.sendEmptyMessage(MSG_LOADING_COMPLETE);
				return;
			}
			
			if(adapter_ != null && adapter_.getCount() > 0)
				getUserList(adapter_.getItem(0).user_id);
			else
				getUserList(0);
		}
		else if(pullListView.getCurrentMode() == Mode.PULL_FROM_END)
		{
			if(adapter_ != null && adapter_.getCount() > 0)
				getUserList(-adapter_.getItem(adapter_.getCount() - 1).user_id);
			else
				getUserList(0);
		}
	}

	// 点击作品的用户头像
	public void navUserCenter(UserInfo user)
	{
		Context context = getPullListView().getContext();
		Intent intent;
		
		if(user.user_id == AppConfig.account.info.user_id)
		{
			intent = new Intent(context, UserCenterSelfActivity.class);
		}
		else
		{
			intent = new Intent(context, UserCenterOtherActivity.class);
			
			intent.putExtra(AppConfig.INTENT_USER_ID, user.user_id);
			intent.putExtra(AppConfig.INTENT_USER_NAME, user.user_name);
		}
		
		context.startActivity(intent);
	}

    private Handler getMessageHandler()
    {
    	return new Handler()
        {
            public void handleMessage(Message msg) 
            {
            	super.handleMessage(msg);
            	
                switch(msg.what)
                {
                case MSG_LOADING_COMPLETE:
                	getPullListView().onRefreshComplete();
                	break;
                case MSG_LOAD_THUMBS:
                	loadThumbs();
                	break;
                }
            };
        };
    }
    
	// 获取最新的作品列表
	public void getUserList(int user_id)
	{
		// 避免有列表项时，重新获取
		if(user_id == 0 && adapter_ != null && adapter_.getCount() > 0)
			return;
		
		ReqData rd = ReqData.create(this, ReqData.FailHandleType.Quite);

		UserListInput in = new UserListInput();

		in.user_id = user_id;
		
		int userType = 0;
		switch(itemType_)
		{
		case 0:
			userType = 0;
			break;
		case 1:
			in.user_id = Math.abs(user_id);
			
			if(adapter_ != null && adapter_.getCount() > 0)
				in.est_dist = adapter_.getItem(adapter_.getCount() - 1).est_dist;
			break;
		case 2:
			userType = 5;
			break;
		case 3:
			userType = 2;
			break;
		case 4:
			userType = 1;
			break;
		}
		
		in.user_type = userType;
		
		rd.input = in;
		
		if(itemType_ == 1)
			sendNotification(UserListCmd.GET_NEAR_USERS, rd);
		else
			sendNotification(UserListCmd.GET_USER_LIST, rd);
	}
	
	private PullToRefreshListView getPullListView()
	{
		return (PullToRefreshListView)getViewComponent();
	}
	
	public void loadThumbs()
	{
		ListView listView = getPullListView().getRefreshableView();
		
		int start = listView.getFirstVisiblePosition();
		int end = listView.getLastVisiblePosition();

		if(start <= end)
		{
			ArrayList<ReqData> reqList = new ArrayList<ReqData>();
			
			for(int i = start; i <= end; i++)
			{
				UserInfo user = (UserInfo) listView.getItemAtPosition(i);
				if(user == null)
					continue;
				
				if(!StringUtil.isStringEmpty(user.avatar64_path))
				{
					String key = StringUtil.getFileNameFromPath(user.avatar64_path);
					if(!AppConfig.cache.isExist(key) || 
							AppConfig.avatars_update.getUpdate(key))
					{
						ReqData req = ReqData.create(this, ReqData.FailHandleType.Quite);
						InputData input = new InputData();
						
						input.url = StringUtil.getFileUrl(user.avatar64_path);
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

}
