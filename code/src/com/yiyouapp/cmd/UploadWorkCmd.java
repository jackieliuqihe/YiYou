package com.yiyouapp.cmd;

import org.puremvc.java.interfaces.INotification;
import org.puremvc.java.patterns.command.SimpleCommand;

import com.loopj.android.http.RequestParams;
import com.yiyouapp.AppConfig;
import com.yiyouapp.AppFacade;
import com.yiyouapp.proxy.LoginProxy;
import com.yiyouapp.proxy.RegProxy;
import com.yiyouapp.proxy.UploadWorkProxy;
import com.yiyouapp.value.KeyValue;
import com.yiyouapp.value.ReqData;

import android.app.Activity;

public class UploadWorkCmd extends SimpleCommand {

	public static String UPLOAD_WORK = "upload_work";
	
	@Override
	public void execute(INotification notification) {
		UploadWorkProxy proxy = (UploadWorkProxy)AppFacade.getInstance().retrieveProxy(UploadWorkProxy.NAME);
	
		proxy.uploadWork((ReqData)notification.getBody());
	}

}
