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
import com.yiyouapp.proxy.WorkCommentProxy;
import com.yiyouapp.proxy.WorkListProxy;
import com.yiyouapp.proxy.WorkListProxy.WorksList;
import com.yiyouapp.value.KeyValue;
import com.yiyouapp.value.ReqData;

import android.app.Activity;

public class WorkCommentCmd extends SimpleCommand {

	public static String GET_COMMENT_LIST = "get_comment_list";
	public static String SEND_COMMENT = "send_comment";
	
	@Override
	public void execute(INotification notification) {
		WorkCommentProxy proxy = (WorkCommentProxy)facade.retrieveProxy(WorkCommentProxy.NAME);
	
		String name = notification.getName();
		if(name.equals(GET_COMMENT_LIST))
			proxy.getCommentList((ReqData)notification.getBody());
		else if(name.equals(SEND_COMMENT))
			proxy.sendComment((ReqData)notification.getBody());
	}

}
