Android-XListView
================
![](https://raw.githubusercontent.com/guojunyi/Android-XListView/master/screenshot/1.gif)
![](https://raw.githubusercontent.com/guojunyi/Android-XListView/master/screenshot/2.gif)
![](https://raw.githubusercontent.com/guojunyi/Android-XListView/master/screenshot/3.gif)
![](https://raw.githubusercontent.com/guojunyi/Android-XListView/master/screenshot/4.gif)
## Sample Application
<a href="https://raw.githubusercontent.com/guojunyi/Android-XListView/master/apk/XListView.apk" target="_blank" title="Download From Google Play">Click to Download the simple apk</a>

## Usage
``` xml
	<com.xlistview.XListView
   	    android:id="@+id/listView"
   	    android:layout_width="match_parent"
   	    android:layout_height="match_parent"
   	    gjy:headerView="@layout/layout_header_all_config"
   	    gjy:moreView="@layout/layout_more_all_config"
   	    gjy:fixBar="@layout/layout_fixbar_all_config"
   	    gjy:fixBarMinHeight="0dp"
   	    android:background="#fff" 
   	    >
   	    
   	</com.xlistview.XListView>
```


``` java
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
```



## License

    Copyright 2015 guojunyi

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
