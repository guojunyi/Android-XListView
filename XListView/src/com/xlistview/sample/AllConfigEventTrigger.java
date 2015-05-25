package com.xlistview.sample;

import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.xlistview.EventTrigger;
import com.xlistview.R;
import com.xlistview.XListView;

public class AllConfigEventTrigger extends EventTrigger{

	@Override
	protected int getOnHeaderViewRefreshingHeight(View pullHeaderView) {
		// TODO Auto-generated method stub
		return pullHeaderView.getMeasuredHeight();
	}

	@Override
	protected void onHeaderViewHeightChange(View pullHeaderView, int pullHeight) {
		// TODO Auto-generated method stub
		final TextView textView = (TextView) pullHeaderView
				.findViewById(R.id.textView);
		ProgressBar progressBar = (ProgressBar) pullHeaderView
				.findViewById(R.id.progressBar);

		TextView text1 = (TextView) pullHeaderView
				.findViewById(R.id.textView1);
		TextView text2 = (TextView) pullHeaderView
				.findViewById(R.id.textView2);
		
		
		text1.setText("" + pullHeaderView.getBottom());
		text2.setText("" + pullHeight);

		if (getXListView().getPullRefreshState() != XListView.STATE_REFRESH) {
			progressBar.setVisibility(View.GONE);
			if (pullHeight > pullHeaderView.getHeight()) {
				textView.setText("Release to refresh.");
			} else {
				textView.setText("Pull to refresh.");
			}
		}
	}

	@Override
	protected boolean onPullHeaderTouchUp(View pullHeaderView, int pullHeight) {
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
	protected void onMoreViewHeightChange(View moreView, int pullHeight) {
		// TODO Auto-generated method stub
		TextView textHeight = (TextView) moreView
				.findViewById(R.id.textHeight);
		textHeight.setText("" + pullHeight);
		if (getXListView().getLoadMoreState() != XListView.MORE_STATE_LOADING) {

			TextView textView = (TextView) moreView
					.findViewById(R.id.textView);
			if (pullHeight > moreView.getMeasuredHeight()) {
				textView.setText("Release to loading.");
			} else {
				textView.setText("Pull up load more.");

			}
		}
	}

	@Override
	protected void onCompleteLoadMore(View moreView) {
		// TODO Auto-generated method stub
		ProgressBar progressBar = (ProgressBar) moreView
				.findViewById(R.id.progressBar);
		TextView textView = (TextView) moreView
				.findViewById(R.id.textView);
		progressBar.setVisibility(View.GONE);
		textView.setText("Pull up load more.");
	}

	@Override
	protected void onLoadMore(View moreView) {
		// TODO Auto-generated method stub
		ProgressBar progressBar = (ProgressBar) moreView
				.findViewById(R.id.progressBar);
		TextView textView = (TextView) moreView
				.findViewById(R.id.textView);
		progressBar.setVisibility(View.VISIBLE);
		textView.setText("Loading...");
	}

	@Override
	protected void onRefresh(View pullHeaderView) {
		// TODO Auto-generated method stub
		ProgressBar progressBar = (ProgressBar) pullHeaderView
				.findViewById(R.id.progressBar);
		progressBar.setVisibility(View.VISIBLE);
	}

}
