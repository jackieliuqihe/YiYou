package com.yiyouapp.utils;

import com.yiyouapp.AppConfig;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.util.Log;

public class SizeUtil {

	public static Bitmap adjustScreenBitmap(Bitmap bmp, boolean force, int marginDp)
	{
		int swidth = AppConfig.screen_width - dp2px(marginDp);
		if(!force && bmp.getWidth() <= swidth)
			return bmp;
		
		float image_width = (float)swidth;
		float expect_height = image_width / ((float)bmp.getWidth() / (float)bmp.getHeight());
		
		float scaleX = image_width / (float)bmp.getWidth();
		float scaleY = (float)expect_height / (float)bmp.getHeight();
		
		return ImageUtil.scaleImage(bmp, scaleX, scaleY);
	}

	public static Bitmap adjustBitmapInMax(Bitmap bmp)
	{
		if(bmp.getWidth() <= AppConfig.max_image_width && 
				bmp.getHeight() <= AppConfig.max_image_height)
			return bmp;
		
		float scaleX = (float)AppConfig.max_image_width / (float)bmp.getWidth();
		float scaleY = (float)AppConfig.max_image_height / (float)bmp.getHeight();
		
		float scale = scaleX;
		if(scaleY < scaleX)
			scale = scaleY;
		
		return ImageUtil.scaleImage(bmp, scale, scale);
	}

	public static Bitmap adjustBitmapInSize(Bitmap bmp, int limitWidth, int limitHeight)
	{
		if(bmp.getWidth() <= limitWidth && 
				bmp.getHeight() <= limitHeight)
			return bmp;
		
		float scaleX = (float)limitWidth / (float)bmp.getWidth();
		float scaleY = (float)limitHeight / (float)bmp.getHeight();
		
		float scale = scaleX;
		if(scaleY < scaleX)
			scale = scaleY;
		
		return ImageUtil.scaleImage(bmp, scale, scale);
	}

	public static Bitmap adjustAvatarBitmap(Bitmap bmp)
	{
		int width = bmp.getWidth();
		int height = bmp.getHeight();
		
		if(width == AppConfig.avatar_image_size && 
				height == AppConfig.avatar_image_size)
			return bmp;
		
		int pixels = dp2px(AppConfig.avatar_image_size);
		float scaleX = (float)pixels / (float)width;
		float scaleY = (float)pixels / (float)height;

	    Matrix matrix = new Matrix();
	    matrix.postScale(scaleX, scaleY);

		return Bitmap.createBitmap(bmp, 0, 0, width, height, matrix, true);
	}

	/** 
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素) 
     */  
    public static int dp2px(float dpValue) 
    {  
        return (int) (dpValue * AppConfig.density + 0.5f);  
    }  
  
    /** 
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp 
     */  
    public static int px2dp(float pxValue) 
    {  
        return (int) (pxValue / AppConfig.density + 0.5f);  
    }  
}
