package com.yiyouapp.value;

public class ReqData {

	// 失败时的处理方式
	public enum FailHandleType
	{
		Retry, // 必须重试
		Notify, // 通知
		Quite, // 静默
	}
	
	public FailHandleType type;
	
	// 发送者
	public Object sender;
	// 处理者
	public Object receiver;
	
	// 输入数据
	public Object input;
	// 输出数据
	public Object output;
	
	public static ReqData create(Object s, FailHandleType t)
	{
		ReqData req = new ReqData();
		
		req.type = t;
		req.sender = s;
		
		return req;
	}
}
