package com.yiyouapp.utils;

import java.util.HashMap;

import com.yiyouapp.AppConfig;
import com.yiyouapp.R;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.ImageView.ScaleType;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;
import android.view.View;
import android.view.View.OnClickListener;

public class EmotionUtil {

	// 表情缓存
	private static HashMap<Integer, Drawable> emotionCache;
	
	// 创建表情的排版
	public static void createEmotionLayout(TableLayout tl, final EditText et)
	{
		TableRow row = null;
		ImageView iv = null;
		
		final Context ctx = tl.getContext();
		Resources res = ctx.getResources();
		
		OnClickListener listener = new OnClickListener()
		{

			@Override
			public void onClick(View v) {
				insertEmotion(ctx, et, (Integer)v.getTag());
			}
			
		};
		
		int size = SizeUtil.dp2px(32);
		int topMargin = SizeUtil.dp2px(10);
		int bottomMargin = SizeUtil.dp2px(10);

		for(int i = 0; i < 93; i++)
		{
			if(i % 7 == 0)
			{
				row = new TableRow(tl.getContext());

				tl.addView(row);
			}

			iv = new ImageView(ctx);

			row.addView(iv);

			int resId = res.getIdentifier("bq" + (i + 1), "drawable", ctx.getPackageName());

			iv.setScaleType(ScaleType.CENTER_INSIDE);
			iv.setImageResource(resId);
			
			TableRow.LayoutParams params = (LayoutParams) iv.getLayoutParams();//new TableRow.LayoutParams();
			
			params.width = size;
			params.height = size;
			params.topMargin = topMargin;
			params.bottomMargin = bottomMargin;
			
			iv.setLayoutParams(params);
			
			iv.setOnClickListener(listener);
			iv.setTag(i + 1);
		}
	}
	
	// 插入表情到edit text
	public static void insertEmotion(Context ctx, EditText et, int id)
	{
		Editable eb = et.getEditableText();
		
		//获得光标所在位置
		int pos = et.getSelectionStart();
		
		SpannableString ss = new SpannableString("/" + id);

		Drawable drawable;
		
		try
		{
			//定义插入图片
			//drawable = ctx.getResources().getDrawable(resId);
			drawable = getSetEmotionCache(ctx, id);
		}
		catch(Exception e)
		{
			return;
		}
		
		ss.setSpan(new ImageSpan(drawable), 0, ss.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		
		drawable.setBounds(2, 0, drawable.getIntrinsicWidth() + 20, drawable.getIntrinsicHeight() + 20);
		
		//插入图片
		eb.insert(pos, ss);
	}

	// 显示表情到text view
	public static void showEmotion(Context ctx, TextView tv, String value)
	{
		String sarr[] = value.split("/");

		tv.setText(sarr[0]);
		for(int i = 1; i < sarr.length; i++)
		{
			if(sarr[i].length() > 0)
			{
				int id = 0;
				if(Character.isDigit(sarr[i].charAt(0)))
				{
					if(sarr[i].length() > 1)
					{
						if(Character.isDigit(sarr[i].charAt(1)))
						{
							id = Integer.parseInt(sarr[i].substring(0, 2));
						}
						else
						{
							id = Integer.parseInt(sarr[i].substring(0, 1));
						}
					}
					else
					{
						id = Integer.parseInt(sarr[i].substring(0, 1));
					}
				}
				else
				{
					tv.append("/" + sarr[i]);
					continue;
				}
				
				if(id < 1 || id > 93)
				{
					tv.append("/" + sarr[i]);
					continue;
				}
				
				Drawable drawable;
				
				try
				{
					//定义插入图片
					//drawable = ctx.getResources().getDrawable(resId);
					drawable = getSetEmotionCache(ctx, id);
				}
				catch(Exception e)
				{
					tv.append("/" + sarr[i]);
					continue;
				}
				
		        ImageSpan imgSpan = new ImageSpan(drawable);//, ImageSpan.ALIGN_BASELINE);
		        
		        SpannableString spanString = new SpannableString("/" + id);
		        
		        spanString.setSpan(imgSpan, 0, spanString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		        
		        tv.append(spanString);
		        
		        int l = String.valueOf(id).length();
		        tv.append(sarr[i].substring(l));
			}
		}

	}

	// 显示表情的替代
	public static String getEmotionSubstitude(Context ctx, String value)
	{
		String sarr[] = value.split("/");
		String result;

		result = sarr[0];
		for(int i = 1; i < sarr.length; i++)
		{
			if(sarr[i].length() > 0)
			{
				int id = 0;
				if(Character.isDigit(sarr[i].charAt(0)))
				{
					if(sarr[i].length() > 1)
					{
						if(Character.isDigit(sarr[i].charAt(1)))
						{
							id = Integer.parseInt(sarr[i].substring(0, 2));
						}
						else
						{
							id = Integer.parseInt(sarr[i].substring(0, 1));
						}
					}
					else
					{
						id = Integer.parseInt(sarr[i].substring(0, 1));
					}
				}
				else
				{
					result += "/" + sarr[i];
					continue;
				}
				
				if(id < 1 || id > 93)
				{
					result += "/" + sarr[i];
					continue;
				}
				
				Drawable drawable;
				
				try
				{
					//定义插入图片
					//drawable = ctx.getResources().getDrawable(resId);
					drawable = getSetEmotionCache(ctx, id);
				}
				catch(Exception e)
				{
					result += "/" + sarr[i];
					continue;
				}

		        int l = String.valueOf(id).length();
				result += "[表情]" + sarr[i].substring(l);
			}
		}

		return result;
	}
	
	// 获取drawable，和缓存表情
	public static Drawable getSetEmotionCache(Context ctx, int id)
	{
		if(emotionCache == null)
			emotionCache = new HashMap<Integer, Drawable>();
		
		Drawable drawable;
		
		if(emotionCache.containsKey(id))
		{
			drawable = emotionCache.get(id);
		}
		else
		{
			int resId = ctx.getResources().getIdentifier("bq" + id, "drawable", ctx.getPackageName());
			
			int size = AppConfig.emotion_size;
			
			Bitmap bmp = BitmapFactory.decodeResource(ctx.getResources(), resId);
			
			drawable = new BitmapDrawable(ImageUtil.scaleImage2Width(bmp, size, size));
			drawable.setBounds(2, 0, drawable.getIntrinsicWidth() + 20, drawable.getIntrinsicHeight() + 20);

			emotionCache.put(id, drawable);
		}
		
		return drawable;
	}
}
