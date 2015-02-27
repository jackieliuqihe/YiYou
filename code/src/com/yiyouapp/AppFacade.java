package com.yiyouapp;

import org.puremvc.java.interfaces.IProxy;
import org.puremvc.java.patterns.facade.Facade;

import android.app.Activity;

import com.yiyouapp.cmd.NetErrorCmd;
import com.yiyouapp.cmd.NetInfoCmd;
import com.yiyouapp.cmd.RegCmd;
import com.yiyouapp.cmd.StartupCmd;
import com.yiyouapp.proxy.NetTextProxy;

public class AppFacade extends Facade {

    private static AppFacade instance_ = null;

    public static AppFacade getInstance()
    {
        if( instance_ == null) instance_ = new AppFacade ();
        return instance_ ;
    }

    protected void initializeController() 
    {
	    super.initializeController();

	    // Æô¶¯Ïà¹Ø
	    registerCommand(StartupCmd.STARTUP, new StartupCmd());
    }

    @Override
	protected void initializeModel() {
		super.initializeModel();

	}

	@Override
	public IProxy retrieveProxy(String proxyName) {
		IProxy proxy = super.retrieveProxy(proxyName); 
		AppConfig.current_proxy = (NetTextProxy)proxy;
		return proxy;
	}

	public void startup(Activity main) 
    {
    	sendNotification(StartupCmd.STARTUP, main);
    }
}
