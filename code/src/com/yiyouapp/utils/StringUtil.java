package com.yiyouapp.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

import com.yiyouapp.AppConfig;

import android.graphics.Bitmap;
import android.graphics.Matrix;

public class StringUtil {

	public static String getURL(String logic, String action)
	{
		return AppConfig.BASE_URL + "/logic/" + logic + ".php?a=" + action;
	}

	public static String getFileUrl(String url)
	{
		return AppConfig.FILE_BASE_URL + url;
	}

	public static String getFileNameFromPath(String path)
	{
		int start = path.lastIndexOf("/");
	    int end = path.length();
	    
	    if (start!=-1 && end!=-1)
	        return path.substring(start + 1, end);
	    else
	        return path;
	}

	public static String formatCity(String city)
	{
		if(isStringEmpty(city))
			return city;
		
		return city.replace(",", "¡¤");
	}

	public static String formatDate(String datetime)
	{
		if(isStringEmpty(datetime))
			return datetime;
		
		String arr[] = datetime.split(" ");
		if(arr.length > 0)
			return arr[0];
		else
			return "";
	}

	public static long parseDate(String date)
	{
        SimpleDateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long value = 0;
        
        try
        {
        	Date d = formatDate.parse(date);

        	value = d.getTime();
        }
        catch(Exception e)
        {
        }
        
        return value;
	}

	public static String formatDistance(double distance)
	{
		return String.valueOf(distance) + " km";
	}
	
	public static String getBriefDateString(String date) 
	{
        SimpleDateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String str = "";
        
        try
        {
        	Date d = formatDate.parse(date);
        	Date now = new Date(System.currentTimeMillis());
        	
        	if(d.getYear() == now.getYear())
        	{
	        	if(d.getMonth() == now.getMonth() &&
	        	   d.getDate() == now.getDate())
	        		str = formatIntLength2(d.getHours()) + ":" + formatIntLength2(d.getMinutes());
	        	else
	        		str = formatIntLength2(d.getMonth() + 1) + "-" + formatIntLength2(d.getDate());
        	}
        	else
        	{
        		str = d.getYear() + "-" + formatIntLength2(d.getMonth() + 1);
        	}
        }
        catch(Exception e)
        {
        }
        
        return str;
    }
	
	public static String formatIntLength2(int value)
	{
		String v = String.valueOf(value);
		if(v.length() < 2)
			return "0" + value;
		else
			return v;
	}
	
	public static boolean isMobile(String value)
	{
		if(value.length() != 11)
			return false;
		
		return Pattern.matches("^[1][0-9]{10}$", value);
	}

	public static boolean isVerifyCode(String value)
	{
		if(value.length() != 6)
			return false;
		
		return Pattern.matches("^[0-9]{6}$", value);
	}

	public static boolean isName(String value)
	{
		String trimValue = value.trim();
		if(trimValue.length() < 2 || trimValue.length() > 20)
			return false;
		
		return true;//Pattern.matches("^([a-zA-Z0-9\u4e00-\u9fa5]){2,20}$", value);
	}

	public static boolean isPass(String value)
	{
		if(value.length() < 6 || value.length() > 20)
			return false;
		
		return true;
	}
	
	public static boolean isStringEmpty(String value)
	{
		return value == null || value.length() <= 0;
	}
	
	public static String getString(String value)
	{
		if(value == null || value.equals("null"))
			return "";
		
		return value;
	}
	
	// È¥µôUTF-8 BOM
	public static String removeBOM(String value)
	{
		if(value != null && value.startsWith("\ufeff"))
		{
			value =  value.substring(1);
		}
		
		return value;
	}
}
