package com.yiyouapp;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class AppSettings {

	private static final String SETTINGS_NAME = "settings";
	
	private static final String UPDATE_DATE = "update_date";
	
	private static final String SPLASH_TIME = "splash_time";

	private static final String USER_TYPE = "user_type";

	// 注册时输入的手机号
	private static final String REG_MOBILE = "reg_mobile";
	
	private static final String MOBILE = "mobile";
	private static final String PASSWORD = "password";

	private static final String LONGITUDE = "longitude";
	private static final String LATITUDE = "latitude";
	
	private static final String ADDRESS = "address";
	private static final String CITY = "city";

	private static final String DEVICE_USERID = "device_uid";
	private static final String DEVICE_CHANNELID = "device_cid";

	private static final String DEVICE_BIND = "device_bind";
	
	private SharedPreferences settings_;
	
	public AppSettings(Context context)
	{
		settings_ = context.getSharedPreferences(SETTINGS_NAME, 0); 
	}
	
	public void setUpdateDate()
	{
		Date currentTime = new Date(System.currentTimeMillis());
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String dateString = formatter.format(currentTime);
		
		settings_.edit().putString(UPDATE_DATE, dateString).commit();
	}
	
	public Date getUpdateDate()
	{
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		ParsePosition pos = new ParsePosition(0);
		Date date = formatter.parse(settings_.getString(UPDATE_DATE, "2014-01-01"), pos);
		
		return date;
	}
	
	public void setSplashTime(int time)
	{
		settings_.edit().putInt(SPLASH_TIME, time).commit();
	}
	
	public int getSplashTime()
	{
		return settings_.getInt(SPLASH_TIME, 0);
	}

	public void setUserType(String usertype)
	{
		settings_.edit().putString(USER_TYPE, usertype).commit();
	}
	
	public String getUserType()
	{
		return settings_.getString(USER_TYPE, "");
	}
	
	public void setRegMobile(String mobile)
	{
		settings_.edit().putString(REG_MOBILE, mobile).commit();
	}
	
	public String getRegMobile()
	{
		return settings_.getString(REG_MOBILE, "");
	}
	
	public void setMobile(String mobile)
	{
		settings_.edit().putString(MOBILE, mobile).commit();
	}
	
	public String getMobile()
	{
		return settings_.getString(MOBILE, "");
	}
	
	public void setPass(String password)
	{
		settings_.edit().putString(PASSWORD, password).commit();
	}
	
	public String getPass()
	{
		return settings_.getString(PASSWORD, "");
	}
	
	// 经度
	public void setLongitude(float longitude)
	{
		settings_.edit().putFloat(LONGITUDE, longitude).commit();
	}
	
	public float getLongitude()
	{
		return settings_.getFloat(LONGITUDE, 0.0f);
	}
	
	// 纬度
	public void setLatitude(float latitude)
	{
		settings_.edit().putFloat(LATITUDE, latitude).commit();
	}
	
	public float getLatitude()
	{
		return settings_.getFloat(LATITUDE, 0.0f);
	}

	// address
	public void setAddress(String address)
	{
		settings_.edit().putString(ADDRESS, address).commit();
	}
	
	public String getAddress()
	{
		return settings_.getString(ADDRESS, "");
	}

	// city
	public void setCity(String city)
	{
		settings_.edit().putString(CITY, city).commit();
	}
	
	public String getCity()
	{
		return settings_.getString(CITY, "");
	}

	// device user id
	public void setDeviceUserID(String uid)
	{
		settings_.edit().putString(DEVICE_USERID, uid).commit();
	}
	
	public String getDeviceUserID()
	{
		return settings_.getString(DEVICE_USERID, "");
	}

	// device channel id
	public void setDeviceChannelID(String cid)
	{
		settings_.edit().putString(DEVICE_CHANNELID, cid).commit();
	}
	
	public String getDeviceChannelID()
	{
		return settings_.getString(DEVICE_CHANNELID, "");
	}
	
	// device 绑定
	public void setDeviceBind(boolean bind)
	{
		settings_.edit().putBoolean(DEVICE_BIND, bind).commit();
	}
	
	public boolean getDeviceBind()
	{
		return settings_.getBoolean(DEVICE_BIND, false);
	}
	
}
