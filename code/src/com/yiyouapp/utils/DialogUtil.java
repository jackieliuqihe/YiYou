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

	// ����
	public static final int SELECT_CAMERA = 0;
	// ѡͼ
	public static final int SELECT_PICTURE = 1;

	// ������Ʒ�ϴ���ʽѡ���
	public static void showPhotoSelectDialog(final Activity activity)
	{
		AlertDialog photo_dialog = new AlertDialog.Builder(activity)
		.setItems(new String[] {"����","���ֻ����ѡ��"}, new DialogInterface.OnClickListener(){

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

	// ��ʾ�������Ի���
	public static void showNetErrorDialog(IRetry retry)
	{
		if(netErrorDialog == null || !netErrorDialog.isShowing())
		{
			retry_ = retry;
			CustomAlertDialog.Builder builder = new CustomAlertDialog.Builder(AppConfig.current_activity)
			.setMessage("�ܱ�Ǹ����ǰ������������ʱ");
			
			if(retry_ != null)
			{
				builder.setPositiveButton("����" , new DialogInterface.OnClickListener(){
		
					@Override
					public void onClick(DialogInterface dialog,int id) {
		
						if(retry_ != null)
						{
							AppConfig.current_activity.showProgress();
							retry_.retry();
						}
					}
				
				})  
				.setNegativeButton("ȡ��" , null);
			}
			else
			{
				builder.setNegativeButton("ȷ��" , null);
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

	// ��ʾ�������Ի���
	public static void showAppErrorDialog(String err_desc)
	{
		if(AppConfig.current_activity != null)
		{
			new CustomAlertDialog.Builder(AppConfig.current_activity)
			.setPositiveButton("ȷ��" , new DialogInterface.OnClickListener(){
				
				@Override
				public void onClick(DialogInterface dialog,int id) {
	
					System.exit(-1);
					
				}
			
			})
			.setMessage(err_desc).show();
		}
	}

}
