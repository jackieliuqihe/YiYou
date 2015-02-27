package com.yiyouapp.value;

import java.util.HashMap;

public class AvatarUpdate {

	public class AvatarTime
	{
		public String avatarUptime;
		public boolean needUpdate;
	}
	
	public HashMap<String, AvatarTime> avatars;
	
	public AvatarUpdate()
	{
		avatars = new HashMap<String, AvatarTime>();
	}

	public void setUptime(String key, String uptime)
	{
		if(avatars.containsKey(key))
		{
			AvatarTime at = avatars.get(key);
			if(at.needUpdate)
				return;
			
			if(!at.avatarUptime.equals(uptime))
			{
				at.needUpdate = true;
				at.avatarUptime = uptime;
			}
		}
		else
		{
			AvatarTime at = new AvatarTime();
			at.needUpdate = true;
			at.avatarUptime = uptime;
			
			avatars.put(key, at);
		}
	}

	public void setUpdate(String key)
	{
		if(avatars.containsKey(key))
		{
			AvatarTime at = avatars.get(key);
			at.needUpdate = false;
		}
	}
	
	public boolean getUpdate(String key)
	{
		boolean needUpdate = false;
		
		if(avatars.containsKey(key))
		{
			needUpdate = avatars.get(key).needUpdate;
		}
		
		return needUpdate;
	}
}
