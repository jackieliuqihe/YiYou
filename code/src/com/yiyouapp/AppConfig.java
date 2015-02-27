package com.yiyouapp;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import org.puremvc.java.interfaces.IMediator;

import com.baidu.frontia.Frontia;
import com.loopj.android.http.PersistentCookieStore;
import com.yiyouapp.controls.cache.ImageCache;
import com.yiyouapp.proxy.NetTextProxy;
import com.yiyouapp.utils.Location;
import com.yiyouapp.utils.SDCardUtil;
import com.yiyouapp.utils.SizeUtil;
import com.yiyouapp.value.AvatarUpdate;
import com.yiyouapp.value.TypeDef;
import com.yiyouapp.value.UserInfo;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;

public class AppConfig {

	/*
	 * 以下为不可变的量
	 */
	
	// app 名字
	public static final String APP_NAME = "com.yiyouapp.YiYou";

	// host
	public static final String BASE_URL = "http://app.yiyouapp.com";
	
	// image host
	public static final String FILE_BASE_URL = "http://data.yiyouapp.com/";

	// 配置文件路径
	public static final String FILENAME_MAIN_WORKS = "main_works.dat";
	public static final String FILENAME_MY_WORKS = "works2.dat";
	public static final String FILENAME_USERINFO = "userinfo.dat";
	public static final String FILENAME_AVATAR = "avatar.dat";

	// 是否选择了作品图的intent传参名
	public static final String INTENT_HAS_WORK_PHOTO = "has_work_photo";
	// 选择的作品类型的intent传参名
	public static final String INTENT_WORK_TYPE = "work_type";
	// 列表页标题intent传参名
	public static final String INTENT_CHANNEL2_TYPE = "channel2_type";

	public static final String INTENT_USER_ID = "user_id";

	public static final String INTENT_USER_TYPE = "user_type";

	public static final String INTENT_USER_NAME = "user_name";

	public static final String INTENT_AVATAR_PATH = "avatar_path";

	public static final String INTENT_COLLECT = "collect";

	public static final String INTENT_WORK_ID = "work_id";

	// 图像规格限定
	public static final int max_image_width = 3000; // 单位px
	public static final int max_image_height = 3000; // 单位px
	public static final int avatar_image_size = 30; // 单位dp

	// 上传图像最大尺寸
	public static final int upload_image_width = 1024; // 单位px
	public static final int upload_image_height = 1024; // 单位px
	
	// 网络请求重试次数
	public static final int retry_count = 2;

	// 加解密的key
	public static final byte[] encode_key = new byte[]{0x3F, 0x42, (byte) 0x99, 0x1F, (byte)0xAA, 0x33, 0x45, 0x22};
	public static final int encode_length = 128;

	// sdcard cache
	public static final long max_sdcard_cache_size = 1 << 30; // 1GB
	public static final int max_sdcard_files_count = 10000; // 1万个文件
	public static final long clearto_sdcard_cache_size = 1 << 26; // 64MB
	public static final int clearto_sdcard_files_count = 2000; // 2000个文件

	// 百度API key
	public static final String baidu_api_key = "8F9FD5cglWj0vWtngpXNepjW";
	
	/*
	 * 以下为运行时可变的量
	 */

	// 版本信息
	public static int version_code = 100;
	public static String version_name = "1.0";
	
	// 拍照出来的图片
	public static Bitmap work_photo;

	// 屏幕宽度
	public static int screen_width;
	public static float density;

	// 表情尺寸计算
	public static int emotion_size = 30;
	
	// 外部存储目录
	public static String app_data_dir = null;

	// 文件缓存目录
	public static String cache_dir = null;

	// 账户配置目录
	public static String account_dir = null;

	// cookie store
	public static PersistentCookieStore cookie = null;

	// 程序配置记录
	public static AppSettings settings = null;
	
	// 缓存
	public static ImageCache cache = null;
	
	// 所有启动的activity
	public static HashMap<String, Activity> activities = null;
	// 当前的activity
	public static MediatorActivity current_activity = null;
	// 当前proxy
	public static NetTextProxy current_proxy = null;
	
