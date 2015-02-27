package com.yiyouapp.utils;

import java.util.ArrayList;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONObject;

import com.yiyouapp.AppConfig;
import com.yiyouapp.value.AvatarUpdate;
import com.yiyouapp.value.UserNews;
import com.yiyouapp.value.TypeDef;
import com.yiyouapp.value.UserInfo;
import com.yiyouapp.value.UserInfo.UserLatestWorkThumb;
import com.yiyouapp.value.UserType;
import com.yiyouapp.value.Work;

public class WorkUtil {

	public static String getTimeDesc(int secs)
	{
		float rlt = (float)secs / (float)60;
		int minu = (int)rlt;
		if(minu <= 2)
			return "刚刚";
		if(secs < 3600)
			return minu + "分钟前";
			
		String str;
		int hours = secs / 3600;
		
		if(hours < 24)
		{
			str = hours + "小时前";
		}
		else if(hours < 24 * 30)
		{
			str = (int)Math.floor(hours / 24) + "天前";
		}
		else if(hours < 24 * 30 * 12)
		{
			str = (int)Math.floor(hours / (24 * 30)) + "个月前";
		}
		else
		{
			str = (int)Math.floor(hours / (24 * 30 * 12)) + "年前";
		}
		
		return str;
	}

	public static String getUserTypeNameFromValue(String value)
	{
		if(value.length() != 6)
			return "未知用户类型";
		
		int type = Integer.parseInt(value.substring(2, 4));
		int subtype = Integer.parseInt(value.substring(4, 6));
		
		String usertype = "";
		
		switch(type)
		{
		case 1:
			if(subtype > 0 && subtype < 4)
				usertype = TypeDef.userType_names[subtype - 1];
			break;
		case 2:
		case 3:
		case 4:
			usertype = TypeDef.userType_names[type + 1];
			break;
		case 5:
			if(subtype > 0 && subtype < 4)
				usertype = TypeDef.userType_names[subtype + 5];
			break;
		}
		
		return usertype;
	}

	public static UserType getUserTypeFromValue(String value)
	{
		if(value.length() != 6)
			return UserType.None;
		
		int type = Integer.parseInt(value.substring(2, 4));
		int subtype = Integer.parseInt(value.substring(4, 6));
		
		switch(type)
		{
		case 1:
			if(subtype == 1)
				return UserType.Student1;
			else if(subtype == 2)
				return UserType.Student2;
			else if(subtype == 3)
				return UserType.Student3;
			break;
		case 2:
			return UserType.Studio;
		case 3:
			return UserType.Painter;
		case 4:
			return UserType.Hobbyist;
		case 5:
			if(subtype == 1)
				return UserType.Teacher1;
			else if(subtype == 2)
				return UserType.Teacher2;
			else if(subtype == 3)
				return UserType.Teacher3;
			break;
		}
		
		return UserType.None;
	}

	// UserInfo 转为JSON
	public static String userInfoToJSON(UserInfo info)
	{
		String json = "";

    	try
    	{
            JSONObject obj = new JSONObject();

            obj.put("user_id", info.user_id);
            obj.put("user_name", info.user_name);
            obj.put("user_type", info.user_type);
            
            obj.put("sex", info.sex);
            obj.put("avatar_path", info.avatar_path);

            obj.put("score", info.score);
            obj.put("attention", info.attention ? 1 : 0);
            obj.put("reg_time", info.reg_time);
            
            obj.put("user_city", info.user_city);
            obj.put("user_desc", info.user_desc);

            obj.put("train_city", info.train_city);
            obj.put("contacts", info.contacts);

            obj.put("school", info.school);
            obj.put("latitude", info.latitude * 1000000);
            obj.put("longitude", info.longitude * 1000000);

        	JSONObject workInfo = new JSONObject();

        	workInfo.put("work_count", info.work_count);

        	JSONArray arr = new JSONArray();

    		for(int i = 0; i < info.thumbs.size(); i++)
    		{
    			JSONObject thumbObj = new JSONObject();
    			thumbObj.put("file_path", info.thumbs.get(i).thumb_path);
    			thumbObj.put("thumb_ratio", info.thumbs.get(i).thumb_ratio);
    			
    			arr.put(thumbObj);
    		}
        	workInfo.put("thumbs", arr);
            
        	obj.put("works_info", workInfo);
        	
            json = obj.toString();
        } catch (Exception e) {
        	json = "";
		}

    	return json;
	}

