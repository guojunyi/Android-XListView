package com.xlistview;

import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

public class DefaultEventTrigger extends EventTrigger{
	
	@Override
	public int getOnHeaderViewRefreshingHeight(View pullHeaderView) {
		// TODO Auto-generated method stub
		return pullHeaderView.getMeasuredHeight();
	}

	@Override
	public boolean onPullHeaderTouchUp(View pullHeaderView, int pullHeight) {
		// TODO Auto-generated method stub
		if(pullHeight>=pullHeaderView.getMeasuredHeight()){
			return true;
		}
		
		return false;
	}

	
	@Override
	protected boolean onMoreTouchUp(View moreView, int pullHeight) {
		// TODO Auto-generated method stub
		if(pullHeight>moreView.getMeasuredHeight()){
			return true;
		}
		return false;
	}

	@Override
	protected void onHeaderViewHeightChange(View pullHeaderView, int pullHeight) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onMoreViewHeightChange(View moreView, int pullHeight) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onCompleteLoadMore(View moreView) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onLoadMore(View moreView) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onRefresh(View pullHeaderView) {
		// TODO Auto-generated method stub
		
	}



	

}
