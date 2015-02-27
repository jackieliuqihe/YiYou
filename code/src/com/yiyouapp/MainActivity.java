package com.yiyouapp;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

import org.puremvc.java.interfaces.INotification;
import org.puremvc.java.patterns.mediator.Mediator;

import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
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
import com.yiyouapp.controls.Updater;
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
import com.yiyouapp.value.Work;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
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
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
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

public class MainActivity extends MediatorActivity implements OnClickListener, OnPageChangeListener {

	public static final String NAME = "MainActivity";
	
	// 导航条的横杆
	private NavItemMover item_nav_sel_;

	private ViewPager tag_pager_;
	
	private int last_position_sel_ = 0;
	
	private ArrayList<RelativeLayout> nav_items_ = new ArrayList<RelativeLayout>();
	private ArrayList<View> channel_pages_ = new ArrayList<View>();

	private WorkListMediator workListMediator_;
	
	private PullToRefreshListView pullListView_;
	
	// 菜单弹出窗口
	private PopupWindow menu_;
	private View menuView_;
	
	private ImageButton menuBtn_;
	private ImageView menuItem2Dot_;
	
	private long lastGetNewsTime_ = 0;

	// 切换到桌面的方式，是否回到作品页
	private boolean switchToChannel1_ = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// 检查更新
		Updater.checkUpdate();
		
		// 按钮事件
		findViewById(R.id.publish_btn).setOnClickListener(this);
		
		menuBtn_ = (ImageButton)findViewById(R.id.menu_btn);
		menuBtn_.setOnClickListener(this);
        
		item_nav_sel_ = (NavItemMover)findViewById(R.id.img_item_sel);
		item_nav_sel_.init();

        // 创建频道页
        createChannelPage();
        
        createMenu();
        menuItem2Dot_ = (ImageView)menuView_.findViewById(R.id.menu_item2_dot);
        
        workListMediator_ = new WorkListMediator(AppConfig.FILENAME_MAIN_WORKS, pullListView_, 0, false);
        facade.registerMediator(workListMediator_);
        
