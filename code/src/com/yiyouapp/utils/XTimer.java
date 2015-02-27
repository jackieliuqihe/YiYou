package com.yiyouapp.utils;

import java.util.Timer;
import java.util.TimerTask;

import android.os.Handler;
import android.os.Message;

public class XTimer {

	public static interface OnTimerListener {
		public void onTimer(XTimer timer);
	}

	public Object tag = null;
	
	private OnTimerListener timer_listener_ = null;
	private boolean timer_running_ = false;
	
	private final static int TIMER_MSG = 1; 
	
	private int delay_;
	private int period_;
	
	private Timer timer_;	
	private TimerTask task_;

	private Handler handler = new Handler(){

		public void handleMessage(Message msg) {
			switch (msg.what) {    
			case TIMER_MSG:
				if(timer_listener_ != null)
				{
					timer_listener_.onTimer(XTimer.this);
				}
				break;    
			}
			super.handleMessage(msg);
		}
		
	};

	public XTimer(OnTimerListener timer_listener)
	{
		timer_listener_ = timer_listener;
		delay_ = 100;
		period_ = 100;
	}

	public XTimer(OnTimerListener timer_listener, int delay)
	{
		timer_listener_ = timer_listener;
		delay_ = delay;
		period_ = -1;
	}

	public XTimer(OnTimerListener timer_listener, int delay, int period)
	{
		timer_listener_ = timer_listener;
		delay_ = delay;
		period_ = period;
	}

	private void createTimer()
	{
		timer_ = new Timer();
		
		task_ = new TimerTask(){

			public void run() {
				Message message = new Message();    
				message.what = TIMER_MSG;    
				handler.sendMessage(message);  
			}
			
		};
	}
	
	public void start()
	{
		if(!timer_running_)
		{
			createTimer();
			
			if(period_ <= 0)
				timer_.schedule(task_, delay_);
			else
				timer_.schedule(task_, delay_, period_);

			timer_running_ = true;
		}
	}

	public void stop()
	{
		if(timer_running_)
		{
			task_.cancel();
			timer_.cancel();
			timer_.purge();
			timer_running_ = false;
		}
	}
}
