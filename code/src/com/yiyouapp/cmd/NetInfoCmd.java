package com.yiyouapp.cmd;

import org.puremvc.java.interfaces.INotification;
import org.puremvc.java.patterns.command.SimpleCommand;

import com.yiyouapp.AppConfig;
import com.yiyouapp.controls.CustomAlertDialog;
import com.yiyouapp.proxy.NetTextProxy;
import com.yiyouapp.proxy.ReqDataProxy;
import com.yiyouapp.utils.DialogUtil;
import com.yiyouapp.value.ReqData;

import android.app.Activity;
import android.content.DialogInterface;
import android.widget.Toast;

public class NetInfoCmd extends SimpleCommand {

	public static String NET_INFO = "net_info";
	
	@Override
	public void execute(INotification notification) {

		ReqData req = (ReqData)notification.getBody();
		if(req == null)
			return;
		
		final ReqDataProxy proxy = (ReqDataProxy)req.receiver;
		
		if(notification.getName().equals(NET_INFO))
		{
			if(AppConfig.current_activity != null)
			{
				AppConfig.current_activity.hideProgress();

				switch(req.type)
				{
				case Retry:
					new CustomAlertDialog.Builder(AppConfig.current_activity)
					.setMessage(proxy.getInfo())
					.setPositiveButton("È·¶¨" , null)  
					.show();
					break;
				case Notify:
					Toast.makeText(AppConfig.current_activity, proxy.getInfo(), Toast.LENGTH_SHORT).show();
					break;
				case Quite:
					break;
				}
			}
		}
	}

}
