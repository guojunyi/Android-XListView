package com.xlistview.sample;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nineoldandroids.animation.ObjectAnimator;
import com.xlistview.EventTrigger;
import com.xlistview.R;
import com.xlistview.XListView;
import com.xlistview.widget.CustomProgressBar;

public class PLEventTrigger extends EventTrigger{
	boolean isRotated;
	
	@Override
	protected int getOnHeaderViewRefreshingHeight(View pullHeaderView) {
		// TODO Auto-generated method stub
		CustomProgressBar progressBar = (CustomProgressBar) pullHeaderView.findViewById(R.id.progressBar);
		return progressBar.getHeight();
	}

	@Override
	protected boolean onPullHeaderTouchUp(View pullHeaderView,
			int pullHeight) {
		// TODO Auto-generated method stub
		if(pullHeight>pullHeaderView.getHeight()){
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
	protected void onHeaderViewHeightChange(View pullHeaderView,
			int pullHeight) {
		// TODO Auto-generated method stub
		final TextView textView = (TextView) pullHeaderView
				.findViewById(R.id.textView);
		ImageView imageArrow = (ImageView) pullHeaderView.findViewById(R.id.imageArrow);
		final CustomProgressBar progressBar = (CustomProgressBar) pullHeaderView.findViewById(R.id.progressBar);
		
		if (getXListView().getPullRefreshState() != XListView.STATE_REFRESH) {
			if (pullHeight > pullHeaderView.getHeight()) {
				textView.setText("Release to refresh.");
				if(!isRotated){
					isRotated = true;
					ObjectAnimator.ofFloat(imageArrow, "rotation", 0, 180).setDuration(200).start();
				}
			} else {
				textView.setText("Pull to refresh.");
				if(isRotated){
					isRotated = false;
					ObjectAnimator.ofFloat(imageArrow, "rotation", 180, 0).setDuration(200).start();
				}
			}
			textView.setVisibility(View.VISIBLE);
			imageArrow.setVisibility(View.VISIBLE);
			progressBar.setVisibility(View.INVISIBLE);
			progressBar.stopAnimating();
		}else{
			if(pullHeight==progressBar.getHeight()){
				imageArrow.clearAnimation();
				textView.setVisibility(View.GONE);
				imageArrow.setVisibility(View.GONE);
				progressBar.setVisibility(View.VISIBLE);
				progressBar.startAnimating();
			}
		}
	}

	@Override
	protected void onMoreViewHeightChange(View moreView, int pullHeight) {
		// TODO Auto-generated method stub
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
		CustomProgressBar progressBar = (CustomProgressBar) moreView.findViewById(R.id.progressBar);
		progressBar.setVisibility(View.GONE);
		progressBar.stopAnimating();
		
		TextView textView = (TextView) moreView
				.findViewById(R.id.textView);
		textView.setText("Pull up load more.");
	}

	@Override
	protected void onLoadMore(View moreView) {
		// TODO Auto-generated method stub
		TextView textView = (TextView) moreView
				.findViewById(R.id.textView);
		textView.setText("Loading...");
		
		CustomProgressBar progressBar = (CustomProgressBar) moreView.findViewById(R.id.progressBar);
		progressBar.setVisibility(View.VISIBLE);
		progressBar.startAnimating();
	}

	@Override
	protected void onRefresh(View pullHeaderView) {
		// TODO Auto-generated method stub
		
	}

}