	// 用户账户
	public static AppAccount account;
	// 用户消息数量
	public static int newsCount;

	// 百度端服务是否初始化成功
	public static boolean is_frontia_init = false;

	// app是否刚启动
	public static boolean is_start = false;
	
	// 用户头像更新
	public static AvatarUpdate avatars_update = null;
	
	// 用户经纬度
	public static double latitude = 0.0;
	public static double longitude = 0.0;
	public static String address;
	public static String city;
	
	public static void Initialize(MediatorActivity activity)
	{
        DisplayMetrics metric = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metric);
        screen_width = metric.widthPixels;
        
        density = activity.getResources().getDisplayMetrics().density;
        
        // 缓存使用策略，优先使用sdcard，没有再使用手机内存

        // 避免外存和内存使用的交替摇摆
        boolean hasInnerCache = false;
        
    	File cacheDir = activity.getCacheDir();
    	if(cacheDir.exists() && cacheDir.isDirectory() && cacheDir.canWrite())
    	{
    		File file = new File(cacheDir.getAbsolutePath() + "/account/");
    		if(file.exists() && file.isDirectory())
    			hasInnerCache = true;
    	}
    	
        // 只有加载sdcard了，才可以用
        if(!hasInnerCache && Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
        {
        	cacheDir = activity.getExternalCacheDir();
        	if(cacheDir.exists() && cacheDir.isDirectory() && cacheDir.canWrite())
        		app_data_dir = cacheDir.getAbsolutePath() + "/";//SDCardUtil.createDir(Environment.getExternalStorageDirectory().getAbsolutePath() + "/yiyou/");
        	else
        		app_data_dir = null;
        	
            //photo_dir = SDCardUtil.createDir(Environment.getExternalStorageDirectory().getAbsolutePath() + "/DCIM/Camera/");
            if(app_data_dir != null)
            {
	            cache_dir = SDCardUtil.createDir(app_data_dir + "cache/");
	            account_dir = SDCardUtil.createDir(app_data_dir + "account/");
            }
        }
        else
        {
			Log.w(AppConfig.APP_NAME, "sdcard is not writable");
        }

        if(app_data_dir == null)
        {
        	cacheDir = activity.getCacheDir();
        	if(cacheDir.exists() && cacheDir.isDirectory() && cacheDir.canWrite())
        		app_data_dir = cacheDir.getAbsolutePath() + "/";
        	else
        		app_data_dir = null;
        	
            if(app_data_dir != null)
            {
	            cache_dir = SDCardUtil.createDir(app_data_dir + "cache/");
	            account_dir = SDCardUtil.createDir(app_data_dir + "account/");
            }
        }
        
		try {
			PackageManager pm = activity.getPackageManager();
			PackageInfo pinfo = pm.getPackageInfo(activity.getPackageName(), PackageManager.GET_CONFIGURATIONS);

			version_code = pinfo.versionCode;
			version_name = pinfo.versionName;
		} catch (NameNotFoundException e) {
		}

		settings = new AppSettings(activity);
		cookie = new PersistentCookieStore(activity);
		cache = new ImageCache();
		
		activities = new HashMap<String, Activity>();

		account = new AppAccount();
		avatars_update = new AvatarUpdate();
		
		latitude = settings.getLatitude();
		longitude = settings.getLongitude();
		address = settings.getAddress();
		city = settings.getCity();
		
		newsCount = 0;
		
		emotion_size = SizeUtil.dp2px(metric.widthPixels / 24);
		
		// 类型定义初始化
		TypeDef.Initialize();
		
		Location.startGetLocation();
		is_frontia_init = Frontia.init(activity.getApplicationContext(), baidu_api_key);
		
		// 当前activity
		current_activity = activity;
	}

	public static void Uninitialize()
	{
		Collection<Activity> acts = activities.values();
		
		for(Iterator<Activity> it = acts.iterator(); it.hasNext();)
			it.next().finish();
	}
	
}
