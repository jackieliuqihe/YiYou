package com.yiyouapp.cmd;

import org.puremvc.java.interfaces.INotification;
import org.puremvc.java.patterns.command.SimpleCommand;

import com.loopj.android.http.RequestParams;
import com.yiyouapp.AppConfig;
import com.yiyouapp.AppFacade;
import com.yiyouapp.proxy.LoginProxy;
import com.yiyouapp.proxy.UserNewsProxy;
import com.yiyouapp.proxy.RegProxy;
import com.yiyouapp.proxy.UploadWorkProxy;
import com.yiyouapp.proxy.UserListProxy;
import com.yiyouapp.proxy.WorkListProxy;
import com.yiyouapp.proxy.WorkListProxy.WorksList;
import com.yiyouapp.value.KeyValue;
import com.yiyouapp.value.ReqData;

import android.app.Activity;

public class UserNewsCmd extends SimpleCommand {

	public static String GET_NEWS_LIST = "get_news_list";

	public static String GET_UNVIEW_NEWS_COUNT = "get_unview_news_count";

	public static String SET_NEWS_VIEWED = "set_news_viewed";
	
	@Override
	public void execute(INotification notification) {
		String name = notification.getName();
		UserNewsProxy proxy = (UserNewsProxy)facade.retrieveProxy(UserNewsProxy.NAME);
	
		if(name.equals(GET_NEWS_LIST))
			proxy.getNewsList((ReqData)notification.getBody());
		else if(name.equals(GET_UNVIEW_NEWS_COUNT))
			proxy.getUnviewNewsCount((ReqData)notification.getBody());
		else if(name.equals(SET_NEWS_VIEWED))
			proxy.setNewsViewed((ReqData)notification.getBody());
	}

}
