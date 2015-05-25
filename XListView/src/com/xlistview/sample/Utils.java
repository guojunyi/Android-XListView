package com.xlistview.sample;

import android.content.Context;
import android.util.TypedValue;

public class Utils {
	public static int dp2px(Context context, int dp) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
				context.getResources().getDisplayMetrics());
	}

	public static int px2dp(Context context,float pxValue){
		final float scale = context.getResources().getDisplayMetrics().density;  
	    return (int) (pxValue / scale + 0.5f); 
	}
}
