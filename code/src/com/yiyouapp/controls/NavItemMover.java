package com.yiyouapp.controls;

import com.yiyouapp.AppConfig;
import com.yiyouapp.R;
import com.yiyouapp.utils.ImageUtil;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;

public class NavItemMover extends View {

	private Bitmap bitmap_;
	private int left_ = 0;
	
	public NavItemMover(Context context) {
		super(context);
	}

	public NavItemMover(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public NavItemMover(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	public void init()
	{
		bitmap_ = BitmapFactory.decodeResource(getResources(), R.drawable.nav_item_sel);
		
		int navItemWidth = AppConfig.screen_width / 2;
		float scaleX = (float)navItemWidth / (float)bitmap_.getWidth();
		float scaleY = 1.0f;
		
		bitmap_ = ImageUtil.scaleImage(bitmap_, scaleX, scaleY);
	}
	
	public void setHorizontal(int left)
	{
		left_ = left;
		invalidate();
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		if(bitmap_ != null)
			canvas.drawBitmap(bitmap_, left_, 0, null);
	}

}
