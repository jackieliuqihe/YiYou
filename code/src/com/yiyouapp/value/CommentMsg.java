package com.yiyouapp.value;

import android.net.Uri;

public class CommentMsg {

	// 是否已经校验过了(从服务器端获取的)
	public SendState state;
	
	// 发送时的时间
	public long sending_time;
	
	public int id;

	public int user_id;
	
	public int reply_user_id;
	
	public String name;

	public String date;

	public String original_text;
	public String text;

	public String avatar_path;
}
