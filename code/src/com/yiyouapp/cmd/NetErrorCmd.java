package com.yiyouapp.cmd;

import org.puremvc.java.interfaces.INotification;
import org.puremvc.java.patterns.command.SimpleCommand;

import com.yiyouapp.AppConfig;
import com.yiyouapp.controls.CustomAlertDialog;
import com.yiyouapp.proxy.IRetry;
import com.yiyouapp.proxy.NetTextProxy;
import com.yiyouapp.proxy.ReqDataProxy;
import com.yiyouapp.utils.DialogUtil;
import com.yiyouapp.value.ReqData;

import android.app.Activity;
import android.content.DialogInterface;
import android.widget.Toast;

public class NetErrorCmd extends SimpleCommand {

	public static String NET_ERROR = "net_error";
	
	@Override
	public void execute(INotification notification) {

		ReqData req = (ReqData)notification.getBody();
		if(req == null)
			return;
		
		IRetry retry = null;
		
		if(req.receiver instanceof IRetry)
			retry = (IRetry)req.receiver;
		
		if(notification.getName().equals(NET_ERROR))
		{
			if(AppConfig.current_activity != null)
			{
				AppConfig.current_activity.hideProgress();
				
				switch(req.type)
				{
				case Retry:
					DialogUtil.showNetErrorDialog(retry);
					break;
				case Notify:
					Toast.makeText(AppConfig.current_activity, "很抱歉，当前您的网络请求超时", Toast.LENGTH_SHORT).show();
					break;
				case Quite:
					break;
				}
			}
		}
	}

}
