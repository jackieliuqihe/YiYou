package com.yiyouapp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import org.puremvc.java.interfaces.INotification;
import org.puremvc.java.patterns.mediator.Mediator;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.loopj.android.http.RequestParams;
import com.yiyouapp.R;
import com.yiyouapp.adapters.WorkListAdapter;
import com.yiyouapp.adapters.WorkListAdapter.IWorkAction;
import com.yiyouapp.cmd.GetImageCmd;
import com.yiyouapp.cmd.NetErrorCmd;
import com.yiyouapp.cmd.WorkInfoCmd;
import com.yiyouapp.cmd.WorkListCmd;
import com.yiyouapp.proxy.NetImageProxy;
import com.yiyouapp.proxy.NetImageProxy.ImageType;
import com.yiyouapp.proxy.NetImageProxy.InputData;
import com.yiyouapp.proxy.NetImageProxy.OutputData;
import com.yiyouapp.proxy.WorkListProxy;
import com.yiyouapp.proxy.WorkListProxy.WorksList;
import com.yiyouapp.proxy.WorkListProxy.WorksListInput;
import com.yiyouapp.utils.AccountUtil;
import com.yiyouapp.utils.ImageUtil;
import com.yiyouapp.utils.SDCardUtil;
import com.yiyouapp.utils.SizeUtil;
import com.yiyouapp.utils.StringUtil;
import com.yiyouapp.utils.WorkUtil;
import com.yiyouapp.value.ReqData;
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
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;

public class WorkListMediator extends Mediator implements OnRefreshListener<ListView>, IWorkAction {

	public static final String WORK_LIST_INIT_COMPLETE = "work_list_init_complete";
	public static final String WORK_LIST_INIT_FAILED = "work_list_init_failed";
	public static final String WORK_LIST_UPDATE_COMPLETE = "work_list_update_complete";
	
	public static int MaxItemCount = 100;
	
	// 解码线程是否退出
	private boolean decoding_thread_running_ = true;
	
	private LinkedList<ReqData> decoding_queue_ = new LinkedList<ReqData>();

	// 图片解码完成的消息
	private final static int MSG_DECODING_FINISH = 0;
	// 列表加载图片的消息
	private final static int MSG_LOAD_THUMBS = 1;

	// 消息处理
    private Handler msg_handler_ = null;
	private Thread decoding_thread_ = null;

	private WorkListAdapter adapter_;
	
	private int visit_user_id_ = 0;
	private boolean collect_ = false;
	
