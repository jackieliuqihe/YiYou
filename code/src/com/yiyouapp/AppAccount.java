package com.yiyouapp;

import com.yiyouapp.utils.AccountUtil;
import com.yiyouapp.utils.SDCardUtil;
import com.yiyouapp.utils.WorkUtil;
import com.yiyouapp.value.UserInfo;

public class AppAccount {

	// �û��Ự�Ƿ��¼
	public boolean session_is_login = false;
	// �û���Ϣ
	public UserInfo info;
	
	public AppAccount()
	{
		info = new UserInfo();
	}

	// ����ڴ���Ϣ
	public void clearInfo()
	{
		info = new UserInfo();
	}
	
	// �����Ϣ������
	public boolean IsInfoValid()
	{
		return info.user_id > 0 && info.user_type != null;
	}
	
	// �ֻ����˻��Ƿ��ڵ�¼״̬
	public boolean IsLogin()
	{
		return AppConfig.settings.getMobile() != "" && AppConfig.settings.getPass() != "";
	}

	// �����˻���Ϣ
	public void saveAccount()
	{
		if(AppConfig.account_dir != null)
		{
			String json = WorkUtil.userInfoToJSON(info);
			SDCardUtil.writeFile(AccountUtil.getCurrAccountDir() + AppConfig.FILENAME_USERINFO, json.getBytes());
		}
	}
	
	// ��ȡ�˻���Ϣ
	public boolean readAccount()
	{
		byte[] bytes = SDCardUtil.readFile(AccountUtil.getCurrAccountDir() + AppConfig.FILENAME_USERINFO);
		if(bytes != null)
		{
			info = WorkUtil.jsonToUserInfo(new String(bytes));
			
			// ��Ϊ�ܶ�ȡ����Ϣ���ǶԵģ���У����Ϣ����ȷ��
			return true;
		}
		
		return false;
	}
}
