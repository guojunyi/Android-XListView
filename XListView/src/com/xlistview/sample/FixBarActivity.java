package com.xlistview.sample;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.xlistview.R;
import com.xlistview.XListView;

public class FixBarActivity extends Activity{
	Context mContext;
	XListView listView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fixbar);
		mContext = this;
		listView = (XListView) findViewById(R.id.listView);
		
		listView.enableFixBar(true);
		listView.setFixBarMode(XListView.FIX_BAR_MODE_MOVE);
		listView.setAdapter(mAdapter);
	}
	
	
	BaseAdapter mAdapter = new BaseAdapter(){

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return 30;
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
			return textView;
		}
		
	};
}
