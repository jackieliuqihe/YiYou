package com.yiyouapp;

import com.yiyouapp.utils.AccountUtil;
import com.yiyouapp.utils.SDCardUtil;
import com.yiyouapp.utils.WorkUtil;
import com.yiyouapp.value.UserInfo;

public class AppAccount {

	// 用户会话是否登录
	public boolean session_is_login = false;
	// 用户信息
	public UserInfo info;
	
	public AppAccount()
	{
		info = new UserInfo();
	}

	// 清楚内存信息
	public void clearInfo()
	{
		info = new UserInfo();
	}
	
	// 检查信息完整性
	public boolean IsInfoValid()
	{
		return info.user_id > 0 && info.user_type != null;
	}
	
	// 手机端账户是否在登录状态
	public boolean IsLogin()
	{
		return AppConfig.settings.getMobile() != "" && AppConfig.settings.getPass() != "";
	}

	// 保存账户信息
	public void saveAccount()
	{
		if(AppConfig.account_dir != null)
		{
			String json = WorkUtil.userInfoToJSON(info);
			SDCardUtil.writeFile(AccountUtil.getCurrAccountDir() + AppConfig.FILENAME_USERINFO, json.getBytes());
		}
	}
	
	// 读取账户信息
	public boolean readAccount()
	{
		byte[] bytes = SDCardUtil.readFile(AccountUtil.getCurrAccountDir() + AppConfig.FILENAME_USERINFO);
		if(bytes != null)
		{
			info = WorkUtil.jsonToUserInfo(new String(bytes));
			
			// 认为能读取出信息就是对的，不校验信息的正确性
			return true;
		}
		
		return false;
	}
}
