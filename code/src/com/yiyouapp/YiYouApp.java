package com.yiyouapp;

import com.baidu.frontia.FrontiaApplication;

import android.app.Application;
import android.content.Intent;
import android.content.res.Configuration;
import android.util.Log;

public class YiYouApp extends FrontiaApplication {

	public static YiYouApp instance_;
	
    @Override
    public void onCreate()
    {
        super.onCreate();
        instance_ = this;
    }
    
    public static YiYouApp getInstance()
    {
    	return instance_;
    }

}
