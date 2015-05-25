package com.xlistview.sample;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nineoldandroids.animation.ObjectAnimator;
import com.xlistview.EventTrigger;
import com.xlistview.R;
import com.xlistview.XListView;
import com.xlistview.XListView.LoadMoreListener;
import com.xlistview.XListView.PullRefreshListener;
import com.xlistview.widget.CustomProgressBar;

public class PLActivity extends Activity{
	Context mContext;
	XListView listView;
	
	int count = 0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pl);
		mContext = this;
		
		listView = (XListView) findViewById(R.id.listView);
		listView.enablePullRefresh(true);
		listView.setPullHeaderMode(XListView.PULL_HEADER_MODE_MOVE);
		listView.setEventTrigger(new PLEventTrigger());
		listView.setAdapter(mAdapter);
		
		listView.enableLoadMore(true);
		listView.enableAutoLoadMore(false);
		listView.setPullRefreshListener(new PullRefreshListener(){

			@Override
			public void onRefresh(View headerView) {
				// TODO Auto-generated method stub
				new RefreshTask().execute();
			}
		});
		
		listView.setLoadMoreListener(new LoadMoreListener(){

			@Override
			public void onLoadMore(View moreView) {
				// TODO Auto-generated method stub
				new LoadMoreTask().execute();
			}
		});
		
	}
	
	BaseAdapter mAdapter = new BaseAdapter(){

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return count;
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
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			TextView textView = (TextView) convertView;
			if(null==textView){
				textView = new TextView(mContext);
				textView.setLayoutParams(new AbsListView.LayoutParams(LayoutParams.MATCH_PARENT,Utils.dp2px(mContext, 40)));
			}
			
			textView.setText(""+position);
			textView.setTextColor(0xFF388E3C);
			textView.setGravity(Gravity.CENTER_VERTICAL);
			textView.setPadding(Utils.dp2px(mContext, 20), 0, 0, 0);
			textView.setBackgroundResource(R.drawable.selector_list_item);
			
			textView.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					
				}
				
			});
			return textView;
		}
		
	};
	
	class RefreshTask extends AsyncTask<Void, Void, String[]>{

		@Override
		protected String[] doInBackground(Void... params) {
			// Simulates a background job.
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(String[] result) {
			//mListItems.addFirst("Added after refresh...");
			super.onPostExecute(result);
			count = 20;
			mAdapter.notifyDataSetChanged();
			listView.pullUp();
			listView.enableLoadMore(true);
		}

		
	};
	
	class LoadMoreTask extends AsyncTask<Void, Void, String[]>{

		@Override
		protected String[] doInBackground(Void... params) {
			// Simulates a background job.
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(String[] result) {
			//mListItems.addFirst("Added after refresh...");
			super.onPostExecute(result);
			count += 10;
			if(count>50){
				listView.enableLoadMore(false);
			}
			mAdapter.notifyDataSetChanged();
			listView.completeLoadMore();
		}

		
	};
	
}