        // 读取保存的作品列表
        ArrayList<Work> works = readWorksList();
        if(works != null)
        {
        	workListMediator_.setInitWorksList(works);
        }
        else
        {
			loading_.setLoading();
			pullListView_.getRefreshableView().setVisibility(View.GONE);
        }
	}

	@Override
	protected String getName() {
		return NAME;
	}

	@Override
	public String[] listNotificationInterests() {
		return new String[]{WorkListMediator.WORK_LIST_INIT_COMPLETE,
				WorkListMediator.WORK_LIST_UPDATE_COMPLETE,
				UserNewsProxy.GET_UNVIEW_NEWS_COUNT_COMPLETE,
				WorkListMediator.WORK_LIST_INIT_FAILED};
	}

	@Override
	public void handleNotification(INotification notification) {
		String noteName = notification.getName();
		if(noteName.equals(WorkListMediator.WORK_LIST_INIT_COMPLETE))
		{
			ReqData req = (ReqData)notification.getBody();

			Mediator med = (Mediator)req.sender;
			if(med.getMediatorName().equals(AppConfig.FILENAME_MAIN_WORKS))
			{
				loading_.setCompleted();
				pullListView_.getRefreshableView().setVisibility(View.VISIBLE);
				
				WorksList worksList = (WorksList)req.output;

				saveWorksList(worksList.works);
			}
		}
		else if(noteName.equals(WorkListMediator.WORK_LIST_UPDATE_COMPLETE))
		{
			ReqData req = (ReqData)notification.getBody();

			Mediator med = (Mediator)req.sender;
			if(med.getMediatorName().equals(AppConfig.FILENAME_MAIN_WORKS))
			{
				WorksList worksList = (WorksList)req.output;

				saveWorksList(worksList.works);
			}
		}
		else if(noteName.equals(UserNewsProxy.GET_UNVIEW_NEWS_COUNT_COMPLETE))
		{
			ReqData req = (ReqData)notification.getBody();
			int count = (Integer)req.output;
			
			AppConfig.newsCount = count;
			setNewsDot(AppConfig.newsCount > 0);
			setNewsDotMenuItem(false);
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
    	facade.removeMediator(AppConfig.FILENAME_MAIN_WORKS);
		super.onDestroy();
	}
    
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		
		switch(keyCode)
		{
		case KeyEvent.KEYCODE_BACK:
			switchToChannel1_ = true;
			
	        Intent intent = new Intent(Intent.ACTION_MAIN);
	        
	        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);      
	        intent.addCategory(Intent.CATEGORY_HOME);
	        
	        startActivity(intent);
	        
	        return true;      
	    }
		
		return super.onKeyDown(keyCode, event);
	}

	private ArrayList<Work> readWorksList()
	{
		if(AppConfig.account_dir != null)
		{
			byte[] bytes = SDCardUtil.readFile(AccountUtil.getCurrAccountDir() + AppConfig.FILENAME_MAIN_WORKS);
			if(bytes != null)
			{
				ArrayList<Work> works = WorkUtil.jsonToWorks(new String(bytes));
				if(works.size() > 0)
					return works;
			}
		}
		
		return null;
	}
	
	private void saveWorksList(ArrayList<Work> works)
	{
		if(AppConfig.account_dir != null)
		{
			String json = WorkUtil.worksToJSON(works);
			if(!StringUtil.isStringEmpty(json))
    			SDCardUtil.writeFile(AccountUtil.getCurrAccountDir() + AppConfig.FILENAME_MAIN_WORKS, json.getBytes());
		}
	}
	
	@Override  
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
    	AppConfig.work_photo = PhotoObtainer.processBitmap(requestCode, resultCode, data);
    	
    	if(AppConfig.work_photo != null)
    	{
    		Intent intent = new Intent(this, UploadWorkActivity.class);
			
    		intent.putExtra(AppConfig.INTENT_HAS_WORK_PHOTO, true);
    		
			startActivity(intent);
    	}
    }
    
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		StatService.onResume(this);

		if(switchToChannel1_)
		{
			switchToChannel1_ = false;
			tag_pager_.setCurrentItem(0);
		}
		
		// 获取用户账户信息
		sendNotification(AccountCmd.GET_ACCOUNT_INFO, ReqData.create(this, ReqData.FailHandleType.Quite));

		// 设置消息点
		setNewsDot(AppConfig.newsCount > 0);
		setNewsDotMenuItem(false);
		
		// 隔60秒才能取数据
        if((System.currentTimeMillis() - lastGetNewsTime_) > 30000 &&
        		AppConfig.newsCount == 0)
        {
            lastGetNewsTime_ = System.currentTimeMillis();
			// 获取用户最新消息
			sendNotification(UserNewsCmd.GET_UNVIEW_NEWS_COUNT, ReqData.create(this, ReqData.FailHandleType.Notify));
        }
        
		// 获取最新的作品列表
		((WorkListMediator)facade.retrieveMediator(AppConfig.FILENAME_MAIN_WORKS)).getWorksList(0);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		StatService.onPause(this);
		closeMenu();
	}

	// 设置消息点
	private void setNewsDot(boolean hasDot)
	{
		if(hasDot)
			menuBtn_.setImageResource(R.drawable.menu_dot_button);
		else
			menuBtn_.setImageResource(R.drawable.menu_button);
	}

	// 设置消息点
	private void setNewsDotMenuItem(boolean hasDot)
	{
		if(hasDot)
			menuItem2Dot_.setVisibility(View.VISIBLE);
		else
			menuItem2Dot_.setVisibility(View.GONE);
	}
	
	@Override
	public void onClick(View v) {
		
		Intent intent;
		
		switch(v.getId())
		{
		case R.id.publish_btn:
			DialogUtil.showPhotoSelectDialog(this);
			break;
		case R.id.menu_btn:

			String name = "我的空间";
			if(!StringUtil.isStringEmpty(AppConfig.account.info.user_name))
				name = AppConfig.account.info.user_name;

			ImageView iv = (ImageView)menuView_.findViewById(R.id.user_avatar);
			AccountUtil.setAvatar(iv);
			
			((TextView)menuView_.findViewById(R.id.menu_item1_text)).setText(name);

	        View view = findViewById(R.id.nav_bar_top);
	        int[] loc = new int[2];
	        view.getLocationOnScreen(loc);
	        
	        // 这里是位置显示方式,在屏幕的左侧  
	        menu_.showAtLocation(v, Gravity.TOP | Gravity.RIGHT, 6, loc[1]);

			setNewsDot(false);
			setNewsDotMenuItem(AppConfig.newsCount > 0);
			
			break;
		case R.id.list_item1:
			intent = new Intent(this, UserListActivity.class);
			intent.putExtra(AppConfig.INTENT_CHANNEL2_TYPE, 0);
			startActivity(intent);
			break;
		case R.id.list_item2:
			intent = new Intent(this, UserListActivity.class);
			intent.putExtra(AppConfig.INTENT_CHANNEL2_TYPE, 1);
			startActivity(intent);
			break;
		case R.id.list_item3:
			intent = new Intent(this, UserListActivity.class);
			intent.putExtra(AppConfig.INTENT_CHANNEL2_TYPE, 2);
			startActivity(intent);
			break;
		case R.id.list_item4:
			intent = new Intent(this, UserListActivity.class);
			intent.putExtra(AppConfig.INTENT_CHANNEL2_TYPE, 3);
			startActivity(intent);
			break;
		case R.id.list_item5:
			intent = new Intent(this, UserListActivity.class);
			intent.putExtra(AppConfig.INTENT_CHANNEL2_TYPE, 4);
			startActivity(intent);
			break;

		case R.id.menu_item1:
			
			intent = new Intent(this, UserCenterSelfActivity.class);
			startActivity(intent);

			closeMenu();
			
			break;
		case R.id.menu_item2:
			
			intent = new Intent(this, UserNewsListActivity.class);
			startActivity(intent);

			closeMenu();
			break;
		case R.id.menu_item3:

			if(AppConfig.account.info == null)
				break;
			
			intent = new Intent(this, WorkListActivity.class);
			intent.putExtra(AppConfig.INTENT_USER_ID, AppConfig.account.info.user_id);
			intent.putExtra(AppConfig.INTENT_USER_NAME, "我的收藏");
			intent.putExtra(AppConfig.INTENT_COLLECT, true);
			
			startActivity(intent);
			
			closeMenu();
			break;
		case R.id.menu_item4:

			intent = new Intent(this, PreferencesActivity.class);
			startActivity(intent);

			closeMenu();
			break;
		}
	}

	private void createMenu()
	{
		menuView_ = getLayoutInflater().inflate(R.layout.popup_main_menu, null,false);
		
		menuView_.findViewById(R.id.menu_item1).setOnClickListener(this);
		menuView_.findViewById(R.id.menu_item2).setOnClickListener(this);
		menuView_.findViewById(R.id.menu_item3).setOnClickListener(this);
		menuView_.findViewById(R.id.menu_item4).setOnClickListener(this);

        // 创建PopupWindow实例,200,LayoutParams.MATCH_PARENT分别是宽度和高度
		int menuWidth = (int)(AppConfig.screen_width * 4.0 / 7.0);
        menu_ = new PopupWindow(menuView_, menuWidth, LayoutParams.WRAP_CONTENT, true);  
        // 设置动画效果  
        menu_.setAnimationStyle(R.style.MenuAnim);  
        
        // 点击其他地方消失  
        menuView_.setOnTouchListener(new OnTouchListener() {  
            @Override  
            public boolean onTouch(View v, MotionEvent event) {  
            	closeMenu();
                return false;  
            }  
        });
        
        menuView_.setFocusable(true);
        menuView_.setFocusableInTouchMode(true);
        menuView_.setOnKeyListener(new OnKeyListener() {
            @Override  
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {  
                	closeMenu();
                    return true;  
                }  
                return false;  
            }  
        });
	}
	
	private void closeMenu()
	{
		if (menu_ != null && menu_.isShowing()) {  
            menu_.dismiss();
        }

		setNewsDot(AppConfig.newsCount > 0);
		setNewsDotMenuItem(false);
	}
	
	/**
	 * 导航项点击监听
	 */
	public class NavItemClickListener implements View.OnClickListener {
		private int index = 0;

		public NavItemClickListener(int i) {
			index = i;
		}
		
		@Override
		public void onClick(View v) {
			tag_pager_.setCurrentItem(index);
		}
	};
	
	 /* 页卡切换监听
	 */
	public void onPageSelected(int position) {

		TextView tv = (TextView)(nav_items_.get(last_position_sel_).findViewById(R.id.nav_item_text));
		tv.setTextColor(getResources().getColor(android.R.color.black));
		
		tv = (TextView)(nav_items_.get(position).findViewById(R.id.nav_item_text));
		tv.setTextColor(getResources().getColor(R.color.top_right_btn_bg_color));
		
		last_position_sel_ = position;
	}
	
	public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
		
		int nav_item_width = AppConfig.screen_width / 2;
		int left = nav_item_width * position + (int)(positionOffset * (float)nav_item_width);
		item_nav_sel_.setHorizontal(left);
	}

	public void onPageScrollStateChanged(int state) {
		
	}

	// 创建频道页
	private void createChannelPage()
	{
		// 导航项
        nav_items_.add((RelativeLayout)findViewById(R.id.nav_item1));
        nav_items_.add((RelativeLayout)findViewById(R.id.nav_item2));

        for(int i = 0; i < nav_items_.size(); i++)
        	nav_items_.get(i).setOnClickListener(new NavItemClickListener(i));

        tag_pager_ = (ViewPager)findViewById(R.id.tabpager);
        tag_pager_.setOnPageChangeListener(this);

        // 导航页
        LayoutInflater inflater = LayoutInflater.from(this);

    	View channel1 = inflater.inflate(R.layout.main_channel1, null);
    	channel1.setTag(0);

    	channel_pages_.add(channel1);
    	pullListView_ = (PullToRefreshListView)channel1.findViewById(R.id.work_list);

        // 初始化加载显示
        initLoading((ViewGroup)channel1, pullListView_);
        
    	View channel2 = inflater.inflate(R.layout.main_channel2, null);
    	channel2.setTag(1);
    	
    	channel_pages_.add(channel2);

		// 第二个频道页
		channel2.findViewById(R.id.list_item1).setOnClickListener(this);
		channel2.findViewById(R.id.list_item2).setOnClickListener(this);
		
		channel2.findViewById(R.id.list_item3).setOnClickListener(this);
		channel2.findViewById(R.id.list_item4).setOnClickListener(this);
		channel2.findViewById(R.id.list_item5).setOnClickListener(this);

        //填充ViewPager的数据适配器
        PagerAdapter adapter = new PagerAdapter() {
			
			@Override
			public boolean isViewFromObject(View arg0, Object arg1) {
				return arg0 == arg1;
			}
			
			@Override
			public int getCount() {
				return channel_pages_.size();
			}

			@Override
			public void destroyItem(View container, int position, Object object) {
				((ViewPager)container).removeView(channel_pages_.get(position));
			}
			
			@Override
			public Object instantiateItem(View container, int position) {
				((ViewPager)container).addView(channel_pages_.get(position));
				return channel_pages_.get(position);
			}
		};
		
		tag_pager_.setAdapter(adapter);

	}
	
}
