package com.yiyouapp.cmd;

import java.util.ArrayList;

import org.puremvc.java.interfaces.INotification;
import org.puremvc.java.patterns.command.SimpleCommand;

import com.loopj.android.http.RequestParams;
import com.yiyouapp.AppConfig;
import com.yiyouapp.AppFacade;
import com.yiyouapp.proxy.LoginProxy;
import com.yiyouapp.proxy.NetImageProxy;
import com.yiyouapp.proxy.RegProxy;
import com.yiyouapp.proxy.UploadWorkProxy;
import com.yiyouapp.proxy.WorkInfoProxy;
import com.yiyouapp.proxy.WorkListProxy;
import com.yiyouapp.proxy.WorkListProxy.WorksList;
import com.yiyouapp.value.KeyValue;
import com.yiyouapp.value.ReqData;

import android.app.Activity;

public class GetImageCmd extends SimpleCommand {

	public static String GET_IMAGE = "get_image";
	
	@Override
	public void execute(INotification notification) {
		NetImageProxy proxy = (NetImageProxy)facade.retrieveProxy(NetImageProxy.NAME);
	
		proxy.batchGet((ArrayList<ReqData>)notification.getBody());
	}

}
