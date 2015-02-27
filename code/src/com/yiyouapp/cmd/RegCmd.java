package com.yiyouapp.cmd;

import org.puremvc.java.interfaces.INotification;
import org.puremvc.java.patterns.command.SimpleCommand;

import com.yiyouapp.AppConfig;
import com.yiyouapp.AppFacade;
import com.yiyouapp.proxy.RegProxy;
import com.yiyouapp.value.KeyValue;
import com.yiyouapp.value.ReqData;

import android.app.Activity;

public class RegCmd extends SimpleCommand {

	public static String REGISTER = "register";
	
	@Override
	public void execute(INotification notification) {

		RegProxy proxy = (RegProxy)AppFacade.getInstance().retrieveProxy(RegProxy.NAME);
		String type = notification.getType();
		
		proxy.setData(type);
		
		if(type.equals("1")){
			proxy.getCode((ReqData)notification.getBody());
		}
		else if(type.equals("2")){
			proxy.verifyCode((ReqData)notification.getBody());
		}
		else if(type.equals("21")){
			proxy.getCode((ReqData)notification.getBody());
		}
		else if(type.equals("4")){
			proxy.register((ReqData)notification.getBody());
		}
	}

}
