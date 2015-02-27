package com.yiyouapp.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;

import com.yiyouapp.AppConfig;
import com.yiyouapp.R;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.widget.ImageView;

public class AccountUtil {

	// �����˻�ͷ��
	public static void setAvatar(ImageView avatarView)
	{
		String path = getCurrAccountDir() + AppConfig.FILENAME_AVATAR;
		File file = new File(path);
		
		if(file.exists() && file.isFile())
		{
			avatarView.setImageBitmap(null);
			avatarView.setImageURI(Uri.fromFile(file));
		}
		else
		{
			avatarView.setImageResource(R.drawable.default_avatar_100);
		}
	}
	
	// �����˻�ͷ��
	public static void saveAvatar(Bitmap avatar)
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		avatar.compress(CompressFormat.JPEG, 70, baos);
    	
		SDCardUtil.writeFile(getCurrAccountDir() + AppConfig.FILENAME_AVATAR, baos.toByteArray());
	}
	
	// ��ȡ��ǰ�˻�Ŀ¼
	public static String getCurrAccountDir()
	{
		return AppConfig.account_dir + AppConfig.settings.getMobile() + "/";
	}
	
	// ������ǰ�˻�Ŀ¼
	public static boolean makeCurrAccountDir()
	{
		String path = getCurrAccountDir();
		File file = new File(path);

		if(file.isDirectory())
			return true;
		
		if(!file.exists())
		{
			return file.mkdirs();
		}
		
		if(file.isFile())
		{
			File newFile = new File(path + "." + System.currentTimeMillis());
			if(file.renameTo(newFile))
			{
				file = new File(path);
				return file.mkdirs();
			}
		}
		
		return false;
	}
}
