package com.xlistview;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.xlistview.SwipeLayout.SwipeLayoutListener;

public abstract class BaseSwipeAdapter extends BaseAdapter {
	SwipeLayout openedSwipeLayout;
	boolean mIsDragged;
	XListView listView;
	HashMap<Integer, SwipeLayout> openedMap = new HashMap<Integer, SwipeLayout>();

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		final SwipeLayout swipeLayout = getSwipeView(position, convertView,
				parent);
		swipeLayout.setPosition(position);
		SwipeLayout catchLayout = openedMap.get(position);
		if (null != catchLayout) {
			swipeLayout.open(false);
			openedMap.put(position, swipeLayout);
		} else {
			swipeLayout.close(false);
		}
		swipeLayout.setListView(listView);
		swipeLayout.setSwipeLayoutListener(new SwipeLayoutListener() {

			@Override
			public void onOpen() {
				// TODO Auto-generated method stub
				openedMap.put(swipeLayout.getPosition(), swipeLayout);
			}

			@Override
			public void onClose() {
				// TODO Auto-generated method stub
				openedMap.remove(position);
			}

			@Override
			public void onStartDragging() {
				// TODO Auto-generated method stub
				mIsDragged = true;
			}

			@Override
			public void onStopDragging() {
				// TODO Auto-generated method stub
				mIsDragged = false;
			}

			@Override
			public void onTouch() {
				// TODO Auto-generated method stub

			}

		});

		return swipeLayout;
	}

	public abstract SwipeLayout getSwipeView(int position, View convertView,
			ViewGroup parent);

	public void setListView(XListView listView) {
		this.listView = listView;
	}

	public boolean isDragged() {
		return this.mIsDragged;
	}

	public void closeAllSwipeLayout() {
		List<SwipeLayout> temp = new ArrayList<SwipeLayout>();
		for (Integer key : openedMap.keySet()) {
			SwipeLayout swipeLayout = openedMap.get(key);
			temp.add(swipeLayout);
		}

		for (SwipeLayout item : temp) {
			item.close(true);
		}
	}

	public boolean existOpenSwipeLayout() {
		return openedMap.size() > 0;
	}

	@Override
	public void notifyDataSetChanged() {
		// TODO Auto-generated method stub
		openedMap.clear();
		super.notifyDataSetChanged();
	}

	@Override
	public void notifyDataSetInvalidated() {
		// TODO Auto-generated method stub
		openedMap.clear();
		super.notifyDataSetInvalidated();
	}

}
