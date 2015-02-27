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
import com.yiyouapp.proxy.UserMsgProxy;
import com.yiyouapp.proxy.WorkListProxy;
import com.yiyouapp.proxy.WorkListProxy.WorksList;
import com.yiyouapp.value.KeyValue;
import com.yiyouapp.value.ReqData;

import android.app.Activity;

public class UserMsgCmd extends SimpleCommand {

	public static String GET_USER_MSG_LIST = "get_user_msg_list";
	public static String SEND_USER_MSG = "send_user_msg";
	
	@Override
	public void execute(INotification notification) {
		UserMsgProxy proxy = (UserMsgProxy)facade.retrieveProxy(UserMsgProxy.NAME);
	
		String name = notification.getName();
		if(name.equals(GET_USER_MSG_LIST))
			proxy.getMsgList((ReqData)notification.getBody());
		else if(name.equals(SEND_USER_MSG))
			proxy.sendMsg((ReqData)notification.getBody());
	}

}
