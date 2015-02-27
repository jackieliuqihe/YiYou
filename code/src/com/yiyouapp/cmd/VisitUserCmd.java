package com.yiyouapp.cmd;

import org.puremvc.java.interfaces.INotification;
import org.puremvc.java.patterns.command.SimpleCommand;

import com.yiyouapp.AppConfig;
import com.yiyouapp.AppFacade;
import com.yiyouapp.proxy.LoginProxy;
import com.yiyouapp.proxy.RegProxy;
import com.yiyouapp.proxy.VisitUserProxy;
import com.yiyouapp.utils.SDCardUtil;
import com.yiyouapp.utils.StringUtil;
import com.yiyouapp.utils.WorkUtil;
import com.yiyouapp.value.KeyValue;
import com.yiyouapp.value.ReqData;

import android.app.Activity;

public class VisitUserCmd extends SimpleCommand {

	public static String VISIT_USER_CENTER = "visit_user_center";
	public static String GET_USER_SCORE = "get_user_score";

	public static String SET_USER_ATTENTION = "set_user_attention";
	
	@Override
	public void execute(INotification notification) {
		
		// 查看用户
		String name = notification.getName();
		
		VisitUserProxy proxy = (VisitUserProxy)facade.retrieveProxy(VisitUserProxy.NAME);
		
		if(name.equals(VISIT_USER_CENTER))
			proxy.visitUserCenter((ReqData)notification.getBody());
		else if(name.equals(GET_USER_SCORE))
			proxy.getUserScore((ReqData)notification.getBody());
		else if(name.equals(SET_USER_ATTENTION))
			proxy.setAttention((ReqData)notification.getBody());
	}

}
