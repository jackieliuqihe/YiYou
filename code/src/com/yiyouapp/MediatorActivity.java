package com.yiyouapp;

import org.puremvc.java.interfaces.IMediator;
import org.puremvc.java.interfaces.INotification;
import org.puremvc.java.interfaces.INotifier;
import org.puremvc.java.patterns.facade.Facade;

import com.yiyouapp.cmd.AccountCmd;
import com.yiyouapp.cmd.CancelNetCmd;
import com.yiyouapp.cmd.RegCmd;
import com.yiyouapp.controls.Loading;
import com.yiyouapp.proxy.NetImageProxy.ImageType;
import com.yiyouapp.proxy.NetImageProxy.InputData;
import com.yiyouapp.proxy.NetImageProxy.OutputData;
import com.yiyouapp.value.ReqData;

import android.app.Activity;
import android.app.ProgressDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnKeyListener;
import android.os.Bundle;

public class MediatorActivity extends Activity implements IMediator, INotifier {

	protected Facade facade = Facade.getInstance();

	/**
	 * The default name of the <code>Mediator</code>.
	 */
	public static final String NAME = "Mediator";
	
	/** 
	 * The name of the <code>Mediator</code>.
	 */
	protected String mediatorName = null;

	/**
	 * The view component
	 */
	protected Object viewComponent = null;

	public void sendNotification( String notificationName, Object body,	String type )
	{
		facade.sendNotification( notificationName, body, type );
	}
	
	public void sendNotification( String notificationName, Object body)
	{
		facade.sendNotification( notificationName, body);
	}
	
	public void sendNotification( String notificationName)
	{
		facade.sendNotification( notificationName);
	}

	/**
	 * Get the name of the <code>Mediator</code>.
	 * 
	 * @return the name
	 */
	public final String getMediatorName( )
	{
		return mediatorName;
	}

	protected String getName()
	{
		return NAME;
	}

	/**
	 * Set the <code>IMediator</code>'s view component.
	 * 
	 * @param viewComponent
	 * 		The view component
	 */
	public void setViewComponent( Object viewComponent ) 
	{
		this.viewComponent = viewComponent;
	}

	/**
	 * Get the <code>Mediator</code>'s view component.
	 * 
	 * <P>
	 * Additionally, an implicit getter will usually be defined in the subclass
	 * that casts the view object to a type, like this:
	 * </P>
	 * 
	 * <listing> private function get comboBox : mx.controls.ComboBox { return
	 * viewComponent as mx.controls.ComboBox; } </listing>
	 * @return the view component
	 */
	public Object getViewComponent()
	{
		return viewComponent;
	}

	/**
	 * Handle <code>INotification</code>s.
	 * 
	 * <P>
	 * Typically this will be handled in a switch statement, with one 'case'
	 * entry per <code>INotification</code> the <code>Mediator</code> is
	 * interested in.
	 * @param notification 
	 */
	public void handleNotification( INotification notification ){}

	/**
	 * List the <code>INotification</code> names this <code>Mediator</code>
	 * is interested in being notified of.
	 * 
	 * @return String[] the list of <code>INotification</code> names
	 */
	public String[] listNotificationInterests( )
	{
		return new String[]{};
	}

	/**
	 * Called by the View when the Mediator is registered
	 */ 
	public void onRegister(){}
	
	/**
	 * Called by the View when the Mediator is removed
	 */ 
	public void onRemove(){}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		boolean reinit = false;
		if(AppConfig.settings == null)
		{
			AppConfig.Initialize(this);
			AppFacade.getInstance().startup(this);
			
			Log.i(AppConfig.APP_NAME, "app config reinitialized");
			
			reinit = true;
		}
		
		AppConfig.activities.put(getName(), this);
		
		this.mediatorName = getName(); 
		this.viewComponent = this;	
		facade.registerMediator(this);
		
		if(reinit)
		{
			// 检查登录情况
			if(isLoginedPage())
			{
				if(!AppConfig.account.IsLogin())
				{
					// 进入登录页
					startActivity(new Intent(this, LoginActivity.class));
				}
				else
				{
					sendNotification(AccountCmd.GET_ACCOUNT_INFO, ReqData.create(this, ReqData.FailHandleType.Notify));
				}
			}
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		AppConfig.activities.remove(this.mediatorName);
		
		facade.removeMediator(this.mediatorName);
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		AppConfig.current_activity = this;
	}
	
	/**
	 *  Activity 相关
	 */
	
	private ProgressDialog progress_dlg_;
	private String info_;
	
	public void showProgress(String info)
	{
		info_ = info;
		
		if(progress_dlg_ == null)
		{
			progress_dlg_ = ProgressDialog.show(this, null, info, true);
			progress_dlg_.setOnKeyListener(new OnKeyListener(){
	
				@Override
				public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
	
			        if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN)
			        {
			        	sendNotification(CancelNetCmd.CANCEL_NET);
			        	hideProgress();
			        }
			        
					return false;
				}
				
			});
		}
	}

	public void showProgress()
	{
		if(info_ != null && !info_.equals(""))
			showProgress(info_);
	}
	
	public void hideProgress()
	{
		if(progress_dlg_ != null)
		{
			if(progress_dlg_.isShowing())
				progress_dlg_.dismiss();
			progress_dlg_ = null;
		}
	}
	
	// 是否是登录后进的页
	protected boolean isLoginedPage()
	{
		return true;
	}
	
	protected Loading loading_;
	
	// 设置开始加载
	protected void initLoading(ViewGroup container, View target)
	{
		if(loading_ == null)
			loading_ = new Loading(this, container, target);
	}
}
