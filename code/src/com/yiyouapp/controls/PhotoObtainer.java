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

	// ��ȡͼ
	public static final int GET_IMAGE = 1;
	// �ü�
	public static final int CROP_IMAGE = 2;

	private static Activity activity_;
	
	private static Uri uri_ = null;
	
	// ����
	public static void doTakePhoto(Activity activity)
	{
		activity_ = activity;
		
		try 
		{
            //����������ActionΪMediaStore.ACTION_IMAGE_CAPTURE��  
            //��Щ��ʹ��������Action���ҷ�������Щ�����л�����⣬��������ѡ�����  

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

	// ��ȡ��Ƭ
	public static void doGetPicture(Activity activity)
	{
		activity_ = activity;
		uri_ = null;
		
		if(!doGetPicture2())
			doGetPicture1();
	}
	
	// ��ȡ��Ƭ����һ
	private static boolean doGetPicture1()
	{
		try 
		{  
            //ѡ����Ƭ��ʱ��Ҳһ����������ActionΪIntent.ACTION_GET_CONTENT��  
            //��Щ��ʹ��������Action���ҷ�������Щ�����л�����⣬��������ѡ�����  
			
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

	// ��ȡ��Ƭ������
	private static boolean doGetPicture2()
	{
		try 
		{  
            //ѡ����Ƭ��ʱ��Ҳһ����������ActionΪIntent.ACTION_GET_CONTENT��  
            //��Щ��ʹ��������Action���ҷ�������Щ�����л�����⣬��������ѡ�����  
			
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

	// ��ȡ��Ƭ
	public static void doCropImage(Uri uri)
	{
        try 
        {
			Intent intent = new Intent("com.android.camera.action.CROP");    
	        intent.setDataAndType(uri, "image/*");    
	        // ���òü�    
	        intent.putExtra("crop", "true");    
	        // aspectX aspectY �ǿ�ߵı���    
	        intent.putExtra("aspectX", 1);    
	        intent.putExtra("aspectY", 1);    
	        // outputX outputY �ǲü�ͼƬ���    
	        intent.putExtra("outputX", 256);    
	        intent.putExtra("outputY", 256);    
	        intent.putExtra("scale", true);//�ڱ�
	        intent.putExtra("scaleUpIfNeeded", true);//�ڱ�
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
	                //ȡ�÷��ص�Uri,������ѡ����Ƭ��ʱ�򷵻ص�����Uri��ʽ���������������еû�����Uri�ǿյģ�����Ҫ�ر�ע��  
	                Uri mImageCaptureUri = data.getData();  
	                
	                //���ص�Uri��Ϊ��ʱ����ôͼƬ��Ϣ���ݶ�����Uri�л�á����Ϊ�գ���ô���Ǿͽ�������ķ�ʽ��ȡ  
	                if (mImageCaptureUri != null) 
	                {
                        //��������Ǹ���Uri��ȡBitmapͼƬ�ľ�̬����  
                        image = MediaStore.Images.Media.getBitmap(activity_.getContentResolver(), mImageCaptureUri);
	                }
	                else 
	                {  
	                    Bundle extras = data.getExtras();  
	                    if(extras != null) 
	                    {  
	                        //��������Щ���պ��ͼƬ��ֱ�Ӵ�ŵ�Bundle�е��������ǿ��Դ��������ȡBitmapͼƬ  
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
	                    //��������Щ���պ��ͼƬ��ֱ�Ӵ�ŵ�Bundle�е��������ǿ��Դ��������ȡBitmapͼƬ  
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
	                //ȡ�÷��ص�Uri,������ѡ����Ƭ��ʱ�򷵻ص�����Uri��ʽ���������������еû�����Uri�ǿյģ�����Ҫ�ر�ע��  
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
