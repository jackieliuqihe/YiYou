package com.yiyouapp.cmd;

import org.puremvc.java.interfaces.INotification;
import org.puremvc.java.patterns.command.SimpleCommand;

import com.loopj.android.http.RequestParams;
import com.yiyouapp.AppConfig;
import com.yiyouapp.AppFacade;
import com.yiyouapp.proxy.LoginProxy;
import com.yiyouapp.proxy.RegProxy;
import com.yiyouapp.proxy.UploadWorkProxy;
import com.yiyouapp.proxy.WorkInfoProxy;
import com.yiyouapp.proxy.WorkListProxy;
import com.yiyouapp.proxy.WorkListProxy.WorksList;
import com.yiyouapp.proxy.WorkProxy;
import com.yiyouapp.value.KeyValue;
import com.yiyouapp.value.ReqData;

import android.app.Activity;

public class WorkCmd extends SimpleCommand {

	public static String GET_WORK = "get_work";
	
	@Override
	public void execute(INotification notification) {
		WorkProxy proxy = (WorkProxy)AppFacade.getInstance().retrieveProxy(WorkProxy.NAME);
	
		proxy.getWork((ReqData)notification.getBody());
	}

}
