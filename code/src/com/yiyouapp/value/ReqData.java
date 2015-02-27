package com.yiyouapp.value;

public class ReqData {

	// ʧ��ʱ�Ĵ���ʽ
	public enum FailHandleType
	{
		Retry, // ��������
		Notify, // ֪ͨ
		Quite, // ��Ĭ
	}
	
	public FailHandleType type;
	
	// ������
	public Object sender;
	// ������
	public Object receiver;
	
	// ��������
	public Object input;
	// �������
	public Object output;
	
	public static ReqData create(Object s, FailHandleType t)
	{
		ReqData req = new ReqData();
		
		req.type = t;
		req.sender = s;
		
		return req;
	}
}
