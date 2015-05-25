package com.xlistview;

import android.view.View;
import android.widget.ListAdapter;

import com.xlistview.XListView.FixBarListener;
import com.xlistview.XListView.InnerListView;
import com.xlistview.XListView.LoadMoreListener;
import com.xlistview.XListView.PullRefreshListener;

public interface IXListView {
	public void pullUp();

	public void autoRefresh();

	public void enableSwipe(boolean enable);

	public void enableAutoCloseSwipe(boolean auto);

	public void enablePullRefresh(boolean enable);

	public void enableLoadMore(boolean enable);

	public void enableAutoLoadMore(boolean enable);

	public void enableFixBar(boolean enable);
	
	public boolean isEnableSwipe();

	public boolean isEnableAutoCloseSwipe();

	public boolean isEnablePullRefresh();

	public boolean isEnableLoadMore();

	public boolean isEnableAutoLoadMore();
	
	public boolean isEnableFixBar();

	public int getPullRefreshState();

	public void completeLoadMore();

	public int getLoadMoreState();

	public void setLoadMoreListener(LoadMoreListener loadMoreListener);

	public void setPullRefreshListener(PullRefreshListener pullRefreshListener);
	
	public void setFixBarListener(FixBarListener fixBarListener);
	
	public ListAdapter getListAdapter();
	
	public int getPullHeaderMode();
	
	public void setPullHeaderMode(int pullHeaderMode);
	
	public InnerListView getListView();
	
	public int getFixBarMinHeight();
	
	public void setFixBarMinHeight(int fixBarMinHeight);
	
	public int getFixBarMode();
	
	public void setFixBarMode(int fixBarMode);
	
	public View getFixBar();
	
	public View getMoreView();
	
	public View getPullHeaderView();
	
	public EventTrigger getEventTrigger();
	
	public void setEventTrigger(EventTrigger eventTrigger);
}
