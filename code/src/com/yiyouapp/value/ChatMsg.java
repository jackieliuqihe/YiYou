package com.yiyouapp.value;

import android.net.Uri;

public class ChatMsg {

	// �Ƿ��Ѿ�У�����(�ӷ������˻�ȡ��)
	public SendState state;
	
	// ����ʱ��ʱ��
	public long sending_time;
	
	public boolean show_time;
	
	public int id;
	
	public String date;

	public String text;

	public boolean isComMeg = true;

	public Uri avatar;
}