	//  JSON转为UserInfo
	public static UserInfo jsonToUserInfo(String json)
	{
		UserInfo info = new UserInfo();
		
    	try
    	{
            JSONObject obj = new JSONObject(json);

            if(obj.getString("user_id") != "null")
            	info.user_id = obj.getInt("user_id");
            else
            	info.user_id = 0;
            
            info.user_name = obj.getString("user_name");
            info.user_type = obj.getString("user_type");
            info.sex = obj.getString("sex");

            info.avatar_path = StringUtil.getString(obj.getString("avatar_path"));
            info.avatar64_path = info.avatar_path.replace("*", "64");

            if(obj.getString("score") != "null")
            	info.score = obj.getInt("score");
            else
            	info.score = 0;

            if(obj.getString("attention") != "null")
            	info.attention = obj.getInt("attention") > 0;
            else
            	info.attention = false;
            
    		info.reg_time = StringUtil.getString(obj.getString("reg_time"));
            info.user_city = StringUtil.formatCity(obj.getString("user_city"));
            
            info.user_desc = StringUtil.getString(obj.getString("user_desc"));
            info.train_city = StringUtil.getString(obj.getString("train_city"));
            
            info.contacts = StringUtil.getString(obj.getString("contacts"));
            info.school = StringUtil.getString(obj.getString("school"));

            info.latitude = obj.getDouble("latitude") / 1000000;
            info.longitude = obj.getDouble("longitude") / 1000000;

        	info.thumbs = new ArrayList<UserLatestWorkThumb>();
        	
            if(obj.has("works_info"))
            {
            	JSONObject workInfo = (JSONObject)obj.get("works_info");

                if(workInfo.has("work_count"))
                	info.work_count = workInfo.getInt("work_count");
                
	            if(workInfo.has("thumbs"))
	            {
	            	JSONArray arr = new JSONArray(workInfo.getString("thumbs"));
	
	        		for(int i = 0; i < arr.length(); i++)
	        		{
	        			JSONObject thumbObj = (JSONObject)arr.get(i);
	        			UserLatestWorkThumb thumb = new UserLatestWorkThumb();

	        			thumb.thumb_path = thumbObj.getString("file_path").replace("*", "t1");
	        			thumb.thumb_ratio = (float)thumbObj.getDouble("thumb_ratio");
	        			
	        			thumb.thumb_path = thumb.thumb_path.replace("*", "1");
	        			
	        			info.thumbs.add(thumb);
	        		}
	            }
            }
            
            if(obj.has("msgs"))
            	info.msgs = jsonToNewsList(obj.getString("msgs"));
            
        } catch (Exception e) {
            info = new UserInfo();
		}

    	return info;
	}

