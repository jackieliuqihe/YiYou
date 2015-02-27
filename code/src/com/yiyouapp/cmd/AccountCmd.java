package com.yiyouapp.cmd;

import org.puremvc.java.interfaces.INotification;
import org.puremvc.java.patterns.command.SimpleCommand;

import com.yiyouapp.AppConfig;
import com.yiyouapp.AppFacade;
import com.yiyouapp.proxy.AccountProxy;
import com.yiyouapp.proxy.LoginProxy;
import com.yiyouapp.proxy.RegProxy;
import com.yiyouapp.utils.AccountUtil;
import com.yiyouapp.utils.DialogUtil;
import com.yiyouapp.utils.SDCardUtil;
import com.yiyouapp.utils.StringUtil;
import com.yiyouapp.utils.WorkUtil;
import com.yiyouapp.value.KeyValue;
import com.yiyouapp.value.ReqData;

import android.app.Activity;
import android.util.Log;

public class AccountCmd extends SimpleCommand {

	// ��ȡ�˻�������Ϣ������
	public static String GET_ACCOUNT_INFO = "get_account_info";
	// �����˻�������Ϣ������
	public static String SET_ACCOUNT_INFO = "set_account_info";

	// �����豸������Ϣ������
	public static String SET_DEVICE_INFO = "set_device_info";
	
	@Override
	public void execute(INotification notification) {
		
		String name = notification.getName();
		AccountProxy proxy = (AccountProxy)facade.retrieveProxy(AccountProxy.NAME);
		
		// ��ȡ�û���Ϣ
		if(name.equals(GET_ACCOUNT_INFO))
		{
			if(!AccountUtil.makeCurrAccountDir())
			{
				Log.e(AppConfig.APP_NAME, "cannot create account dir");
				DialogUtil.showAppErrorDialog("�ܱ�Ǹ�������豸�洢�����ã�Ӧ�ó������ؼ�������Ҫ�˳���");
				return;
			}

			// ����û���Ϣ��������
			boolean accountInfoIntegrity = false;
			
			// �ӱ��ػ�ȡ�˻�������Ϣʧ�ܣ��ʹ������ȡ
			if(AppConfig.account.readAccount())
			{
				accountInfoIntegrity = AppConfig.account.IsInfoValid();
			}
			
			if(!accountInfoIntegrity)
				proxy.getAccountInfo((ReqData)notification.getBody());
			else
				proxy.doGetAvatar((ReqData)notification.getBody());
		}
		else if(name.equals(SET_ACCOUNT_INFO))
		{
			proxy.setAccountInfo((ReqData)notification.getBody());
		}
		else if(name.equals(SET_DEVICE_INFO))
		{
			if(!StringUtil.isStringEmpty(AppConfig.settings.getDeviceUserID()))
				proxy.setDeviceInfo();
		}
	}

}
