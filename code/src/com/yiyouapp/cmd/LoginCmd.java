package com.yiyouapp.cmd;

import org.puremvc.java.interfaces.INotification;
import org.puremvc.java.patterns.command.SimpleCommand;

import com.yiyouapp.AppConfig;
import com.yiyouapp.AppFacade;
import com.yiyouapp.proxy.LoginProxy;
import com.yiyouapp.proxy.LogoutProxy;
import com.yiyouapp.proxy.RegProxy;
import com.yiyouapp.value.KeyValue;
import com.yiyouapp.value.ReqData;

import android.app.Activity;

public class LoginCmd extends SimpleCommand {

	public static String LOGIN = "login";
	public static String LOGOUT = "logout";
	
	@Override
	public void execute(INotification notification) {
		
		String name = notification.getName();
		if(name.equals(LOGIN))
		{
			LoginProxy proxy = (LoginProxy)AppFacade.getInstance().retrieveProxy(LoginProxy.NAME);
			proxy.login((ReqData)notification.getBody());
		}
		else if(name.equals(LOGOUT))
		{
			LogoutProxy proxy = (LogoutProxy)facade.retrieveProxy(LogoutProxy.NAME);
			proxy.logout((ReqData)notification.getBody());
		}
	}

}
