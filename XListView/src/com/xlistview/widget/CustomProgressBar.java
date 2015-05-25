package com.xlistview.widget;

import com.nineoldandroids.animation.ValueAnimator;
import com.nineoldandroids.animation.ValueAnimator.AnimatorUpdateListener;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class CustomProgressBar extends View {
	static final int[] colors = new int[] {0xff0a8f08,0xff259b24,0xff2baf2b,0xff42bd41,0xff72d572,0xff42bd41,0xff2baf2b};

	int index = 0;
	float progress = 0.0f;

	Paint mPaint;
	boolean isAnimating;
	public CustomProgressBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub

		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setStyle(Style.FILL);
	}

	@Override
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (index > 0) {
			canvas.drawColor(colors[index - 1]);
		}else{
			canvas.drawColor(colors[colors.length-1]);
		}

		if(index>=colors.length){
			index = 0;
		}
		
		mPaint.setColor(colors[index]);
		
		canvas.drawRect(0, 0, getWidth() * progress, getBottom(),
				mPaint);
	}

	public void animating() {
		if(isAnimating){
			ValueAnimator animator = ValueAnimator.ofFloat(0, 1.0f);
			animator.setDuration(600);
			animator.addUpdateListener(new AnimatorUpdateListener() {

				@Override
				public void onAnimationUpdate(ValueAnimator animation) {
					// TODO Auto-generated method stub
					float frameValue = (Float) animation.getAnimatedValue();

					progress = frameValue;

					mHandler.post(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							invalidate();
						}

					});

					if (frameValue==1.0) {
						index++;
						animating();
					}
				}
			});
			animator.start();
		}
	}

	public void startAnimating(){
		if(!isAnimating){
			isAnimating = true;
			animating();
		}
	}
	
	public void stopAnimating(){
		isAnimating = false;
		clearAnimation();
		index = 0;
		progress = 0;
	}
	
	private Handler mHandler = new Handler(Looper.getMainLooper());
}
