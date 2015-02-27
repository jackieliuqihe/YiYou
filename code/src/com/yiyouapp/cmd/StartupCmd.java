package com.yiyouapp.cmd;

import org.puremvc.java.interfaces.INotification;
import org.puremvc.java.patterns.command.SimpleCommand;

import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.yiyouapp.AppConfig;
import com.yiyouapp.controls.Updater;
import com.yiyouapp.proxy.AccountProxy;
import com.yiyouapp.proxy.LoginProxy;
import com.yiyouapp.proxy.NetImageProxy;
import com.yiyouapp.proxy.UserNewsProxy;
import com.yiyouapp.proxy.RegProxy;
import com.yiyouapp.proxy.UploadWorkProxy;
import com.yiyouapp.proxy.UserMsgProxy;
import com.yiyouapp.proxy.VisitUserProxy;
import com.yiyouapp.proxy.UserListProxy;
import com.yiyouapp.proxy.WorkCommentProxy;
import com.yiyouapp.proxy.WorkInfoProxy;
import com.yiyouapp.proxy.WorkListProxy;
import com.yiyouapp.proxy.WorkProxy;
import com.yiyouapp.utils.SDCardUtil;
import com.yiyouapp.utils.WorkUtil;
import com.yiyouapp.value.ReqData;

import android.app.Activity;
import android.content.Context;

public class StartupCmd extends SimpleCommand {

	public static String STARTUP = "startup";
	
	@Override
	public void execute(INotification notification) {

		facade.registerCommand(NetErrorCmd.NET_ERROR, new NetErrorCmd());
		facade.registerCommand(NetInfoCmd.NET_INFO, new NetInfoCmd());
		
		facade.registerCommand(CancelNetCmd.CANCEL_NET, new CancelNetCmd());
		
		facade.registerCommand(LoginCmd.LOGIN, new LoginCmd());
		facade.registerCommand(RegCmd.REGISTER, new RegCmd());
		
		facade.registerCommand(UploadWorkCmd.UPLOAD_WORK, new UploadWorkCmd());
		facade.registerCommand(WorkListCmd.GET_WORKS_LIST, new WorkListCmd());
		
		facade.registerCommand(WorkInfoCmd.WORK_INFO_CHANGE, new WorkInfoCmd());
		facade.registerCommand(GetImageCmd.GET_IMAGE, new GetImageCmd());

		facade.registerCommand(VisitUserCmd.VISIT_USER_CENTER, new VisitUserCmd());
		facade.registerCommand(VisitUserCmd.GET_USER_SCORE, new VisitUserCmd());
		facade.registerCommand(VisitUserCmd.SET_USER_ATTENTION, new VisitUserCmd());

		facade.registerCommand(AccountCmd.GET_ACCOUNT_INFO, new AccountCmd());
		facade.registerCommand(AccountCmd.SET_ACCOUNT_INFO, new AccountCmd());
		facade.registerCommand(AccountCmd.SET_DEVICE_INFO, new AccountCmd());

		facade.registerCommand(UserListCmd.GET_USER_LIST, new UserListCmd());
		facade.registerCommand(UserListCmd.GET_NEAR_USERS, new UserListCmd());

		facade.registerCommand(UserNewsCmd.GET_NEWS_LIST, new UserNewsCmd());
		facade.registerCommand(UserNewsCmd.GET_UNVIEW_NEWS_COUNT, new UserNewsCmd());
		facade.registerCommand(UserNewsCmd.SET_NEWS_VIEWED, new UserNewsCmd());

		facade.registerCommand(UserMsgCmd.GET_USER_MSG_LIST, new UserMsgCmd());
		facade.registerCommand(UserMsgCmd.SEND_USER_MSG, new UserMsgCmd());

		facade.registerCommand(WorkCommentCmd.GET_COMMENT_LIST, new WorkCommentCmd());
		facade.registerCommand(WorkCommentCmd.SEND_COMMENT, new WorkCommentCmd());
		
		facade.registerCommand(WorkCmd.GET_WORK, new WorkCmd());
		
		facade.registerProxy(new LoginProxy());
		facade.registerProxy(new RegProxy());
		
		facade.registerProxy(new UploadWorkProxy());
		facade.registerProxy(new WorkListProxy());

		facade.registerProxy(new WorkInfoProxy());
		facade.registerProxy(new NetImageProxy());

		facade.registerProxy(new AccountProxy());
		facade.registerProxy(new VisitUserProxy());

		facade.registerProxy(new UserListProxy());
		facade.registerProxy(new UserNewsProxy());

		facade.registerProxy(new UserMsgProxy());
		facade.registerProxy(new WorkCommentProxy());
		
		facade.registerProxy(new WorkProxy());
		
	}

}
