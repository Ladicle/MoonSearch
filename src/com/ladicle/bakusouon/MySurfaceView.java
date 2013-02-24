package com.ladicle.bakusouon;

import com.ladicle.util.Log;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class MySurfaceView extends SurfaceView implements
		SurfaceHolder.Callback {
	private static final Log log = new Log("Surfaceview");

	private SurfaceHolder holder;
	private Canvas canvas;
	private int xNum, yNum;
	private float wBox, hBox;
	private Paint paint;

	public MySurfaceView(Context context, int xNum, int yNum) {
		super(context);
		this.xNum = xNum;
		this.yNum = yNum;
		init();
		log.i("const");
	}

	public MySurfaceView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public MySurfaceView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public void init() {
		holder = getHolder();
		holder.addCallback(this);
		setFocusable(true);
		requestFocus();
		paint = new Paint();
		paint.setColor(Color.WHITE);
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		log.i("change");
		
		log.i("widht:"+width+", height:"+height);
		
		wBox = (float)(width / xNum);
		hBox = (float)(height / yNum);
		
		draw(0, 0, 0);
		draw(xNum-1, yNum-1, 0);
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		log.i("create");
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		log.i("destoroy");
	}

	public void draw(int x, int y, int color) {
		canvas = holder.lockCanvas();
		if (canvas == null) {
			System.out.println("canvas is null.");
			return;
		}

		float px = wBox * x;
		float py = hBox * y;
		RectF rect = new RectF(px, py, px + wBox, py + hBox);
		canvas.drawRect(rect, paint);
		holder.unlockCanvasAndPost(canvas);

		log.i("x:" + x + ", y:" + y + ", wBox:" + wBox + ", hBox:" + hBox
				+ ", px:" + px + ", py:" + py);
	}
}
