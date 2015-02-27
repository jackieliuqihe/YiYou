package com.yiyouapp.value;

import android.net.Uri;

public class ChatMsg {

	// 是否已经校验过了(从服务器端获取的)
	public SendState state;
	
	// 发送时的时间
	public long sending_time;
	
	public boolean show_time;
	
	public int id;
	
	public String date;

	public String text;

	public boolean isComMeg = true;

	public Uri avatar;
}