	// 解析消息
	public static ArrayList<UserNews> jsonToNewsList(String json)
	{
		ArrayList<UserNews> msgs = new ArrayList<UserNews>();
		
    	try
    	{
			boolean self_loc = Math.abs(AppConfig.longitude) > 0.01 && 
					Math.abs(AppConfig.latitude) > 0.01;
			JSONArray arr = new JSONArray(json);

    		for(int i = 0; i < arr.length(); i++)
    		{
                JSONObject obj = (JSONObject)arr.get(i);
		                
                UserNews msg = new UserNews();
				
	            if(obj.getString("user_id") != "null")
	            	msg.user_id = obj.getInt("user_id");
	            else
	            	msg.user_id = 0;
	            
	            msg.user_name = obj.getString("user_name");
	            msg.user_type = obj.getString("user_type");
	            msg.sex = obj.getString("sex");
	            
	            msg.avatar64_path = StringUtil.getString(obj.getString("avatar_path")).replace("*", "64");
	
	    		msg.elapsed_secs = obj.getInt("elapsed_secs");
	            msg.user_city = StringUtil.formatCity(obj.getString("user_city"));
	            
	            msg.user_news_id = obj.getLong("user_news_id");
	            msg.is_view = obj.getInt("is_view") != 0;
	            msg.extra_data = obj.getInt("extra_data");
	            
	            msg.latitude = obj.getDouble("latitude");
	            msg.longitude = obj.getDouble("longitude");

                if(Math.abs(msg.longitude) > 0.01 && Math.abs(msg.latitude) > 0.01 && self_loc)
                {
                	msg.distance = Location.getDistance(AppConfig.latitude, AppConfig.longitude, msg.latitude, msg.longitude);
                	msg.has_location= true; 
                }
                else
                {
                	msg.has_location= false; 
                }

	            msg.news_type = obj.getInt("news_type");
	            msg.news = obj.getString("news");
	            
	            if(TypeDef.news_desc.containsKey(msg.news_type))
	            	msgs.add(msg);
    		}
        } catch (Exception e) {
        	msgs = new ArrayList<UserNews>();
		}

    	return msgs;
	}
	
	//  JSON转为简洁的UserInfo列表
	public static ArrayList<UserInfo> jsonToBriefUserInfo(String json)
	{
		ArrayList<UserInfo> users = new ArrayList<UserInfo>();
		
    	try
    	{
			boolean self_loc = Math.abs(AppConfig.longitude) > 0.01 && 
					Math.abs(AppConfig.latitude) > 0.01;
			JSONArray arr = new JSONArray(json);

    		for(int i = 0; i < arr.length(); i++)
    		{
                JSONObject obj = (JSONObject)arr.get(i);
		                
				UserInfo info = new UserInfo();
				
	            if(obj.getString("user_id") != "null")
	            	info.user_id = obj.getInt("user_id");
	            else
	            	info.user_id = 0;
	            
	            info.user_name = obj.getString("user_name");
	            info.user_type = obj.getString("user_type");
	            info.sex = obj.getString("sex");

	            info.avatar_path = StringUtil.getString(obj.getString("avatar_path"));
	    	    info.avatar64_path = info.avatar_path.replace("*", "64");
	
	    		info.reg_time = StringUtil.getString(obj.getString("reg_time"));
	            info.user_city = StringUtil.formatCity(obj.getString("user_city"));
	            
	            info.user_desc = StringUtil.getString(obj.getString("user_desc"));
	            info.train_city = StringUtil.getString(obj.getString("train_city"));
	            
	            info.latitude = obj.getDouble("latitude") / 1000000;
	            info.longitude = obj.getDouble("longitude") / 1000000;

	            if(obj.has("est_dist"))
	            	info.est_dist = obj.getString("est_dist");
	            
                if(Math.abs(info.longitude) > 0.01 && Math.abs(info.latitude) > 0.01 && self_loc)
                {
                	info.distance = Location.getDistance(AppConfig.latitude, AppConfig.longitude, info.latitude, info.longitude);
                	info.has_location= true; 
                }
                else
                {
                	info.has_location= false; 
                }
                
	            users.add(info);
    		}
        } catch (Exception e) {
        	users = new ArrayList<UserInfo>();
		}

    	return users;
	}

