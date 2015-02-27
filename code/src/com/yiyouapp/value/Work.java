package com.yiyouapp.value;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

import android.net.Uri;

public class Work implements Serializable {

	public int user_id;
	public String user_name;
	public String user_type;
	public String sex;
	public String avatar_path;

	public double longitude;
	public double latitude;
	public double distance;
	public boolean has_location;
	
	public String city;
	
	public int works_id;

	public String work_type;
	
	// 作品描述
	public String work_desc;
	
	// 作品上传后过的时间段 
	public int elapsed_secs;
	// 作品获取下来时的设备时间
	public Date device_time;

	// 赞的数量
	public int prise_count;
	// 收藏的数量
	public int collect_count;
	// 评论数量
	public int comment_count;

	// 用户是否赞
	public boolean i_prise;
	// 用户是否收藏
	public boolean i_collect;
	
	// 作品图数量
	public int image_count;
	
	// 作品路径模式
	public String path_pattern;
	
	// 作品缩略图路径
	public String[] thumb_path;
	// 作品大图路径
	public String[] file_path;

	// 头像更新时间
	public String avatar_uptime;
	
	public Uri thumb_uri;
	public Uri avatar_uri;
	
	// 纵横比
	public float thumb1_ratio;

	/*
	public boolean relativeEqualWith(Object o) 
	{
		Work work = (Work)o;
		if(work == null)
			return false;
		
		boolean b_int = user_id == work.user_id &&
				works_id == work.works_id;
				
		boolean b_float = Math.abs(thumb_ratio - work.thumb_ratio) < 0.01;
		
		boolean b_str = user_name.equals(work.user_name) &&
				user_type.equals(work.user_type) &&
				sex.equals(work.sex) &&
				avatar_path.equals(work.avatar_path) &&
				city.equals(work.city) &&
				work_type.equals(work.work_type) &&
				work_desc.equals(work.work_desc) &&
				thumb_path.equals(work.thumb_path) &&
				file_path.equals(work.file_path);
		
		return b_int && b_float && b_str;
	}
	*/
}
