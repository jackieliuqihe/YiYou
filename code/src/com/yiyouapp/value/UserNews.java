package com.yiyouapp.value;

import java.util.Date;

public class UserNews {

	public int user_id;
	
	public String avatar64_path;

	public String sex;
	
	public String user_name;
	
	public String user_type;

	// 用户位置
	public String user_city;
	
	// 用户经纬度
	public double latitude;
	public double longitude;

	public double distance;
	public boolean has_location;
	
	public long user_news_id;
	
	public int elapsed_secs;
	public boolean is_view;
	
	public int extra_data;
	
	public int news_type;
	
	// 消息内容
	public String news;
}
