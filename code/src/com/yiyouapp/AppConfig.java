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
	 * ����Ϊ���ɱ����
	 */
	
	// app ����
	public static final String APP_NAME = "com.yiyouapp.YiYou";

	// host
	public static final String BASE_URL = "http://app.yiyouapp.com";
	
	// image host
	public static final String FILE_BASE_URL = "http://data.yiyouapp.com/";

	// �����ļ�·��
	public static final String FILENAME_MAIN_WORKS = "main_works.dat";
	public static final String FILENAME_MY_WORKS = "works2.dat";
	public static final String FILENAME_USERINFO = "userinfo.dat";
	public static final String FILENAME_AVATAR = "avatar.dat";

	// �Ƿ�ѡ������Ʒͼ��intent������
	public static final String INTENT_HAS_WORK_PHOTO = "has_work_photo";
	// ѡ�����Ʒ���͵�intent������
	public static final String INTENT_WORK_TYPE = "work_type";
	// �б�ҳ����intent������
	public static final String INTENT_CHANNEL2_TYPE = "channel2_type";

	public static final String INTENT_USER_ID = "user_id";

	public static final String INTENT_USER_TYPE = "user_type";

	public static final String INTENT_USER_NAME = "user_name";

	public static final String INTENT_AVATAR_PATH = "avatar_path";

	public static final String INTENT_COLLECT = "collect";

	public static final String INTENT_WORK_ID = "work_id";

	// ͼ�����޶�
	public static final int max_image_width = 3000; // ��λpx
	public static final int max_image_height = 3000; // ��λpx
	public static final int avatar_image_size = 30; // ��λdp

	// �ϴ�ͼ�����ߴ�
	public static final int upload_image_width = 1024; // ��λpx
	public static final int upload_image_height = 1024; // ��λpx
	
	// �����������Դ���
	public static final int retry_count = 2;

	// �ӽ��ܵ�key
	public static final byte[] encode_key = new byte[]{0x3F, 0x42, (byte) 0x99, 0x1F, (byte)0xAA, 0x33, 0x45, 0x22};
	public static final int encode_length = 128;

	// sdcard cache
	public static final long max_sdcard_cache_size = 1 << 30; // 1GB
	public static final int max_sdcard_files_count = 10000; // 1����ļ�
	public static final long clearto_sdcard_cache_size = 1 << 26; // 64MB
	public static final int clearto_sdcard_files_count = 2000; // 2000���ļ�

	// �ٶ�API key
	public static final String baidu_api_key = "8F9FD5cglWj0vWtngpXNepjW";
	
	/*
	 * ����Ϊ����ʱ�ɱ����
	 */

	// �汾��Ϣ
	public static int version_code = 100;
	public static String version_name = "1.0";
	
	// ���ճ�����ͼƬ
	public static Bitmap work_photo;

	// ��Ļ���
	public static int screen_width;
	public static float density;

	// ����ߴ����
	public static int emotion_size = 30;
	
	// �ⲿ�洢Ŀ¼
	public static String app_data_dir = null;

	// �ļ�����Ŀ¼
	public static String cache_dir = null;

	// �˻�����Ŀ¼
	public static String account_dir = null;

	// cookie store
	public static PersistentCookieStore cookie = null;

	// �������ü�¼
	public static AppSettings settings = null;
	
	// ����
	public static ImageCache cache = null;
	
	// ����������activity
	public static HashMap<String, Activity> activities = null;
	// ��ǰ��activity
	public static MediatorActivity current_activity = null;
	// ��ǰproxy
	public static NetTextProxy current_proxy = null;
	
	// �û��˻�
	public static AppAccount account;
	// �û���Ϣ����
	public static int newsCount;

	// �ٶȶ˷����Ƿ��ʼ���ɹ�
	public static boolean is_frontia_init = false;

	// app�Ƿ������
	public static boolean is_start = false;
	
	// �û�ͷ�����
	public static AvatarUpdate avatars_update = null;
	
	// �û���γ��
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
        
        // ����ʹ�ò��ԣ�����ʹ��sdcard��û����ʹ���ֻ��ڴ�

        // ���������ڴ�ʹ�õĽ���ҡ��
        boolean hasInnerCache = false;
        
    	File cacheDir = activity.getCacheDir();
    	if(cacheDir.exists() && cacheDir.isDirectory() && cacheDir.canWrite())
    	{
    		File file = new File(cacheDir.getAbsolutePath() + "/account/");
    		if(file.exists() && file.isDirectory())
    			hasInnerCache = true;
    	}
    	
        // ֻ�м���sdcard�ˣ��ſ�����
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
		
		// ���Ͷ����ʼ��
		TypeDef.Initialize();
		
		Location.startGetLocation();
		is_frontia_init = Frontia.init(activity.getApplicationContext(), baidu_api_key);
		
		// ��ǰactivity
		current_activity = activity;
	}

	public static void Uninitialize()
	{
		Collection<Activity> acts = activities.values();
		
		for(Iterator<Activity> it = acts.iterator(); it.hasNext();)
			it.next().finish();
	}
	
}
