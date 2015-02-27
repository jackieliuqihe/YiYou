package com.yiyouapp.controls.cache;

import java.lang.ref.SoftReference;
import java.util.HashMap;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

public class ImageMemCache {

	 private HashMap<String, SoftReference<Bitmap>> imageCache;
  
     public ImageMemCache() 
     {
    	 imageCache = new HashMap<String, SoftReference<Bitmap>>();
     }

     public Bitmap getImage(String key) 
     {
         if (imageCache.containsKey(key)) {
             SoftReference<Bitmap> softReference = imageCache.get(key);
             Bitmap bitmap = softReference.get();
             return bitmap;
         }

         return null;
     }
  
     public void setImage(String key, Bitmap bitmap)
     {
    	 if(bitmap != null)
    		 imageCache.put(key, new SoftReference<Bitmap>(bitmap));
    	 else
    		 imageCache.remove(key);
     }
}
