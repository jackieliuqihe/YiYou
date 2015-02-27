package com.yiyouapp.utils;

import com.yiyouapp.AppConfig;
import com.yiyouapp.controls.CustomAlertDialog;
import com.yiyouapp.controls.PhotoObtainer;
import com.yiyouapp.proxy.IRetry;
import com.yiyouapp.proxy.NetTextProxy;
import com.yiyouapp.proxy.ReqDataProxy;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.graphics.Bitmap;

public class DialogUtil {

	// 照相
	public static final int SELECT_CAMERA = 0;
	// 选图
	public static final int SELECT_PICTURE = 1;

	// 弹出作品上传方式选择框
	public static void showPhotoSelectDialog(final Activity activity)
	{
		AlertDialog photo_dialog = new AlertDialog.Builder(activity)
		.setItems(new String[] {"拍照","从手机相册选择"}, new DialogInterface.OnClickListener(){

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				
				switch(which)
				{
				case DialogUtil.SELECT_CAMERA:
					PhotoObtainer.doTakePhoto(activity);
					break;
				case DialogUtil.SELECT_PICTURE:
					PhotoObtainer.doGetPicture(activity);
					break;
				}
			}
			
		})
		.create();
		
		photo_dialog.show();
		photo_dialog.setCanceledOnTouchOutside(true);
	}
	
	private static AlertDialog netErrorDialog = null;
	private static IRetry retry_ = null;

	// 显示网络错误对话框
	public static void showNetErrorDialog(IRetry retry)
	{
		if(netErrorDialog == null || !netErrorDialog.isShowing())
		{
			retry_ = retry;
			CustomAlertDialog.Builder builder = new CustomAlertDialog.Builder(AppConfig.current_activity)
			.setMessage("很抱歉，当前您的网络请求超时");
			
			if(retry_ != null)
			{
				builder.setPositiveButton("重试" , new DialogInterface.OnClickListener(){
		
					@Override
					public void onClick(DialogInterface dialog,int id) {
		
						if(retry_ != null)
						{
							AppConfig.current_activity.showProgress();
							retry_.retry();
						}
					}
				
				})  
				.setNegativeButton("取消" , null);
			}
			else
			{
				builder.setNegativeButton("确定" , null);
			}
			
			netErrorDialog = builder.show();
			
			netErrorDialog.setOnDismissListener(new OnDismissListener(){

				@Override
				public void onDismiss(DialogInterface arg0) {
					netErrorDialog = null;
				}
				
			});
		}
	}

	// 显示网络错误对话框
	public static void showAppErrorDialog(String err_desc)
	{
		if(AppConfig.current_activity != null)
		{
			new CustomAlertDialog.Builder(AppConfig.current_activity)
			.setPositiveButton("确定" , new DialogInterface.OnClickListener(){
				
				@Override
				public void onClick(DialogInterface dialog,int id) {
	
					System.exit(-1);
					
				}
			
			})
			.setMessage(err_desc).show();
		}
	}

}
