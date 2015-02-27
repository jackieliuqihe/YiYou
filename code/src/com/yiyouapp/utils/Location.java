package com.yiyouapp.utils;

import android.util.Log;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.yiyouapp.AppConfig;
import com.yiyouapp.YiYouApp;

public class Location {

	public static class LocationListener implements BDLocationListener {
		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location == null)
		            return;

			String address = "";
			
			if (location.getLocType() == BDLocation.TypeGpsLocation){
				address = location.getAddrStr();
			} else if (location.getLocType() == BDLocation.TypeNetWorkLocation){
				address = location.getAddrStr();
			} 

			AppConfig.latitude = location.getLatitude();
			AppConfig.longitude = location.getLongitude();

			AppConfig.address = StringUtil.getString(address);
			if(StringUtil.isStringEmpty(AppConfig.address))
				AppConfig.address = "";

			String city = StringUtil.getString(location.getCity());
			String district = StringUtil.getString(location.getDistrict());
			if(!StringUtil.isStringEmpty(city))
			{
				if(StringUtil.isStringEmpty(district))
					AppConfig.city = city;
				else
					AppConfig.city = city + "·" + district;
			}
			else
			{
				AppConfig.city = "";
			}
			
			AppConfig.settings.setLatitude((float)AppConfig.latitude);
			AppConfig.settings.setLongitude((float)AppConfig.longitude);
			AppConfig.settings.setAddress(AppConfig.address);
			AppConfig.settings.setCity(AppConfig.city);

			client_.stop();
		}
	}
	
	public static LocationClient client_ = null;
	public static LocationListener listener_ = new LocationListener();
	
	public static void startGetLocation()
	{
		client_ = new LocationClient(YiYouApp.getInstance());
	    client_.registerLocationListener(listener_);
	    
	    LocationClientOption option = new LocationClientOption();
	    option.setLocationMode(LocationMode.Hight_Accuracy); //设置定位模式
	    option.setCoorType("bd09ll"); //返回的定位结果是百度经纬度,默认值gcj02
	    option.setIsNeedAddress(true); //返回的定位结果包含地址信息
	    client_.setLocOption(option);
	    
	    client_.start();
	    
	    if (client_ != null && client_.isStarted())
	    	client_.requestLocation();
	    else 
	    	Log.d("LocSDK4", "locClient is null or not started");
	}
	
	private static final double EARTH_RADIUS = 6378.137; //地球半径
	
	private static double rad(double d)
	{
	   return d * Math.PI / 180.0;
	}
	
	public static double getDistance(double lat1, double lng1, double lat2, double lng2)
	{
	   double radLat1 = rad(lat1);
	   double radLat2 = rad(lat2);
	   
	   double a = radLat1 - radLat2;
	   double b = rad(lng1) - rad(lng2);

	   double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a/2),2) +
	    Math.cos(radLat1)*Math.cos(radLat2)*Math.pow(Math.sin(b/2),2)));
	   
	   s = s * EARTH_RADIUS;
	   s = (double)Math.round(s * 100) / (double)100;
	   
	   return s;
	}
}
