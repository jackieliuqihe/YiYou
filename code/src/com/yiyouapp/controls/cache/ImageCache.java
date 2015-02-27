package com.yiyouapp.controls.cache;

import java.lang.ref.SoftReference;
import java.util.HashMap;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class ImageCache {

	public ImageMemCache mem;
	public ImageSDCardCache sdcard;

	public ImageCache()
	{
		mem = new ImageMemCache();
		sdcard = new ImageSDCardCache();
	}

	public Bitmap getImage(String key) 
	{
		Bitmap result = mem.getImage(key);
		
		if(result == null)
		{
			byte[] content = sdcard.getFile(key);
    	
			if(content != null && content.length > 0)
			{
				try
				{
					result = BitmapFactory.decodeByteArray(content, 0, content.length);
				} catch (Exception e) {
					// 删除指定的文件
	            }
			}

		   if(result != null)
			   mem.setImage(key, result);
		}

		return result;
	}

	public boolean isExist(String key)
	{
		Bitmap result = mem.getImage(key);
		
		if(result != null)
			return true;
		
		return sdcard.isExist(key);
	}
	
	public void setImage(String key, Bitmap bitmap, byte[] bytes)
	{
	   mem.setImage(key, bitmap);
	   sdcard.setFile(key, bytes);
	}
}
