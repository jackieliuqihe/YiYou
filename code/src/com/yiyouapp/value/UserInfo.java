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
	
	// ע��ʱ��
	public String reg_time;
	
	// �û�λ��
	public String user_city;
	
	// ����ǩ��
	public String user_desc;

	// ��ѵ����
	public String train_city;

	// ��ϵ�绰
	public String contacts;
	
	// �Ͷ�ѧУ
	public String school;

	// �û���γ��
	public double latitude;
	public double longitude;

	public double distance;
	public boolean has_location;
	
	// ���ƾ��룬�������˼���
	public String est_dist;
	
	// �û��Ƿ��ע
	public boolean attention;
	
	// ��Ʒ����
	public int work_count;
	
	// ���˵���Ʒ
	public ArrayList<UserLatestWorkThumb> thumbs;
	// �Ƽ������˵Ļ��һ�ѧ��
	public ArrayList<UserInfo> recom;
	// �ҵ���Ϣ
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
