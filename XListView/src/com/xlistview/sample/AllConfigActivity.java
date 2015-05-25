package com.xlistview.sample;

import com.nineoldandroids.animation.ObjectAnimator;
import com.xlistview.BaseSwipeAdapter;
import com.xlistview.R;
import com.xlistview.SwipeLayout;
import com.xlistview.XListView;
import com.xlistview.XListView.FixBarListener;
import com.xlistview.XListView.LoadMoreListener;
import com.xlistview.XListView.PullRefreshListener;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class AllConfigActivity extends Activity{
	Context mContext;
	XListView listView;
	LayoutInflater inflater;
	boolean isFixBarAlpha;
	
	int count = 20;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_all_config);
		mContext = this;
		inflater = LayoutInflater.from(mContext);

		listView = (XListView) findViewById(R.id.listView);
		listView.setAdapter(mAdapter);
		listView.setEventTrigger(new AllConfigEventTrigger());
		listView.setPullRefreshListener(new PullRefreshListener() {

			@Override
			public void onRefresh(View headerView) {
				// TODO Auto-generated method stub
				new RefreshTask().execute();
			}


		});

		listView.setLoadMoreListener(new LoadMoreListener() {

			@Override
			public void onLoadMore(View moreView) {
				// TODO Auto-generated method stub
				new LoadMoreTask().execute();
			}


		});

		listView.setFixBarListener(new FixBarListener() {

			@Override
			public void onHeightChange(View fixBar, int height) {
				// TODO Auto-generated method stub
				
				Log.e("my",""+height);
				if (!isFixBarAlpha && height <= listView.getFixBarMinHeight()) {
					isFixBarAlpha = true;
					ObjectAnimator.ofFloat(fixBar, "alpha", 1.0f, 0.5f)
							.setDuration(500).start();
				}

				if (isFixBarAlpha && height > listView.getFixBarMinHeight()) {
					isFixBarAlpha = false;
					ObjectAnimator.ofFloat(fixBar, "alpha", 0.5f, 1.0f)
							.setDuration(500).start();
				}
			}

		});

	}

	BaseSwipeAdapter mAdapter = new BaseSwipeAdapter() {

		class ViewHolder {
			CheckBox checkBox;
			TextView menu1;
			TextView menu2;
			RelativeLayout fontView;
			TextView textView;
			LinearLayout layout1, layout2;
			SeekBar seekBar;
			TextView textLeft, textRight;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return count;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		public SwipeLayout getSwipeView(final int position, View convertView,
				ViewGroup parent) {
			// TODO Auto-generated method stub
			SwipeLayout view = (SwipeLayout) convertView;
			ViewHolder holder = null;
			if (null == view) {
				view = (SwipeLayout) inflater.inflate(R.layout.list_item_all_config, null);
				holder = new ViewHolder();
				TextView menu1 = (TextView) view.findViewById(R.id.menu1);
				TextView menu2 = (TextView) view.findViewById(R.id.menu2);
				CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkBox);
				RelativeLayout fontView = (RelativeLayout) view
						.findViewById(R.id.fontView);
				TextView textView = (TextView) view.findViewById(R.id.textView);

				LinearLayout layout1 = (LinearLayout) view
						.findViewById(R.id.layout1);
				LinearLayout layout2 = (LinearLayout) view
						.findViewById(R.id.layout2);
				TextView textLeft = (TextView) view.findViewById(R.id.textLeft);
				SeekBar seekBar = (SeekBar) view
						.findViewById(R.id.seekBar);
				holder.menu1 = menu1;
				holder.menu2 = menu2;
				holder.checkBox = checkBox;
				holder.fontView = fontView;
				holder.textView = textView;

				holder.layout1 = layout1;
				holder.layout2 = layout2;
				holder.textLeft = textLeft;
				holder.seekBar = seekBar;
				view.setTag(holder);
			} else {
				holder = (ViewHolder) view.getTag();
			}

			TextView menu1 = holder.menu1;
			TextView menu2 = holder.menu2;
			final CheckBox checkBox = holder.checkBox;
			RelativeLayout fontView = holder.fontView;
			TextView textView = holder.textView;
			final TextView textLeft = holder.textLeft;
			final TextView textRight = holder.textRight;
			checkBox.setClickable(false);

			menu1.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Toast.makeText(mContext, "position:" + position + " menu1",
							1000).show();
					;
				}

			});

			menu2.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Toast.makeText(mContext, "position:" + position + " menu2",
							1000).show();
				}
			});

			if (position == 6) {
				// enable single item swipe false;
				view.setSwipeEnable(false);
			} else {
				view.setSwipeEnable(true);
			}

			checkBox.setVisibility(View.GONE);
			holder.layout1.setVisibility(View.VISIBLE);
			holder.layout2.setVisibility(View.GONE);
			holder.seekBar.setMax(Utils.px2dp(mContext, listView.getFixBar().getMeasuredHeight()));
			holder.seekBar.setProgress(Utils.px2dp(mContext, listView.getFixBarMinHeight()));
			textLeft.setText("MinFixBarHeight:" + Utils.px2dp(mContext, listView.getFixBarMinHeight())+"dp");
			holder.seekBar
					.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

						@Override
						public void onProgressChanged(SeekBar seekBar,
								int progress, boolean fromUser) {
							// TODO Auto-generated method stub
							textLeft.setText("MinFixBarHeight:" + progress);

						}

						@Override
						public void onStartTrackingTouch(SeekBar seekBar) {
							// TODO Auto-generated method stub

						}

						@Override
						public void onStopTrackingTouch(SeekBar seekBar) {
							// TODO Auto-generated method stub
							listView.setFixBarMinHeight(Utils.dp2px(mContext, seekBar.getProgress()));
						}

					});

			switch (position) {
			case 0:
				checkBox.setVisibility(View.VISIBLE);
				checkBox.setChecked(listView.isEnablePullRefresh());
				textView.setText("Change pullRefresh enable");

				break;
			case 1:
				checkBox.setVisibility(View.VISIBLE);
				checkBox.setChecked(listView.getPullHeaderMode() == XListView.PULL_HEADER_MODE_FIX);
				textView.setText("Change pullRefresh header fix mode");
				break;
			case 2:
				textView.setText("Pull Up");
				break;
			case 3:
				textView.setText("AutoRefresh");
				break;
			case 4:
				checkBox.setVisibility(View.VISIBLE);
				checkBox.setChecked(listView.isEnableFixBar());
				textView.setText("Change fixBar enable");
				break;
			case 5:
				checkBox.setVisibility(View.VISIBLE);
				checkBox.setChecked(listView.getFixBarMode()==XListView.FIX_BAR_MODE_FIX);
				textView.setText("Change fixBar fix mode");
				break;
			case 6:
				holder.layout1.setVisibility(View.GONE);
				holder.layout2.setVisibility(View.VISIBLE);
				break;
			case 7:
				checkBox.setVisibility(View.VISIBLE);
				checkBox.setChecked(listView.isEnableSwipe());
				textView.setText("Change swipe enable");
				break;
			case 8:
				checkBox.setVisibility(View.VISIBLE);
				checkBox.setChecked(listView.isEnableAutoCloseSwipe());
				textView.setText("Change auto close swipe enable");
				break;
			case 9:
				checkBox.setVisibility(View.VISIBLE);
				checkBox.setChecked(listView.isEnableLoadMore());
				textView.setText("Change load more enable");
				break;
			case 10:
				checkBox.setVisibility(View.VISIBLE);
				checkBox.setChecked(listView.isEnableAutoLoadMore());
				textView.setText("Change auto load more enable");
				break;
			default:
				textView.setText("" + position);
			}

			fontView.setOnLongClickListener(new OnLongClickListener() {

				@Override
				public boolean onLongClick(View v) {
					// TODO Auto-generated method stub
					Toast.makeText(mContext,
							"position:" + position + " long clicked", 1000)
							.show();
					;
					return true;
				}

			});

			
			fontView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if (position == 0) {
						if (listView.isEnablePullRefresh()) {
							listView.enablePullRefresh(false);
							checkBox.setChecked(false);
						} else {
							listView.enablePullRefresh(true);
							checkBox.setChecked(true);
						}
					} else if (position == 1) {
						if (listView.getPullHeaderMode() == XListView.PULL_HEADER_MODE_FIX) {
							listView.setPullHeaderMode(XListView.PULL_HEADER_MODE_MOVE);
							checkBox.setChecked(false);
						} else {
							listView.setPullHeaderMode(XListView.PULL_HEADER_MODE_FIX);
							checkBox.setChecked(true);
						}
					} else if (position == 2) {
						listView.pullUp();

					} else if (position == 3) {
						listView.autoRefresh();
					} else if (position == 4) {
						if (listView.isEnableFixBar()) {
							listView.enableFixBar(false);
							checkBox.setChecked(false);
						} else {
							listView.enableFixBar(true);
							checkBox.setChecked(true);
						}
					} else if (position == 5) {
						if (listView.getFixBarMode() == XListView.FIX_BAR_MODE_FIX) {
							listView.setFixBarMode(XListView.FIX_BAR_MODE_MOVE);
							checkBox.setChecked(false);
						} else {
							listView.setFixBarMode(XListView.FIX_BAR_MODE_FIX);
							checkBox.setChecked(true);
						}
					} else if (position == 6) {
					} else if (position == 7) {
						if (listView.isEnableSwipe()) {
							listView.enableSwipe(false);
							checkBox.setChecked(false);
						} else {
							listView.enableSwipe(true);
							checkBox.setChecked(true);
						}
					} else if (position == 8) {
						if (listView.isEnableAutoCloseSwipe()) {
							listView.enableAutoCloseSwipe(false);
							checkBox.setChecked(false);
						} else {
							listView.enableAutoCloseSwipe(true);
							checkBox.setChecked(true);
						}
					} else if (position == 9) {
						if (listView.isEnableLoadMore()) {
							listView.enableLoadMore(false);
							checkBox.setChecked(false);
						} else {
							listView.enableLoadMore(true);
							checkBox.setChecked(true);
						}

					} else if (position == 10) {
						if (listView.isEnableAutoLoadMore()) {
							listView.enableAutoLoadMore(false);
							checkBox.setChecked(false);
						} else {
							listView.enableAutoLoadMore(true);
							checkBox.setChecked(true);
						}

					}

				}

			});

			return view;
		}

	};
	
	class RefreshTask extends AsyncTask<Void, Void, String[]>{

		@Override
		protected String[] doInBackground(Void... params) {
			// Simulates a background job.
			try {
				Thread.sleep(1000);
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
		}

		
	};
	
	class LoadMoreTask extends AsyncTask<Void, Void, String[]>{

		@Override
		protected String[] doInBackground(Void... params) {
			// Simulates a background job.
			try {
				Thread.sleep(1000);
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
			mAdapter.notifyDataSetChanged();
			listView.completeLoadMore();
		}

		
	};
}
