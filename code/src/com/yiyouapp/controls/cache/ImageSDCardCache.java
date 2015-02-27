package com.yiyouapp.controls.cache;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

import com.yiyouapp.AppConfig;
import com.yiyouapp.utils.SDCardUtil;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.util.Xml;

public class ImageSDCardCache {

    public ImageSDCardCache() 
    {
    }

    private String getFileNameFromKey(String key)
    {
    	return key;
    }
    
    public byte[] getFile(String key)
    {
    	if(key == null || AppConfig.cache_dir == null)
    		return null;
    	
    	String filename = getFileNameFromKey(key);
    	
    	byte[] content = null;

        try {
			content = SDCardUtil.readFile(AppConfig.cache_dir + filename);
    		//enc_dec(content);
		} catch (Exception e) {
			return null;
		}

        return content;
    }

	public boolean isExist(String key)
	{
    	return SDCardUtil.isFileExist(AppConfig.cache_dir + getFileNameFromKey(key));    	
	}

	public Uri getExistFileUri(String key)
	{
		File file = new File(AppConfig.cache_dir + getFileNameFromKey(key));
		if(file.isFile() && file.exists())
			return Uri.fromFile(file);

		return null;
	}
	
    public void setFile(String key, byte[] bytes)
    {
    	if(AppConfig.cache_dir == null)
    		return;

    	try {
    		//enc_dec(bytes);
    		SDCardUtil.writeFile(AppConfig.cache_dir + getFileNameFromKey(key), bytes);
		} catch (Exception e) {
			Log.e(AppConfig.APP_NAME, "write file error:" + key);
		}
    }

    private void enc_dec(byte[] bytes)
    {
    	if(bytes.length < AppConfig.encode_length)
    		return;
    	
    	int len =  AppConfig.encode_key.length;
    	for(int i = 0; i < AppConfig.encode_length; i++)
    		bytes[i] ^= AppConfig.encode_key[i % len];
    }

	// ÕûÀí»º´æ
	public void manage()
	{
    	if(AppConfig.cache_dir == null)
    		return;

    	new Thread(new Runnable(){

    		@Override
    		public void run() 
    		{
		    	File dir = new File(AppConfig.cache_dir);
		    	File[] cache_files = dir.listFiles(); 
		    	
		    	if (cache_files == null)
		    		return;
				
		    	long total_size = 0;
		    	int count = cache_files.length;
		    	
		    	for(int i = 0; i < count; i++)
		    		total_size += cache_files[i].length();
		    	
		    	if(total_size <= AppConfig.max_sdcard_cache_size &&
		    		count <= AppConfig.max_sdcard_files_count)
		    		return;
		    	
		    	Arrays.sort(cache_files, new CacheFileComparator());
		    	
		    	int index = 0;
		    	if(count > AppConfig.max_sdcard_files_count)
		    	{
		    		int clearCount = count - AppConfig.clearto_sdcard_files_count;
		    		
			    	for(index = 0; index < clearCount; index++)
			    	{
			    		total_size -= cache_files[index].length();
			    		cache_files[index].delete();
			    	}
		    	}
		    	
		    	while(total_size > AppConfig.clearto_sdcard_cache_size && index < count)
		    	{
		    		total_size -= cache_files[index].length();
		    		cache_files[index].delete();
		    		index++;
		    	}
    		}
    	}).start();
	}
	
	class CacheFileComparator implements Comparator
	{
		public int compare(Object arg0, Object arg1) 
		{
			File f0 = (File)arg0;
			File f1 = (File)arg1;
			
			return (int)(f0.lastModified() - f1.lastModified());
		}
	}
	
}
