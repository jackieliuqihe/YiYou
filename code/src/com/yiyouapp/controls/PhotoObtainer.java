package com.yiyouapp.controls;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.yiyouapp.AppConfig;
import com.yiyouapp.utils.SDCardUtil;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.Video.Media;
import android.util.Log;

public class PhotoObtainer {

	// 获取图
	public static final int GET_IMAGE = 1;
	// 裁剪
	public static final int CROP_IMAGE = 2;

	private static Activity activity_;
	
	private static Uri uri_ = null;
	
	// 拍照
	public static void doTakePhoto(Activity activity)
	{
		activity_ = activity;
		
		try 
		{
            //拍照我们用Action为MediaStore.ACTION_IMAGE_CAPTURE，  
            //有些人使用其他的Action但我发现在有些机子中会出问题，所以优先选择这个  

			SimpleDateFormat timeStampFormat = new SimpleDateFormat("yyyyMMddHHmmssSS");
            String filename = timeStampFormat.format(new Date());
            ContentValues values = new ContentValues();
            
	        values.put(Media.TITLE, filename);
	        values.put(Media.DESCRIPTION, "Image from YiYou");
	        uri_ = activity.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
			
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);  
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri_);
            activity_.startActivityForResult(intent, GET_IMAGE);
        }
		catch (Exception e)
		{
			Log.e(AppConfig.APP_NAME, "cannot take photo");
        }
	}

	// 获取照片
	public static void doGetPicture(Activity activity)
	{
		activity_ = activity;
		uri_ = null;
		
		if(!doGetPicture2())
			doGetPicture1();
	}
	
	// 获取照片方法一
	private static boolean doGetPicture1()
	{
		try 
		{  
            //选择照片的时候也一样，我们用Action为Intent.ACTION_GET_CONTENT，  
            //有些人使用其他的Action但我发现在有些机子中会出问题，所以优先选择这个  
			
            Intent intent = new Intent();  
            intent.setType("image/*");  
            intent.setAction(Intent.ACTION_GET_CONTENT);
            activity_.startActivityForResult(intent, GET_IMAGE);
        } 
		catch (Exception e) 
        {
			Log.e(AppConfig.APP_NAME, "cannot get picture [method 1]");
			
			return false;
        }
		
		return true;
	}

	// 获取照片方法二
	private static boolean doGetPicture2()
	{
		try 
		{  
            //选择照片的时候也一样，我们用Action为Intent.ACTION_GET_CONTENT，  
            //有些人使用其他的Action但我发现在有些机子中会出问题，所以优先选择这个  
			
            Intent intent = new Intent(
                    Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            activity_.startActivityForResult(intent, GET_IMAGE);  
        } 
		catch (Exception e) 
        {
			Log.e(AppConfig.APP_NAME, "cannot get picture [method 2]");
			
			return false;
        }
		
		return true;
	}

	// 获取照片
	public static void doCropImage(Uri uri)
	{
        try 
        {
			Intent intent = new Intent("com.android.camera.action.CROP");    
	        intent.setDataAndType(uri, "image/*");    
	        // 设置裁剪    
	        intent.putExtra("crop", "true");    
	        // aspectX aspectY 是宽高的比例    
	        intent.putExtra("aspectX", 1);    
	        intent.putExtra("aspectY", 1);    
	        // outputX outputY 是裁剪图片宽高    
	        intent.putExtra("outputX", 256);    
	        intent.putExtra("outputY", 256);    
	        intent.putExtra("scale", true);//黑边
	        intent.putExtra("scaleUpIfNeeded", true);//黑边
	        intent.putExtra("return-data", true);
	        
	        activity_.startActivityForResult(intent, CROP_IMAGE);
        } 
        catch (Exception e) 
        {
        }
	}
	
    public static Bitmap processBitmap(int requestCode, int resultCode, Intent data) 
    {
    	Bitmap image = null;
    	
    	if(resultCode != Activity.RESULT_OK) 
            return image;

        try 
        {
	        switch (requestCode)
	        {
	        case GET_IMAGE:
	            if (data != null) 
	            {
	                //取得返回的Uri,基本上选择照片的时候返回的是以Uri形式，但是在拍照中有得机子呢Uri是空的，所以要特别注意  
	                Uri mImageCaptureUri = data.getData();  
	                
	                //返回的Uri不为空时，那么图片信息数据都会在Uri中获得。如果为空，那么我们就进行下面的方式获取  
	                if (mImageCaptureUri != null) 
	                {
                        //这个方法是根据Uri获取Bitmap图片的静态方法  
                        image = MediaStore.Images.Media.getBitmap(activity_.getContentResolver(), mImageCaptureUri);
	                }
	                else 
	                {  
	                    Bundle extras = data.getExtras();  
	                    if(extras != null) 
	                    {  
	                        //这里是有些拍照后的图片是直接存放到Bundle中的所以我们可以从这里面获取Bitmap图片  
	                        image = extras.getParcelable("data");
	                    }
	                    
	                    /*
	                    if(image != null && AppConfig.photo_dir != null)
	                    {
	                    	SDCardUtil.savePicture(image, AppConfig.photo_dir + getUniquePhotoName());
	                    }*/
	                }  
	  
	            }
	            else if(uri_ != null)
	            {
	            	String[] proj = { MediaStore.Images.Media.DATA };
	            	Cursor actualimagecursor = activity_.managedQuery(uri_, proj, null, null, null);  
	            	
	            	int actual_image_column_index = actualimagecursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);  
	            	
	            	actualimagecursor.moveToFirst();
	            	String fileName = actualimagecursor.getString(actual_image_column_index);  
	            	
	            	image = BitmapFactory.decodeFile(fileName);
	            }
	            break;  
	        case CROP_IMAGE:
	        	image = data.getParcelableExtra("data");
	
	        	if(image == null)
	        	{
	                Bundle extras = data.getExtras();  
	                if (extras != null) 
	                {  
	                    //这里是有些拍照后的图片是直接存放到Bundle中的所以我们可以从这里面获取Bitmap图片  
	                    image = extras.getParcelable("data");
	                }  
	        	}
	        	
	        	break;
	        default:  
	            break;
	        }

        } 
        catch (Exception e) 
        {
			Log.e(AppConfig.APP_NAME, "process bitmap error");
        }
        
        return image;
    } 

    /*
    private static String getUniquePhotoName()
    {
    	String name = "yiyou_" + System.currentTimeMillis() + ".jpg";
    	
    	return name;
    }*/
    public static Uri processUri(int requestCode, int resultCode, Intent data) 
    {
    	if(resultCode != Activity.RESULT_OK) 
            return null;

        try 
        {
	        switch (requestCode)
	        {
	        case GET_IMAGE:
	            if (data != null) 
	            {  
	                //取得返回的Uri,基本上选择照片的时候返回的是以Uri形式，但是在拍照中有得机子呢Uri是空的，所以要特别注意  
	            	uri_ = data.getData();
	            }
	            break;
	        default:  
	            break;
	        }
	
	    } 
	    catch (Exception e) 
	    {
			Log.e(AppConfig.APP_NAME, "get uri error");
	    }
	    
        return uri_;
    } 
    
}
