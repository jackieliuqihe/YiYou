package com.yiyouapp.controls;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

public class RadiusImageView extends ImageView {

	public RadiusImageView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public RadiusImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public RadiusImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		/*
        RectF rect = new RectF(0, 0, getWidth(), getHeight());

		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		
		Bitmap output = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
		Canvas newCanvas = new Canvas(output);

		paint.setColor(0xFFFFFF);
		paint.setAlpha(0xFF);
		newCanvas.drawRoundRect(rect, 40, 40, paint);
		newCanvas.saveLayer(rect, paint, Canvas.ALL_SAVE_FLAG);
        //newCanvas.save(Canvas.ALL_SAVE_FLAG);
		newCanvas.restore();
		
		//super.onDraw(newCanvas);
		paint.setXfermode(new PorterDuffXfermode(Mode.SCREEN));
		
		canvas.drawBitmap(output, 0, 0, paint);*/
		super.onDraw(canvas);
	}

}
