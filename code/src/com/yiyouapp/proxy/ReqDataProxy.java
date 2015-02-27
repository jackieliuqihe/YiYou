package com.yiyouapp.proxy;

import org.puremvc.java.patterns.proxy.Proxy;

import com.yiyouapp.value.ReqData;

public class ReqDataProxy extends Proxy {

	protected ReqData req_;
	private String info_ = "";

	public ReqDataProxy(String proxyName) {
		super(proxyName);
	}

	public void setReqData(ReqData req)
	{
		req.receiver = this;
		req_ = req;
	}

	public ReqData getReqData()
	{
		return req_;
	}

	public String getInfo()
	{
		return info_;
	}
	
	public void setInfo(String info)
	{
		info_ = info;
	}
	
}