	public WorkListMediator(String mediatorName, Object viewComponent, int visit_user_id, boolean collect) {
		super(mediatorName, viewComponent);
		visit_user_id_ = visit_user_id;
		collect_ = collect;

        ShareSDK.initSDK(getPullListView().getContext());
        
        if(msg_handler_ == null)
        	msg_handler_ = getMessageHandler();
        if(decoding_thread_ == null)
        {
        	decoding_thread_ = getDecodingThread();
        	decoding_thread_.start();
        }

        PullToRefreshListView pullListView = getPullListView();
        ListView listView = pullListView.getRefreshableView();

        pullListView.setMode(Mode.BOTH);
        pullListView.setOnRefreshListener(this);
        
        listView.setOnScrollListener(new OnScrollListener(){
    		
    		@Override
    	    public void onScrollStateChanged(AbsListView view, int scrollState) {
    	        switch (scrollState) {
    	            case OnScrollListener.SCROLL_STATE_IDLE:
    	            	sendLoadThumbsMsg();
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
        /*listView.SetOnFinishListener(new OnFinishListener(){

			@Override
			public void onFinish(XListView lv) {
				super.onFinish(lv);
				
				loadThumbs();
			}
        	
        });*/

	}

	@Override
	public void onRegister() {
		// TODO Auto-generated method stub
		super.onRegister();
	}

	@Override
	public void onRemove() {
		super.onRemove();
		
		decoding_thread_running_ = false;
		synchronized(decoding_queue_)
		{
			decoding_queue_.clear();
			decoding_queue_.notify();
		}
	}

	public void setInitWorksList(ArrayList<Work> works)
	{
        adapter_ = new WorkListAdapter(getPullListView().getContext(), 
        		R.layout.item_work, works, this, !collect_ && visit_user_id_ > 0);
        getPullListView().setAdapter(adapter_);
	}
	
	@Override
	public String[] listNotificationInterests() {
		return new String[]{WorkListProxy.GET_WORKS_LIST_COMPLETE,
				WorkListProxy.GET_WORKS_LIST_FAILED, 
				NetImageProxy.GET_IMAGE_COMPLETE};
	}

	@Override
	public void handleNotification(INotification notification) {
		ReqData req = (ReqData)notification.getBody();

		if(req.sender.equals(this))
		{
			String noteName = notification.getName();
			if(noteName.equals(WorkListProxy.GET_WORKS_LIST_COMPLETE))
			{
				PullToRefreshListView pullListView = ((PullToRefreshListView)getViewComponent());
				
				ListView lv = pullListView.getRefreshableView();
				pullListView.onRefreshComplete();
				
				WorksList worksList = (WorksList)req.output;

				WorksListInput input = (WorksListInput)req.input;
				int work_id = input.work_id;
				
				int count = worksList.works.size();
				if(count == 0)
				{
					if(work_id < 0)
						Toast.makeText(getPullListView().getContext(), "亲，没有更多的作品啦", Toast.LENGTH_SHORT).show();
					else if(work_id > 0)
						Toast.makeText(getPullListView().getContext(), "没有最新的作品啦", Toast.LENGTH_SHORT).show();
					else
				        sendNotification(WORK_LIST_INIT_COMPLETE, req);
					return;
				}

				if(work_id == 0)
				{
					if(adapter_ == null)
					{
						setInitWorksList(worksList.works);

		    			sendLoadThumbsMsg();
		    			
				        sendNotification(WORK_LIST_INIT_COMPLETE, req);
				        return;
					}
			        
					int adapterCount = adapter_.getCount();
					for(int i = count; i < adapterCount; i++)
						worksList.works.add(adapter_.getItem(i));
					
					adapter_.clear();
					adapter_.addAll(worksList.works);

					adapter_.notifyDataSetChanged();

	    			sendLoadThumbsMsg();
				}
				else if(work_id > 0)
				{
					for(int i = 0; i < count; i++)
					{
						adapter_.insert(worksList.works.get(i), 0);
					}
					
					// 删除超过显示的作品数
					for(int i = adapter_.getCount() - 1; i >= MaxItemCount; i--)
					{
						adapter_.remove(adapter_.getItem(i));
					}

					adapter_.notifyDataSetChanged();

					lv.setSelectionFromTop(count + 1, 200);
                    
					sendLoadThumbsMsg();
				}
				else
				{
					for(int i = 0; i < count; i++)
					{
						adapter_.add(worksList.works.get(i));
					}

					// 删除超过显示的作品数
					int adapterCount = adapter_.getCount();
					if(MaxItemCount < adapterCount)
					{
						for(int i = MaxItemCount; i < adapterCount; i++)
						{
							adapter_.remove(adapter_.getItem(0));
						}

						adapter_.notifyDataSetChanged();

						lv.setSelectionFromTop(MaxItemCount - count + 1, lv.getHeight() - 100);
					}
					else
					{
						adapter_.notifyDataSetChanged();
					}

					sendLoadThumbsMsg();

					return;
				}
				
				// 保存下载的作品列表
				int saveCount = 10;
				if(adapter_.getCount() < saveCount)
					saveCount = adapter_.getCount();
				
				ArrayList<Work> works = new ArrayList<Work>();
				
				for(int i = 0; i < saveCount; i++)
					works.add(adapter_.getItem(i));

				worksList.works = works;
		        sendNotification(WORK_LIST_UPDATE_COMPLETE, req);
			}
			else if(noteName.equals(NetImageProxy.GET_IMAGE_COMPLETE))
			{
				// 设置头像已经更新完
				InputData input = (InputData)req.input;;
				AppConfig.avatars_update.setUpdate(input.key);
				
				notifyDecoding(req);
			}
			else if(noteName.equals(WorkListProxy.GET_WORKS_LIST_FAILED))
			{
				getPullListView().onRefreshComplete();
		        sendNotification(WORK_LIST_INIT_FAILED, req);
			}
		}
	}
	
	@Override
	public void onRefresh(PullToRefreshBase<ListView> refreshView) {

		if(adapter_ == null)
		{
			getPullListView().onRefreshComplete();
			return;
		}
		
		PullToRefreshListView pullListView = getPullListView();
		ListView listView = pullListView.getRefreshableView();
		
		String label = DateUtils.formatDateTime(listView.getContext().getApplicationContext(), System.currentTimeMillis(),
				DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

		// Update the LastUpdatedLabel 
		refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);

		if(pullListView.getCurrentMode() == Mode.PULL_FROM_START)
		{
			if(adapter_.getCount() > 0)
				getWorksList(adapter_.getItem(0).works_id);
			else
				getWorksList(0);
		}
		else if(pullListView.getCurrentMode() == Mode.PULL_FROM_END)
		{
			if(adapter_.getCount() > 0)
				getWorksList(-adapter_.getItem(adapter_.getCount() - 1).works_id);
			else
				getWorksList(0);
		}
	}

	public void onPriseChanged(Work work, boolean checked)
	{
		if(checked)
			work.prise_count++;
		else if(work.prise_count > 0)
			work.prise_count--;
		adapter_.notifyDataSetChanged();
		
		RequestParams params = new RequestParams();

		params.add("user_id", String.valueOf(work.user_id));
		params.add("work_id", String.valueOf(work.works_id));
		params.add("i_prise", String.valueOf(checked ? 1 : 0));
		
		ReqData req = ReqData.create(this, ReqData.FailHandleType.Quite);
		req.input = params;
		
		sendNotification(WorkInfoCmd.WORK_INFO_CHANGE, req);
	}

	public void onCollectChanged(Work work, boolean checked)
	{
		if(checked)
			work.collect_count++;
		else if(work.collect_count > 0)
			work.collect_count--;
		adapter_.notifyDataSetChanged();
		
		RequestParams params = new RequestParams();

		params.add("user_id", String.valueOf(work.user_id));
		params.add("work_id", String.valueOf(work.works_id));
		params.add("i_collect", String.valueOf(checked ? 1 : 0));
		
		ReqData req = ReqData.create(this, ReqData.FailHandleType.Quite);
		req.input = params;
		
		sendNotification(WorkInfoCmd.WORK_INFO_CHANGE, req);
	}

	private void showShare(Work work) {
		
		Context ctx = getPullListView().getContext();
		
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        //oks.disableSSOWhenAuthorize();
        
        oks.setDialogMode();
        
        // 分享时Notification的图标和文字
        oks.setNotification(R.drawable.ic_launcher, ctx.getString(R.string.app_name));
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        oks.setTitle(ctx.getString(R.string.share));
        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
        oks.setTitleUrl("http://www.yiyouapp.com");
        // text是分享文本，所有平台都需要这个字段
        oks.setText("亲爱的艺友们！\"" + work.user_name + "\"在艺友app分享作品：" + work.work_desc + " 官网 http://www.yiyouapp.com");
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        
        String path = AppConfig.cache_dir + StringUtil.getFileNameFromPath(work.thumb_path[0]);
        oks.setImagePath(path);
        
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl("http://www.yiyouapp.com");
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        oks.setComment(work.work_desc);
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite(ctx.getString(R.string.app_name));
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl("http://www.yiyouapp.com");

        // 启动分享GUI
        oks.show(ctx);
    }
	
	// 点击分析按钮
	public void onShareClick(Work work)
	{
		showShare(work);
		/*
		Intent intent=new Intent(Intent.ACTION_SEND);
		
        intent.setType("image/*");
        
        intent.putExtra(Intent.EXTRA_SUBJECT, "分享");
        intent.putExtra(Intent.EXTRA_TEXT, "亲，艺友会员'" + work.user_name + "'分享给你作品：" + work.work_desc);
        intent.putExtra(Intent.EXTRA_STREAM, AppConfig.cache.sdcard.getExistFileUri(StringUtil.getFileNameFromPath(work.thumb_path)));
        
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        
        getPullListView().getContext().startActivity(Intent.createChooser(intent, "分享到平台"));*/
	}
	
	// 点击评论按钮
	public void onCommentClick(Work work)
	{
		Context context = getPullListView().getContext();
		Intent intent = new Intent(context, WorkCommentActivity.class);
		
		Bundle bundle = new Bundle();
		bundle.putSerializable("work", work);
		
		intent.putExtras(bundle);
	
		context.startActivity(intent);
	}
	
	// 点击作品缩略图
	public void onThumbClick(Work work)
	{
		Context context = getPullListView().getContext();
		Intent intent = new Intent(context, WorkDetailActivity.class);
		
		Bundle bundle = new Bundle();
		bundle.putSerializable("work", work);
		
		intent.putExtras(bundle);
	
		context.startActivity(intent);
	}
	
	// 点击作品的用户头像
	public void onAvatarClick(Work work)
	{
		Context context = getPullListView().getContext();
		Intent intent;
		
		if(work.user_id == AppConfig.account.info.user_id)
		{
			intent = new Intent(context, UserCenterSelfActivity.class);
		}
		else
		{
			intent = new Intent(context, UserCenterOtherActivity.class);

			intent.putExtra(AppConfig.INTENT_USER_ID, work.user_id);
			intent.putExtra(AppConfig.INTENT_USER_NAME, work.user_name);
		}
		
		context.startActivity(intent);
	}
	
	// 获取最新的作品列表
	public void getWorksList(int work_id)
	{
		ReqData rd = ReqData.create(this, ReqData.FailHandleType.Quite);
		
		WorksListInput input = new WorksListInput();
		
		input.user_id = visit_user_id_;
		input.work_id = work_id;
		input.collect = collect_;
		
		rd.input = input;
		
		sendNotification(WorkListCmd.GET_WORKS_LIST, rd);
	}
	
	private PullToRefreshListView getPullListView()
	{
		return (PullToRefreshListView)getViewComponent();
	}
	
	private void sendLoadThumbsMsg()
	{
		Message message = Message.obtain();
		message.what = MSG_LOAD_THUMBS;
		msg_handler_.sendMessage(message);
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
				Work work = (Work) listView.getItemAtPosition(i);
				if(work == null)
					continue;
				
				// thumb
				String key = StringUtil.getFileNameFromPath(work.thumb_path[0]);
				if(!AppConfig.cache.isExist(key))
				{
					ReqData req = ReqData.create(this, ReqData.FailHandleType.Quite);
					InputData input = new InputData();
					
					input.url = StringUtil.getFileUrl(work.thumb_path[0]);
					input.cachable = true;
					input.type = ImageType.Thumbnail;
					input.position = i;
					input.key = key;
					
					req.input = input;
					
					reqList.add(req);
				}
				
				if(!StringUtil.isStringEmpty(work.avatar_path))
				{
					key = StringUtil.getFileNameFromPath(work.avatar_path);
					if(!AppConfig.cache.isExist(key) || 
							AppConfig.avatars_update.getUpdate(key))
					{
						ReqData req = ReqData.create(this, ReqData.FailHandleType.Quite);
						InputData input = new InputData();
						
						input.url = StringUtil.getFileUrl(work.avatar_path);
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

    private Handler getMessageHandler()
    {
    	return new Handler()
        {
            public void handleMessage(Message msg) 
            {
            	super.handleMessage(msg);
            	
                switch(msg.what)
                {
                case MSG_DECODING_FINISH:
                	ReqData req = (ReqData)msg.obj;

    				InputData input = (InputData)req.input;
    				OutputData output = (OutputData)req.output;
    				
    				ListView lv = getPullListView().getRefreshableView();
    				int first = lv.getFirstVisiblePosition();
                	View view = lv.getChildAt(input.position - first);
                	if(view == null)
                		break;
                	
                	ImageView iv = null;
                	switch(input.type)
                	{
                	case Thumbnail:
	                	iv = (ImageView)view.findViewById(R.id.work_thumb);
	                	break;
                	case Avatar:
	                	iv = (ImageView)view.findViewById(R.id.user_avatar);
                		break;
                	}

                	if(iv == null)
                		break;
                	iv.setImageBitmap(output.bitmap);
                	
                	break;
                case MSG_LOAD_THUMBS:
                	loadThumbs();
                	break;
                }
            };
        };
    }
    
    private Thread getDecodingThread()
    {
    	return new Thread(new Runnable(){

    		@Override
    		public void run() 
    		{
    			while(decoding_thread_running_)
    			{
    				ReqData req;
					
    				synchronized(decoding_queue_)
    				{
    					try {
    						if(decoding_queue_.peek() == null)
    							decoding_queue_.wait();  
                        } catch (InterruptedException e) {
                        	continue;
                        }
    					
    					if(decoding_queue_.peek() == null)
    						continue;
    					
    					req = decoding_queue_.removeFirst();
    				}

    				InputData input = (InputData)req.input;
    				OutputData output = (OutputData)req.output;
    				
    				output.bitmap = ImageUtil.decodingBytes(output.data);
    				
    				if(output.bitmap != null)
    				{
	    				Message message = Message.obtain();
	    				
		    			message.obj = req;
		    			message.what = MSG_DECODING_FINISH;
		    			
		    			msg_handler_.sendMessage(message);
		    			
		    			// 缓存起来
		    			AppConfig.cache.setImage(input.key, output.bitmap, output.data);
    				}
    				
    				// 释放不使用的空间
    				output.data = null;
    			}
    		}
        	
        });
    }
    
    public void notifyDecoding(ReqData req)
    {
    	synchronized(decoding_queue_)
		{
    		decoding_queue_.addLast(req);
    		decoding_queue_.notify();
		}
    }
    
    public void cancelAllLoading()
    {
    	synchronized(decoding_queue_)
		{
    		decoding_queue_.clear();
		}
    }
    
}
