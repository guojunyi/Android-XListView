package com.xlistview.sample;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xlistview.BaseSwipeAdapter;
import com.xlistview.R;
import com.xlistview.SwipeLayout;
import com.xlistview.XListView;

public class SwipeActivity extends Activity{
	XListView listView;
	Context mContext;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_swipe);
		mContext = this;
		listView = (XListView) findViewById(R.id.listView);
		
		listView.enableSwipe(true);
		listView.enableAutoCloseSwipe(true);
		listView.setAdapter(mAdapter);
		
		listView.getListView().setDivider(new ColorDrawable(0xFF4CAF50));  
		listView.getListView().setDividerHeight(1);
		
	}
	
	BaseSwipeAdapter mAdapter = new BaseSwipeAdapter(){

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return 20;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public SwipeLayout getSwipeView(final int position, View convertView,
				ViewGroup parent) {
			// TODO Auto-generated method stub
			SwipeLayout swipeLayout = (SwipeLayout) convertView;
			if(null==swipeLayout){
				swipeLayout = (SwipeLayout) LayoutInflater.from(mContext).inflate(R.layout.list_item_swipe, null);
			}
			
			TextView menu1 = (TextView) swipeLayout.findViewById(R.id.menu1);
			TextView menu2 = (TextView) swipeLayout.findViewById(R.id.menu2);
			TextView textView = (TextView) swipeLayout.findViewById(R.id.textView);
			
			textView.setText(""+position);
			RelativeLayout fontView = (RelativeLayout) swipeLayout
					.findViewById(R.id.fontView);
			
			menu1.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Toast.makeText(mContext, "position:" + position + " menu1 clicked",
							1000).show();
					;
				}

			});

			menu2.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Toast.makeText(mContext, "position:" + position + " menu2 clicked",
							1000).show();
				}
			});
			
			fontView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Toast.makeText(mContext, "position:" + position + " clicked",
							1000).show();
				}
			});
			
			fontView.setOnLongClickListener(new OnLongClickListener(){

				@Override
				public boolean onLongClick(View v) {
					// TODO Auto-generated method stub
					Toast.makeText(mContext, "position:" + position + " long clicked",
							1000).show();
					return true;
				}
				
			});
			return swipeLayout;
		}
		
	};
}
