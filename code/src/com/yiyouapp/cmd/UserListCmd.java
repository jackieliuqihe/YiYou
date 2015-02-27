package com.yiyouapp.cmd;

import org.puremvc.java.interfaces.INotification;
import org.puremvc.java.patterns.command.SimpleCommand;

import com.loopj.android.http.RequestParams;
import com.yiyouapp.AppConfig;
import com.yiyouapp.AppFacade;
import com.yiyouapp.proxy.LoginProxy;
import com.yiyouapp.proxy.RegProxy;
import com.yiyouapp.proxy.UploadWorkProxy;
import com.yiyouapp.proxy.UserListProxy;
import com.yiyouapp.proxy.WorkListProxy;
import com.yiyouapp.proxy.WorkListProxy.WorksList;
import com.yiyouapp.value.KeyValue;
import com.yiyouapp.value.ReqData;

import android.app.Activity;

public class UserListCmd extends SimpleCommand {

	public static String GET_USER_LIST = "get_user_list";
	public static String GET_NEAR_USERS = "get_near_users";
	
	@Override
	public void execute(INotification notification) {
		UserListProxy proxy = (UserListProxy)facade.retrieveProxy(UserListProxy.NAME);
	
		String name = notification.getName();
		if(name.equals(GET_USER_LIST))
			proxy.getUserList((ReqData)notification.getBody());
		else if(name.equals(GET_NEAR_USERS))
			proxy.getNearUsers((ReqData)notification.getBody());
	}

}
