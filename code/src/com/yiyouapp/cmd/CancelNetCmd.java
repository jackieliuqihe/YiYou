package com.yiyouapp.cmd;

import org.puremvc.java.interfaces.INotification;
import org.puremvc.java.patterns.command.SimpleCommand;

import com.yiyouapp.AppConfig;
import com.yiyouapp.AppFacade;
import com.yiyouapp.proxy.LoginProxy;
import com.yiyouapp.proxy.RegProxy;
import com.yiyouapp.value.KeyValue;

import android.app.Activity;

public class CancelNetCmd extends SimpleCommand {

	public static String CANCEL_NET = "cancel_net";
	
	@Override
	public void execute(INotification notification) {
		if(AppConfig.current_proxy != null)
			AppConfig.current_proxy.cancelRequest();
	}

}
