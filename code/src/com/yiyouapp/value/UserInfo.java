package com.yiyouapp.value;

import java.util.ArrayList;
import java.util.Date;

public class UserInfo {
	
	public static class UserLatestWorkThumb
	{
		public String thumb_path;
		public float thumb_ratio;
	}
	
	public int user_id;

	public String avatar_path;
	
	public String avatar64_path;

	public String sex;
	
	public String user_name;
	
	public String user_type;

	public int score;
	
	// 注册时间
	public String reg_time;
	
	// 用户位置
	public String user_city;
	
	// 个性签名
	public String user_desc;

	// 培训城市
	public String train_city;

	// 联系电话
	public String contacts;
	
	// 就读学校
	public String school;

	// 用户经纬度
	public double latitude;
	public double longitude;

	public double distance;
	public boolean has_location;
	
	// 估计距离，服务器端计算
	public String est_dist;
	
	// 用户是否关注
	public boolean attention;
	
	// 作品数量
	public int work_count;
	
	// 本人的作品
	public ArrayList<UserLatestWorkThumb> thumbs;
	// 推荐给本人的画室或学生
	public ArrayList<UserInfo> recom;
	// 我的消息
	public ArrayList<UserNews> msgs;
	
	public UserInfo clone()
	{
		UserInfo info = new UserInfo();

		info.user_id = user_id;

		info.avatar_path = avatar_path;
		info.avatar64_path = avatar64_path;

		info.sex = sex;
		
		info.user_name = user_name;
		
		info.user_type = user_type;

		info.score = score;
		
		info.reg_time = reg_time;
		
		info.user_city = user_city;
		
		info.user_desc = user_desc;

		info.train_city = train_city;

		info.contacts = contacts;
		
		info.school = school;

		info.latitude = latitude;
		info.longitude = longitude;
		
		info.attention = attention;
		
		info.work_count = work_count;
				
		info.thumbs = thumbs;
		
		info.recom = recom;
		
		info.msgs = msgs;
		
		return info;
	}
}
