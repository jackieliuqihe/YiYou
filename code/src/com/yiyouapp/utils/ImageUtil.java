package com.yiyouapp.utils;

import java.io.File;

import com.yiyouapp.AppConfig;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.widget.ImageView;

public class ImageUtil {

	public static Bitmap decodingBytes(byte[] data)
    {
    	Bitmap bitmap = null;
    	
		try
		{
			BitmapFactory.Options opts = new BitmapFactory.Options();
		    // 设置inJustDecodeBounds为true
		    opts.inJustDecodeBounds = true;
		    
			BitmapFactory.decodeByteArray(data, 0, data.length, opts);
			if(opts.outWidth > AppConfig.max_image_width ||
				opts.outHeight > AppConfig.max_image_height)
			{
				float ix = (float)opts.outWidth / (float)AppConfig.max_image_width;
				float iy = (float)opts.outHeight / (float)AppConfig.max_image_height;
				
				int sx = (int)ix + 1;
				int sy = (int)iy + 1;
				
				if(sy > sx)
					sx = sy;

				opts.inSampleSize = sx;
			}

		    opts.inJustDecodeBounds = false;
			bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, opts);
		} catch (Exception e) {
        }
		
		return bitmap;
    }
    
	public static Bitmap scaleImage(Bitmap bmp, float scaleX, float scaleY)
	{
		// 获得图片的宽高
	    int width = bmp.getWidth();
	    int height = bmp.getHeight();
	    
	    // 取得想要缩放的matrix参数
	    Matrix matrix = new Matrix();
	    matrix.postScale(scaleX, scaleY);

		return Bitmap.createBitmap(bmp, 0, 0, width, height, matrix, true);
	}

	public static Bitmap scaleImage2Width(Bitmap bmp, int width, int height)
	{
		int w = bmp.getWidth();
		int h = bmp.getHeight();
		
	    // 取得想要缩放的matrix参数
	    Matrix matrix = new Matrix();
	    matrix.postScale((float)width / (float)w, (float)height / (float)h);

		return Bitmap.createBitmap(bmp, 0, 0, w, h, matrix, true);
	}
	
	public static boolean setImageView(ImageView iv, String path)
	{
		File file = new File(path);
		if(file.exists() && file.isFile())
		{
			iv.setImageBitmap(null);
			iv.setImageURI(Uri.fromFile(file));
			
			return true;
		}
		
		return false;
	}
}
