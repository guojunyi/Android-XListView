package com.xlistview;

import android.view.View;

public abstract class EventTrigger {
	private XListView listView;
	
	protected abstract int getOnHeaderViewRefreshingHeight(View pullHeaderView);
	
	protected abstract void onHeaderViewHeightChange(View pullHeaderView,int pullHeight);
	
	protected abstract boolean onPullHeaderTouchUp(View pullHeaderView,int pullHeight);

	protected abstract boolean onMoreTouchUp(View moreView,int pullHeight);
	
	protected abstract void onMoreViewHeightChange(View moreView,int pullHeight);
	
	protected abstract void onCompleteLoadMore(View moreView);
	
	protected abstract void onLoadMore(View moreView);
	
	protected abstract void onRefresh(View pullHeaderView);
	
	protected XListView getXListView() {
		return listView;
	}

	protected void setXListView(XListView listView) {
		this.listView = listView;
	}
	
	
}