	//  JSON转为Work
	public static ArrayList<Work> jsonToWorks(String json)
	{
		ArrayList<Work> works = new ArrayList<Work>();
		
		try 
		{
			boolean self_loc = Math.abs(AppConfig.longitude) > 0.01 && 
					Math.abs(AppConfig.latitude) > 0.01;
			Date now = new Date();
			
			JSONArray arr = new JSONArray(json);
			
    		for(int i = 0; i < arr.length(); i++)
    		{
                JSONObject work = (JSONObject)arr.get(i);
                
                Work vo = new Work();

                vo.user_id = work.getInt("user_id");
                vo.user_name = work.getString("user_name");
                vo.user_type = work.getString("user_type");
                vo.sex = work.getString("sex");
                
                if(work.getString("avatar_path") == "null")
                {
                	vo.avatar_path = "";
                	vo.avatar_uptime = "";
                }
                else
                {
                	vo.avatar_path = work.getString("avatar_path").replace("*", "64");
                	vo.avatar_uptime = work.getString("avatar_uptime");
                }

                vo.longitude = work.getDouble("longitude") / 1000000;
                vo.latitude = work.getDouble("latitude") / 1000000;
                vo.city = StringUtil.formatCity(work.getString("user_city"));
                
                vo.works_id = work.getInt("works_id");
                vo.work_type = work.getString("work_type");
                vo.work_desc = work.getString("work_desc");

                vo.elapsed_secs = work.getInt("elapsed_secs");
                vo.device_time = now;

                vo.collect_count = work.getInt("collect_count");
                vo.prise_count = work.getInt("prise_count");
                vo.comment_count = work.getInt("comment_count");

                vo.i_collect = work.getInt("i_collect") != 0;
                vo.i_prise = work.getInt("i_prise") != 0;

                vo.image_count = work.getInt("count");
                vo.path_pattern = work.getString("file_path");
                
                vo.thumb_path = new String[1];
                vo.file_path = new String[1];
                
                vo.thumb_path[0] = vo.path_pattern.replace("*", "t1");
                vo.file_path[0] = vo.path_pattern.replace("*", "1");

                vo.thumb1_ratio = (float)work.getDouble("thumb_ratio");
                
                if(Math.abs(vo.longitude) > 0.01 && Math.abs(vo.latitude) > 0.01 && self_loc)
                {
                    vo.distance = Location.getDistance(AppConfig.latitude, AppConfig.longitude, vo.latitude, vo.longitude);
                	vo.has_location= true; 
                }
                else
                {
                	vo.has_location= false; 
                }
                
                works.add(vo);
                
                // 用户头像更新
                AppConfig.avatars_update.setUptime(StringUtil.getFileNameFromPath(vo.avatar_path), vo.avatar_uptime);
    		}

        } catch (Exception e) {
        	works = new ArrayList<Work>();
		}

    	return works;
	}

	// Work 转为JSON
	public static String worksToJSON(ArrayList<Work> works)
	{
		String json = "";

    	try
    	{
			JSONArray arr = new JSONArray();
			
    		for(int i = 0; i < works.size(); i++)
    		{
                JSONObject obj = new JSONObject();

                Work work = works.get(i);

                obj.put("user_id", work.user_id);
                obj.put("user_name", work.user_name);
                
                obj.put("user_type", work.user_type);
                obj.put("sex", work.sex);

                obj.put("avatar_path", work.avatar_path);
                obj.put("avatar_uptime", work.avatar_uptime);
                obj.put("longitude", work.longitude * 1000000);
                
                obj.put("latitude", work.latitude * 1000000);
                obj.put("user_city", work.city);

                obj.put("works_id", work.works_id);
                obj.put("work_type", work.work_type);

                obj.put("work_desc", work.work_desc);
                obj.put("elapsed_secs", work.elapsed_secs);

                obj.put("collect_count", work.collect_count);
                obj.put("prise_count", work.prise_count);
                obj.put("comment_count", work.comment_count);

                obj.put("i_collect", work.i_collect ? 1 : 0);
                obj.put("i_prise", work.i_prise ? 1 : 0);
                
                obj.put("count", work.image_count);
                obj.put("file_path", work.path_pattern);
                obj.put("thumb_ratio", work.thumb1_ratio);
                
                arr.put(i, obj);
    		}

            json = arr.toString();
        } catch (Exception e) {
        	json = "";
		}

    	return json;
	}

}
