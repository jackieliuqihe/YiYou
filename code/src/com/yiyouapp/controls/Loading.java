package com.yiyouapp.controls;

import com.yiyouapp.R;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class Loading {

	private LayoutInflater inflater_;
	private ViewGroup container_;

	private int contentId_;
	private View target_;
	
	public Loading(Activity act, ViewGroup container, View target)
	{
		inflater_ = LayoutInflater.from(act);
		container_ = container;
		target_ = target;
		
		contentId_ = -1;
	}
	
	// 设置为加载完成
	public void setCompleted()
	{
		if(target_ != null)
			target_.setVisibility(View.VISIBLE);
		removeContent();
	}

	// 设置文字提示
	public void setNote(String note)
	{
		if(target_ != null)
			target_.setVisibility(View.GONE);
		removeContent();

		contentId_ = container_.getChildCount();
		View v = inflater_.inflate(R.layout.load_note, container_);
		TextView tv = (TextView)v.findViewById(R.id.note);
		
		tv.setText(note);
	}

	// 设置加载中
	public void setLoading()
	{
		if(target_ != null)
			target_.setVisibility(View.GONE);
		removeContent();

		contentId_ = container_.getChildCount();
		inflater_.inflate(R.layout.load_loading, container_);
		//act_.addContentView(content_, container_.getLayoutParams());
	}

	// 设置加载中
	public void setFailed()
	{
		if(target_ != null)
			target_.setVisibility(View.GONE);
		removeContent();

		contentId_ = container_.getChildCount();
		inflater_.inflate(R.layout.load_failed, container_);
		//act_.addContentView(content_, container_.getLayoutParams());
	}
	
	private void removeContent()
	{
		if(contentId_ >= 0)
		{
			container_.removeViewAt(contentId_);
			
			contentId_ = -1;
		}
	}
}
