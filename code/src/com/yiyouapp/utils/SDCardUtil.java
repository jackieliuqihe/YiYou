package com.yiyouapp.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Bitmap.CompressFormat;
import android.os.Environment;
import android.os.StatFs;

public class SDCardUtil {

	public static String createDir(String path)
	{
		File file = new File(path);
		if(file.exists())
			return path;
		
		if(file.mkdirs())
			return path;
		
		return null;
	}
	
	public static void deleteFile(String fileName)
	{
		File file = new File(fileName);
		if(file.exists() && file.isFile())
			file.delete();
	}
	
	public static boolean writeFile(String fileName, byte[] bytes)
	{
		try {
			File file = new File(fileName);  
			FileOutputStream fos;
			
			fos = new FileOutputStream(file);
			fos.write(bytes); 
			fos.close();
			
		} catch (Exception e) {
			return false;
		}  

		return true; 
	}

	public static byte[] readFile(String fileName)
	{
		byte[] bytes = null;
		
		try {
			File file = new File(fileName);  
			FileInputStream fis = new FileInputStream(file);
				
			int length = fis.available(); 

			bytes = new byte[length]; 
			fis.read(bytes);     

			fis.close();
			
		} catch (Exception e) {
			return null;
		}  

		return bytes; 
	}

	public static boolean copyFile(String srcFileName, String destFileName, boolean reWrite) 
	{
	    File srcFile = new File(srcFileName);
	    File destFile = new File(destFileName);
	    if(!srcFile.exists())
	        return false;
	
	    if(!srcFile.isFile())
	        return false;
	
	    if(!srcFile.canRead())
	        return false;
	
	    if(destFile.exists() && reWrite)
	        destFile.delete();
	 
	    try {
	        InputStream inStream = new FileInputStream(srcFile);
	        FileOutputStream outStream = new FileOutputStream(destFile);
	        byte[] buf = new byte[1024];
	        int byteRead = 0;
	        while ((byteRead = inStream.read(buf)) != -1) {
	            outStream.write(buf, 0, byteRead);
	        }
	        outStream.flush();
	        outStream.close();
	        inStream.close();
	    }
	    catch (Exception e) {
	        return false;
	    }
	 
	    return true;
	}

	public static boolean savePicture(Bitmap bitmap, String fileName) 
	{
        if(bitmap == null)
        {
            return false;  
        }
        else 
        {  
            File destFile = new File(fileName);  
            OutputStream os = null;  
            try
            {
                os = new FileOutputStream(destFile);
                bitmap.compress(CompressFormat.JPEG, 100, os);
                os.flush();
                os.close();
            } 
            catch (IOException e) 
            {
            	return false;
            }
            
            return true;
        }
    }
	
	public static boolean isFileExist(String fileName)
	{
		return new File(fileName).exists(); 
	}
	
	@SuppressWarnings("deprecation")
	public static long getFreeSpace() 
	{ 
        if(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) 
        { 
            File sdcardDir = Environment.getExternalStorageDirectory(); 
            StatFs stat = new StatFs(sdcardDir.getPath());
            
            return stat.getAvailableBlocks() * stat.getBlockSize(); 
        }
        
        return 0;
    } 
}
