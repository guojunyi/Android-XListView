package com.xlistview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.HeaderViewListAdapter;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.nineoldandroids.animation.ValueAnimator;
import com.nineoldandroids.animation.ValueAnimator.AnimatorUpdateListener;

public class XListView extends ViewGroup implements IXListView {
	public static final String TAG = "XListView";
	
	private boolean isEnableSwipe;
	private boolean isEnableAutoCloseSwipe;
	private boolean isEnablePullRefresh;
	private boolean isEnableLoadMore;
	private boolean isEnableAutoLoadMore;
	private boolean isEnableFixBar;
	
	private int mPullRefreshState = 0;
	public final static int STATE_NONE = 0;
	public final static int STATE_PULL = 1;
	public final static int STATE_REFRESH = 2;

	private int pullHeaderMode;
	public final static int PULL_HEADER_MODE_MOVE = 0;
	public final static int PULL_HEADER_MODE_FIX = 1;

	private Context mContext;
	private InnerListView mInnerListView;
	private View pullHeaderView;
	private View fixBar;
	private View listHeaderView;
	private View moreView;
	private LinearLayout moreLayout;
	
	private boolean isShowLoadMoreView;

	public final static int MORE_STATE_NONE = 0;
	public final static int MORE_STATE_PULL = 1;
	public final static int MORE_STATE_LOADING = 2;
	public int mMoreState = 0;
	private int mMoreViewHeight;

	private int fixBarMode;
	public final static int FIX_BAR_MODE_MOVE = 0;
	public final static int FIX_BAR_MODE_FIX = 1;
	private int fixBarMinHeight = 0;

	private PullRefreshListener mPullRefreshListener;
	private OnScrollListener mOnScrollListener;
	private LoadMoreListener mLoadMoreListener;
	private FixBarListener mFixBarListener;
	
	private EventTrigger eventTrigger;
	
	
	public XListView(Context context) {
		this(context, null);
	}

	public XListView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public XListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;
		initConfig(attrs);
	}

	int headerL, headerT, headerR, headerB;
	int fixBarL, fixBarT, fixBarR, fixBarB;
	int innerListL, innerListT, innerListR, innerListB;
	boolean isAnimating;

	
	

	public interface PullRefreshListener {
		public void onRefresh(View headerView);
	}

	public interface LoadMoreListener {
		public void onLoadMore(View moreView);
	}
	
	public interface FixBarListener{
		public void onHeightChange(View fixBar,int height);
	}

	@Override
	public void setPullRefreshListener(PullRefreshListener pullRefreshListener) {
		// TODO Auto-generated method stub
		mPullRefreshListener = pullRefreshListener;
	}
	
	@Override
	public void setFixBarListener(FixBarListener fixBarListener) {
		this.mFixBarListener = fixBarListener;
	}
	
	@Override
	public void setLoadMoreListener(LoadMoreListener loadMoreListener) {
		this.mLoadMoreListener = loadMoreListener;
	}
	
	public void initConfig(AttributeSet attrs) {
		pullHeaderMode = PULL_HEADER_MODE_MOVE;
		fixBarMode = FIX_BAR_MODE_MOVE;
		TypedArray a = mContext.obtainStyledAttributes(attrs,
				R.styleable.XListView);

		// headerView
		int headerViewResID = a.getResourceId(R.styleable.XListView_headerView,
				-1);
		if (headerViewResID != -1) {
			pullHeaderView = LayoutInflater.from(mContext).inflate(headerViewResID,
					null);
			addView(pullHeaderView);
		}

		// moreView
		int moreViewResID = a.getResourceId(R.styleable.XListView_moreView, -1);
		if (moreViewResID != -1) {
			moreView = LayoutInflater.from(mContext).inflate(moreViewResID,
					null);
		}

		// fixBar
		fixBarMinHeight = (int) a.getDimension(
				R.styleable.XListView_fixBarMinHeight, -1);
		int fixBarResID = a.getResourceId(R.styleable.XListView_fixBar, -1);
		if (fixBarResID != -1) {
			fixBar = LayoutInflater.from(mContext).inflate(fixBarResID, null);
		}

		a.recycle();

		mInnerListView = new InnerListView(mContext);
		if (null != getBackground()) {
			mInnerListView.setBackgroundDrawable(getBackground()
					.getConstantState().newDrawable());
		}

		mInnerListView.setYListView(this);
		mInnerListView.setLayoutParams(new ViewGroup.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		
		addView(mInnerListView);
		
		if (null != fixBar) {
			addView(fixBar);
		}
		
		
		
		
		

		if (null != pullHeaderView) {
			if (null != pullHeaderView.getBackground()) {
				setBackgroundDrawable(pullHeaderView.getBackground()
						.getConstantState().newDrawable());
			}
		}

		eventTrigger = new DefaultEventTrigger();
		eventTrigger.setXListView(this);
	}

	@Override
	public EventTrigger getEventTrigger() {
		return eventTrigger;
	}

	@Override
	public void setEventTrigger(EventTrigger animationAdapter) {
		animationAdapter.setXListView(this);
		this.eventTrigger = animationAdapter;
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		// TODO Auto-generated method stub

		mInnerListView.layout(innerListL, innerListT, innerListR, innerListB);

		if (null != pullHeaderView) {
			pullHeaderView.layout(headerL, headerT, headerR, headerB);
		}

		if (null != fixBar && mPullRefreshState != STATE_PULL) {
			fixBar.layout(fixBarL, fixBarT, fixBarR, fixBarB);
		}


		
	}

	boolean isInitMeasure;

	@Override
	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		measureChildren(widthMeasureSpec, heightMeasureSpec);
		if (null != pullHeaderView) {
			pullHeaderView.measure(widthMeasureSpec,
					MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
			if (!isInitMeasure) {
				headerL = 0;
				headerT = 0;
				headerR = pullHeaderView.getMeasuredWidth();
				headerB = pullHeaderView.getMeasuredHeight();
			}
		}

		if (null!= fixBar) {
			if(fixBarMinHeight<0){
				fixBarMinHeight = fixBar.getMeasuredHeight();
			}
			
			if(fixBarMinHeight>fixBar.getMeasuredHeight()){
				this.fixBarMinHeight = fixBar.getMeasuredHeight();
			}
			
			if (!isInitMeasure) {
				
				fixBarL = 0;
				fixBarT = 0;
				fixBarR = fixBar.getMeasuredWidth();
				if(!isEnableFixBar){
					fixBarB = 0;
				}else{
					fixBarB = fixBar.getMeasuredHeight();
				}
				listHeaderView.setLayoutParams(new AbsListView.LayoutParams(
						LayoutParams.MATCH_PARENT,
						fixBarB));
			}

		}

		if (!isInitMeasure) {
			innerListL = 0;
			innerListT = 0;
			innerListR = mInnerListView.getMeasuredWidth();
			innerListB = mInnerListView.getMeasuredHeight();
		}
		isInitMeasure = true;
	}

	@Override
	public void onFinishInflate() {
		super.onFinishInflate();
	}

	@Override
	public ListAdapter getListAdapter() {
		return mInnerListView.getListAdapter();
	}
	
	public void setAdapter(ListAdapter adapter) {
		mInnerListView.setAdapter(adapter);
	}

	@Override
	public void onDraw(Canvas canvas){
		super.onDraw(canvas);
	}
	
	@Override
	public boolean drawChild(Canvas canvas, View child, long drawingTime){
		return super.drawChild(canvas, child, drawingTime);
	}
	
	public class InnerListView extends ListView implements OnScrollListener {
		private XListView yListView;
		private float mMoveY;
		private int mLastMotionY;

		public InnerListView(Context context) {
			this(context, null);
		}

		public InnerListView(Context context, AttributeSet attrs) {
			this(context, attrs, 0);
		}

		public InnerListView(Context context, AttributeSet attrs, int defStyle) {
			super(context, attrs, defStyle);
			mContext = context;
			initConfig(attrs);
		}

		private void initConfig(AttributeSet attrs) {
			super.setOnScrollListener(this);

			if (null != fixBar) {
				listHeaderView = new View(mContext);
				addHeaderView(listHeaderView);
				
				listHeaderView.setLayoutParams(new AbsListView.LayoutParams(
						LayoutParams.MATCH_PARENT,
						fixBar.getMeasuredHeight()));
			}

			if (null != moreView) {
				moreLayout = new LinearLayout(this.getContext());
				moreLayout.setClickable(false);
				moreLayout.setFocusable(false);
				moreLayout.setOrientation(LinearLayout.VERTICAL);
				moreLayout.setLayoutParams(new LayoutParams(
						LayoutParams.MATCH_PARENT, 0));

				moreLayout.addView(moreView);
				LinearLayout.LayoutParams moreViewParams = (LinearLayout.LayoutParams) moreView
						.getLayoutParams();
				moreViewParams.height = 0;
				moreView.setLayoutParams(moreViewParams);

				addFooterView(moreLayout);
			}

		}

		@Override
		public void setBackgroundDrawable(Drawable background) {
			super.setBackgroundDrawable(background);

			if (null != listHeaderView) {
				listHeaderView.setBackgroundDrawable(background
						.getConstantState().newDrawable());
			}
			
			if (null != moreLayout) {
				moreLayout.setBackgroundDrawable(background.getConstantState()
						.newDrawable());
			}
		}

		public XListView getYListView() {
			return yListView;
		}

		public void setYListView(XListView yListView) {
			this.yListView = yListView;
		}

		public ListAdapter getListAdapter() {
			if (getHeaderViewsCount() > 0) {
				HeaderViewListAdapter headerViewListAdapter = (HeaderViewListAdapter) mInnerListView
						.getAdapter();
				if(null!=headerViewListAdapter){
					return headerViewListAdapter.getWrappedAdapter();
				}else{
					return null;
				}
				
			} else {
				return getAdapter();
			}
			
			
		}

		@Override
		public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
			super.onMeasure(widthMeasureSpec, heightMeasureSpec);
			
			if(null!=moreView){
				moreView.measure(0,
						MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
				mMoreViewHeight = moreView.getMeasuredHeight();
			}
		}
		@Override
		public boolean onInterceptTouchEvent(MotionEvent ev) {
			// TODO Auto-generated method stub

			if (getListAdapter() instanceof BaseSwipeAdapter) {
				BaseSwipeAdapter adapter = (BaseSwipeAdapter) getListAdapter();
				if (null != adapter && adapter.isDragged()) {
					return false;
				}

				if (null != adapter && adapter.existOpenSwipeLayout()
						&& yListView.isEnableAutoCloseSwipe()) {
					return false;
				}
			}

			switch (ev.getAction()) {
			case MotionEvent.ACTION_DOWN:
				mLastMotionY = (int) ev.getRawY();
				mMoveY = (int) ev.getRawY();
				break;
			}
			boolean result = super.onInterceptTouchEvent(ev);
			Log.e("my",""+result);
			return result;
		}

		@Override
		public boolean onTouchEvent(MotionEvent ev) {
			if (getListAdapter() instanceof BaseSwipeAdapter) {
				BaseSwipeAdapter adapter = (BaseSwipeAdapter) getListAdapter();
				if (null != adapter && adapter.existOpenSwipeLayout()
						&& yListView.isEnableAutoCloseSwipe()) {
					return false;
				}
			}
			Log.e("my","onTouchEvent");
			boolean flag = false;

			if ((null == fixBar || fixBar.getHeight() >= fixBar.getMeasuredHeight()||!isEnableFixBar)
					&& isEnablePullRefresh
					&& (getFirstVisiblePosition() == 0||mPullRefreshState==STATE_PULL)
					&& !isAnimating) {
				
				if (pullHeaderMode == PULL_HEADER_MODE_MOVE) {
					flag = handlerPullRefreshInMoveMode(ev);
				} else {
					flag = handlerPullRefreshInFixMode(ev);
				}
			} else {

			}

			if (isEnableLoadMore && !isEnableAutoLoadMore && isShowLoadMoreView
					&& isScrollToBottom) {
				flag = handlerMoreView(ev);
			}
			
			if (ev.getAction() == MotionEvent.ACTION_CANCEL
					|| ev.getAction() == MotionEvent.ACTION_UP) {
				if (mMoreState != MORE_STATE_LOADING&&!isEnableAutoLoadMore&&null!=moreView) {

					
					if(eventTrigger.onMoreTouchUp(moreView, moreLayout.getLayoutParams().height-mMoreViewHeight)){
						mMoreState = MORE_STATE_LOADING;
						if (null != mLoadMoreListener) {
							mLoadMoreListener.onLoadMore(moreView);
						}
						
						if(null!=eventTrigger){
							eventTrigger.onLoadMore(moreView);
						}
					}
				}

				if (isShowLoadMoreView
						&& moreLayout.getLayoutParams().height > mMoreViewHeight) {
					releaseMoreView();
				}

				if(mPullRefreshState!=STATE_REFRESH&&null!=pullHeaderView){
					
					if (eventTrigger.onPullHeaderTouchUp(pullHeaderView,mInnerListView.getTop())) {
						if (!isAnimating) {
							if(null != mPullRefreshListener){
								mPullRefreshListener.onRefresh(pullHeaderView);
							}
							
							if(null!=eventTrigger){
								eventTrigger.onRefresh(pullHeaderView);
							}
							
						}
						mPullRefreshState = STATE_REFRESH;
					} else {
						mPullRefreshState = STATE_NONE;
					}
				}

				
				if (mPullRefreshState == STATE_REFRESH
						&& mInnerListView.getTop() > eventTrigger.getOnHeaderViewRefreshingHeight(pullHeaderView)) {
					animateHeaderView();
				}

				if (mPullRefreshState == STATE_NONE
						&& mInnerListView.getTop() > 0) {
					animateHeaderView();
				}

			}
			if (flag) {
				return true;
			} else {
				return super.onTouchEvent(ev);
			}
		}

		private boolean handlerMoreView(MotionEvent ev) {

			boolean result = false;
			switch (ev.getAction()) {
			case MotionEvent.ACTION_DOWN:
				mLastMotionY = (int) ev.getRawY();
				mMoveY = (int) ev.getRawY();
				break;
			case MotionEvent.ACTION_MOVE:
				int direction = (int) (ev.getRawY() - mMoveY);
				if (isEnableLoadMore && !isEnableAutoLoadMore
						&& isShowLoadMoreView) {

					if (isScrollToBottom && direction < 0
							&& mMoreState == MORE_STATE_NONE) {
						mMoreState = MORE_STATE_PULL;
						mLastMotionY = (int) ev.getRawY();
						Log.e(TAG, "MORE_STATE_PULL");
					}

					if (mMoreState == MORE_STATE_PULL) {
						this.setSelection(this.getBottom());
						int yDiff = (int) (ev.getRawY() - mLastMotionY);

						if (yDiff <= 0) {
							yDiff = yDiff / 2;
							LayoutParams params = (LayoutParams) moreLayout
									.getLayoutParams();
							params.height = -yDiff + mMoreViewHeight;
							moreLayout.setLayoutParams(params);

							if (null != eventTrigger) {
								eventTrigger.onMoreViewHeightChange(moreView,
										-yDiff);
							}

						} else {
							LayoutParams params = (LayoutParams) moreLayout
									.getLayoutParams();
							params.height = mMoreViewHeight;
							moreLayout.setLayoutParams(params);
							if (mMoreState != MORE_STATE_NONE) {
								Log.e(TAG, "MORE_STATE_NONE");
							}
							mMoreState = MORE_STATE_NONE;

							if (null != eventTrigger) {
								eventTrigger.onMoreViewHeightChange(moreView,
										-yDiff);
							}

						}
					}
				}

				mMoveY = (int) ev.getRawY();
				break;
			}

			return result;
		}

		private boolean handlerPullRefreshInFixMode(MotionEvent ev) {
			boolean result = false;
			switch (ev.getAction()) {
			case MotionEvent.ACTION_DOWN:
				mLastMotionY = (int) ev.getRawY();
				mMoveY = (int) ev.getRawY();
				break;
			case MotionEvent.ACTION_MOVE:

				float direction = ev.getRawY() - mMoveY;
				View firstItem = getChildAt(0);
				boolean isTop = null == firstItem ? true
						: firstItem.getTop() >= 0 ? true : false;
				if (null != pullHeaderView && isEnablePullRefresh
						&& getFirstVisiblePosition() == 0 && isTop) {

					if (getFirstVisiblePosition() == 0 && isTop
							&& mPullRefreshState == STATE_NONE && direction > 0) {
						mPullRefreshState = STATE_PULL;
						mLastMotionY = (int) ev.getRawY() + 1;
						Log.e(TAG, "STATE_PULL");
					}

					if (mPullRefreshState == STATE_PULL) {
						this.setSelection(0);
						int yDiff = (int) (ev.getRawY() - mLastMotionY);
						if (yDiff >= 0) {
							yDiff = (int) (yDiff / 1.6);
							if (yDiff > pullHeaderView.getMeasuredHeight()) {
								moveListView(yDiff);
							} else {
								moveListView(yDiff);
							}
						} else {
							moveListView(0);
							mPullRefreshState = STATE_NONE;
							Log.e(TAG, "STATE_NONE");
							mLastMotionY = (int) ev.getRawY();
						}

					}
				}

				if (mPullRefreshState != STATE_PULL) {
					mLastMotionY = (int) ev.getRawY();
				} else {
					result = true;
				}
				
				
				mMoveY = (int) ev.getRawY();
				break;
			}

			return result;
		}

		private boolean handlerPullRefreshInMoveMode(MotionEvent ev) {
			boolean result = false;
			switch (ev.getAction()) {
			case MotionEvent.ACTION_DOWN:
				mLastMotionY = (int) ev.getRawY();
				mMoveY = (int) ev.getRawY();
				break;
			case MotionEvent.ACTION_MOVE:
				float direction = ev.getRawY() - mMoveY;
				View firstItem = getChildAt(0);
				boolean isTop = null == firstItem ? true
						: firstItem.getTop() >= 0 ? true : false;
				if (null != pullHeaderView && isEnablePullRefresh
						&& getFirstVisiblePosition() == 0 && isTop) {

					if (getFirstVisiblePosition() == 0 && isTop
							&& mPullRefreshState == STATE_NONE && direction > 0) {
						mPullRefreshState = STATE_PULL;
						mLastMotionY = (int) ev.getRawY() + 1;
						Log.e(TAG, "STATE_PULL");
					}

					if (mPullRefreshState == STATE_PULL) {
						this.setSelection(0);
						int yDiff = (int) (ev.getRawY() - mLastMotionY);
						if (yDiff >= 0) {
							yDiff = (int) (yDiff / 1.6);

							if (yDiff > pullHeaderView.getMeasuredHeight()) {
								moveHeaderView(pullHeaderView.getMeasuredHeight());
								moveListView(yDiff);
							} else {
								moveHeaderView(yDiff);
								moveListView(yDiff);
							}
						} else {
							moveHeaderView(0);
							moveListView(0);
							mPullRefreshState = STATE_NONE;
							Log.e(TAG, "STATE_NONE");
							mLastMotionY = (int) ev.getRawY();
						}

					}
				}

				if (mPullRefreshState != STATE_PULL) {
					mLastMotionY = (int) ev.getRawY();
				} else {
					result = true;
				}
				mMoveY = (int) ev.getRawY();
				break;
			}

			return result;
		}

		private void moveHeaderView(int diff) {
			if (null != pullHeaderView) {
				headerL = pullHeaderView.getLeft();
				headerT = -pullHeaderView.getMeasuredHeight() + diff;
				headerR = pullHeaderView.getRight();
				headerB = diff;
				pullHeaderView.layout(headerL, headerT, headerR, headerB);
			}

		}

		private void moveListView(int diff) {
			if (pullHeaderMode == PULL_HEADER_MODE_FIX) {
				if (null != pullHeaderView) {
					headerL = pullHeaderView.getLeft();
					headerT = 0;
					headerR = pullHeaderView.getRight();
					headerB = diff;
					pullHeaderView.layout(headerL, headerT, headerR, headerB);
				}
			}
			
			
			innerListL = mInnerListView.getLeft();
			innerListT = diff;
			innerListR = mInnerListView.getRight();
			innerListB = mInnerListView.getMeasuredHeight();
			mInnerListView.layout(innerListL, innerListT, innerListR,
					innerListB);

			if (null != fixBar) {
				if(fixBarMode == FIX_BAR_MODE_FIX){
					fixBarL = fixBar.getLeft();
					fixBarT = diff;
					fixBarR = fixBar.getMeasuredWidth();
					fixBarB = fixBar.getHeight() + diff;
					fixBar.layout(fixBarL, fixBarT, fixBarR, fixBarB);
				}else{
					
					int height = this.getFirstVisiblePosition()==0?listHeaderView.getBottom():fixBarMinHeight;
					if(diff>0){
						fixBarL = fixBar.getLeft();
						fixBarT = diff+height-fixBar.getHeight();
						fixBarR = fixBar.getMeasuredWidth();
						fixBarB = diff+height;
						fixBar.layout(fixBarL, fixBarT, fixBarR, fixBarB);
					}
				}
				

				
			}

			
			
			if (null != eventTrigger) {
				eventTrigger.onHeaderViewHeightChange(pullHeaderView, diff);
			}
			
			
		}

		private void releaseMoreView() {
			if (mMoreState != MORE_STATE_LOADING) {
				if (mMoreState != MORE_STATE_NONE) {
					Log.e(TAG, "MORE_STATE_NONE");
				}
				mMoreState = MORE_STATE_NONE;
			}

			if (isShowLoadMoreView
					&& moreLayout.getLayoutParams().height > mMoreViewHeight) {
				ValueAnimator animator = ValueAnimator.ofInt(
						moreLayout.getLayoutParams().height, mMoreViewHeight);
				animator.setDuration(300);
				animator.addUpdateListener(new AnimatorUpdateListener() {

					@Override
					public void onAnimationUpdate(ValueAnimator animation) {
						// TODO Auto-generated method stub
						int frameValue = (Integer) animation.getAnimatedValue();
						Message msg = new Message();
						msg.what = -999;
						msg.arg1 = frameValue;
						mHandler.sendMessage(msg);
						if (frameValue == 0) {
							
						}
					}
				});
				animator.start();
			}
		}

		private void animateHeaderView() {
			if (null == pullHeaderView) {
				return;
			}

			
			if (mPullRefreshState == STATE_REFRESH) {
				ValueAnimator animator = ValueAnimator
						.ofInt(mInnerListView.getTop(),
								eventTrigger.getOnHeaderViewRefreshingHeight(pullHeaderView));
				animator.setDuration(200);
				isAnimating = true;
				animator.addUpdateListener(new AnimatorUpdateListener() {

					@Override
					public void onAnimationUpdate(ValueAnimator animation) {
						// TODO Auto-generated method stub
						int frameValue = (Integer) animation.getAnimatedValue();
						Message msg = new Message();
						msg.what = STATE_REFRESH;
						msg.arg1 = frameValue;
						mHandler.sendMessage(msg);

						if (frameValue == eventTrigger.getOnHeaderViewRefreshingHeight(pullHeaderView)) {
							getYListView().removeView(pullHeaderView);
							getYListView().addView(pullHeaderView);
							isAnimating = false;
							mHandler.postDelayed(new Runnable(){

								@Override
								public void run() {
									// TODO Auto-generated method stub
									if (null != eventTrigger) {
										eventTrigger.onHeaderViewHeightChange(pullHeaderView, pullHeaderView.getBottom());
									}
								}
								
							}, 100);
						}
					}
				});
				animator.start();
			} else {
				if(fixBarMode == FIX_BAR_MODE_FIX){
					getYListView().removeView(pullHeaderView);
					getYListView().addView(pullHeaderView, 0);
				}
				ValueAnimator animator = ValueAnimator.ofInt(
						mInnerListView.getTop(), 0);
				animator.setDuration(200);
				isAnimating = true;
				animator.addUpdateListener(new AnimatorUpdateListener() {

					@Override
					public void onAnimationUpdate(ValueAnimator animation) {
						// TODO Auto-generated method stub
						int frameValue = (Integer) animation.getAnimatedValue();

						Message msg = new Message();
						msg.what = STATE_NONE;
						msg.arg1 = frameValue;
						mHandler.sendMessage(msg);
						if (frameValue == 0) {
							if(fixBarMode == FIX_BAR_MODE_MOVE){
								getYListView().removeView(pullHeaderView);
								getYListView().addView(pullHeaderView, 0);
							}
							isAnimating = false;
							mHandler.postDelayed(new Runnable(){

								@Override
								public void run() {
									// TODO Auto-generated method stub
									if (null != eventTrigger) {
										eventTrigger.onHeaderViewHeightChange(pullHeaderView, 0);
									}
								}
								
							}, 100);
						}
					}
				});
				animator.start();
			}

		}

		private Handler mHandler = new Handler(Looper.getMainLooper()) {

			@Override
			public void handleMessage(Message msg) {
				if (msg.what == -999) {
					LayoutParams params = (LayoutParams) moreLayout
							.getLayoutParams();
					params.height = msg.arg1;
					moreLayout.setLayoutParams(params);
					
					if (null != eventTrigger) {
						eventTrigger.onMoreViewHeightChange(moreView, msg.arg1
							- mMoreViewHeight);
					}
				} else {
					switch (msg.what) {
					case STATE_REFRESH:
						moveListView(msg.arg1);
						if (pullHeaderMode == PULL_HEADER_MODE_MOVE
								&& msg.arg1 <= pullHeaderView.getMeasuredHeight()) {
							moveHeaderView(msg.arg1);
						}
						break;
					case STATE_NONE:
						moveListView(msg.arg1);

						if (pullHeaderMode == PULL_HEADER_MODE_MOVE) {
							moveHeaderView(msg.arg1);
						}
						break;
					}
				}

			}
		};

		@Override
		public void setAdapter(ListAdapter adapter) {
			if (adapter instanceof BaseSwipeAdapter) {
				((BaseSwipeAdapter) adapter).setListView(yListView);
			}
			super.setAdapter(adapter);
		}

		@Override
		public void setOnScrollListener(OnScrollListener listener) {
			mOnScrollListener = listener;
		}

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			// TODO Auto-generated method stub
			if (null != mOnScrollListener) {
				mOnScrollListener.onScrollStateChanged(view, scrollState);
			}
		}

		boolean isScrollToBottom;

		
		int lastFixBarHeight = 0;
		@Override
		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount) {
			if (null != mOnScrollListener) {
				mOnScrollListener.onScroll(view, firstVisibleItem,
						visibleItemCount, totalItemCount);
			}

			if ((firstVisibleItem + visibleItemCount) == totalItemCount-1) {
				if(null!=moreView){
					moreView.requestLayout();
				}
			}
			
			if ((firstVisibleItem + visibleItemCount) == totalItemCount) {
				View lastItem = (View) getChildAt(visibleItemCount - 1);
				if (null != lastItem) {
					if (lastItem.getBottom() == getHeight()) {
						if (!isScrollToBottom) {
							isScrollToBottom = true;
							if (mMoreState != MORE_STATE_LOADING
									&& isEnableAutoLoadMore) {
								mMoreState = MORE_STATE_LOADING;
								if (null != mLoadMoreListener) {
									mLoadMoreListener.onLoadMore(moreView);
								}
								
								if(null!=eventTrigger){
									eventTrigger.onLoadMore(moreView);
								}
							}
						}
					} else {
						isScrollToBottom = false;
					}
				} else {
					isScrollToBottom = false;
				}
			} else {
				isScrollToBottom = false;
			}
			

			if (getCount() > 0) {
				View item = null;
				if (null != fixBar) {
					item = this.getChildAt(1);
				} else {
					item = this.getChildAt(0);
				}

				int listHeaderHeight = listHeaderView != null ? listHeaderView
						.getLayoutParams().height : 0;
				if (null != item
						&& (getCount() * item.getMeasuredHeight() + getCount()
								* getDividerHeight() + listHeaderHeight) > getMeasuredHeight()
						&& isEnableLoadMore) {
					showLoadMoreView();
				} else {
					hideLoadMoreView();
				}
			} else {
				hideLoadMoreView();
			}
			
			

			
			if (null != fixBar && mPullRefreshState != STATE_PULL
					&& null != listHeaderView&&isEnableFixBar) {
				if (isAnimating) {
					return;
				}
				
				if(fixBarMode==FIX_BAR_MODE_FIX){
					if (firstVisibleItem == 0
							&& listHeaderView.getBottom() >= fixBarMinHeight) {
						
						if (mPullRefreshState == STATE_REFRESH) {
							fixBarL = fixBar.getLeft();
							fixBarT = eventTrigger.getOnHeaderViewRefreshingHeight(pullHeaderView);
							fixBarR = fixBar.getMeasuredWidth();
							fixBarB = listHeaderView.getBottom()
									+ eventTrigger.getOnHeaderViewRefreshingHeight(pullHeaderView);
						} else {
							fixBarL = fixBar.getLeft();
							fixBarT = 0;
							fixBarR = fixBar.getMeasuredWidth();
							fixBarB = listHeaderView.getBottom();
						}
						
						fixBar.layout(fixBarL, fixBarT, fixBarR, fixBarB);
					} else {
						if (mPullRefreshState == STATE_REFRESH) {
							fixBarL = fixBar.getLeft();
							fixBarT = eventTrigger.getOnHeaderViewRefreshingHeight(pullHeaderView);
							fixBarR = fixBar.getMeasuredWidth();
							fixBarB = fixBarMinHeight
									+ eventTrigger.getOnHeaderViewRefreshingHeight(pullHeaderView);
						} else {
							fixBarL = fixBar.getLeft();
							fixBarT = 0;
							fixBarR = fixBar.getMeasuredWidth();
							fixBarB = fixBarMinHeight;
						}
						
						
						fixBar.layout(fixBarL, fixBarT, fixBarR, fixBarB);
					}
				}else{
					if (firstVisibleItem == 0
							&& listHeaderView.getBottom() >= fixBarMinHeight) {
						if (mPullRefreshState == STATE_REFRESH) {
							
							fixBarL = fixBar.getLeft();
							fixBarT = listHeaderView.getBottom()-fixBar.getMeasuredHeight()+eventTrigger.getOnHeaderViewRefreshingHeight(pullHeaderView);
							fixBarR = fixBar.getMeasuredWidth();
							fixBarB = listHeaderView.getBottom()
									+ eventTrigger.getOnHeaderViewRefreshingHeight(pullHeaderView);
						} else {
							fixBarL = fixBar.getLeft();
							fixBarT = listHeaderView.getBottom()-fixBar.getMeasuredHeight();
							fixBarR = fixBar.getMeasuredWidth();
							fixBarB = listHeaderView.getBottom();
						}
						
						fixBar.layout(fixBarL, fixBarT, fixBarR, fixBarB);
					} else {
						
						if (mPullRefreshState == STATE_REFRESH) {
							fixBarL = fixBar.getLeft();
							fixBarT = fixBarMinHeight-fixBar.getMeasuredHeight()+eventTrigger.getOnHeaderViewRefreshingHeight(pullHeaderView);
							fixBarR = fixBar.getMeasuredWidth();
							fixBarB = fixBarMinHeight
									+ eventTrigger.getOnHeaderViewRefreshingHeight(pullHeaderView);
						} else {
							fixBarL = fixBar.getLeft();
							fixBarT = fixBarMinHeight-fixBar.getMeasuredHeight();
							fixBarR = fixBar.getMeasuredWidth();
							fixBarB = fixBarMinHeight;
						}
						
						
						fixBar.layout(fixBarL, fixBarT, fixBarR, fixBarB);
					}
				}
				
				
				
				if(null!=mFixBarListener&&lastFixBarHeight!=fixBar.getBottom()){
					if(mPullRefreshState == STATE_REFRESH){
						mFixBarListener.onHeightChange(fixBar, fixBar.getBottom()-eventTrigger.getOnHeaderViewRefreshingHeight(pullHeaderView));
					}else{
						mFixBarListener.onHeightChange(fixBar, fixBar.getBottom());
					}
					
					lastFixBarHeight = fixBar.getBottom();
				}

			}

		}

		@Override
		public int getCount() {
			int count = super.getCount();

			if (null != fixBar) {
				count -= 1;
			}

			if (null != moreView) {
				count -= 1;
			}
			return count;
		}

		private void showLoadMoreView() {
			if (!isShowLoadMoreView) {
				LayoutParams params = (LayoutParams) moreLayout
						.getLayoutParams();
				params.height = mMoreViewHeight;
				moreLayout.setLayoutParams(params);

				LinearLayout.LayoutParams moreViewParams = (LinearLayout.LayoutParams) moreView
						.getLayoutParams();
				moreViewParams.height = mMoreViewHeight;
				moreView.setLayoutParams(moreViewParams);
				isShowLoadMoreView = true;
				moreLayout.setVisibility(View.VISIBLE);
			}

		}

		private void hideLoadMoreView() {
			if (isShowLoadMoreView) {
				LayoutParams params = (LayoutParams) moreLayout
						.getLayoutParams();
				params.height = 0;
				moreLayout.setLayoutParams(params);

				LinearLayout.LayoutParams moreViewParams = (LinearLayout.LayoutParams) moreView
						.getLayoutParams();
				moreViewParams.height = 0;
				moreView.setLayoutParams(moreViewParams);
				isShowLoadMoreView = false;
				moreLayout.setVisibility(View.GONE);
			}

		}

		public void changeLoadMoreView() {
			if (!isEnableLoadMore) {
				hideLoadMoreView();
				return;
			}

			if (getCount() > 0) {
				View item = null;
				if (null != fixBar) {
					item = this.getChildAt(1);
				} else {
					item = this.getChildAt(0);
				}

				int listHeaderHeight = listHeaderView != null ? listHeaderView
						.getLayoutParams().height : 0;
				if (null != item
						&& (getCount() * item.getMeasuredHeight() + getCount()
								* getDividerHeight() + listHeaderHeight) > getMeasuredHeight()
						&& isEnableLoadMore) {
					showLoadMoreView();
				} else {
					hideLoadMoreView();
				}
			} else {
				hideLoadMoreView();
			}
		}
	}

	@Override
	public void pullUp() {
		// TODO Auto-generated method stub
		mPullRefreshState = STATE_NONE;
		mInnerListView.animateHeaderView();
	}

	@Override
	public void autoRefresh() {
		// TODO Auto-generated method stub
		if(isEnablePullRefresh){
			mInnerListView.smoothScrollToPosition(0);
			if (mPullRefreshState != STATE_REFRESH) {
				mPullRefreshState = STATE_REFRESH;
				if(null != mPullRefreshListener){
					mPullRefreshListener.onRefresh(pullHeaderView);
				}
				
				if(null!=eventTrigger){
					eventTrigger.onRefresh(pullHeaderView);
				}
			}
			mInnerListView.animateHeaderView();
		}
	}

	@Override
	public void enableSwipe(boolean enable) {
		// TODO Auto-generated method stub
		isEnableSwipe = enable;
		if (enable == false) {
			if (getListAdapter() instanceof BaseSwipeAdapter) {
				BaseSwipeAdapter adapter = (BaseSwipeAdapter) getListAdapter();
				if (null != adapter) {
					adapter.closeAllSwipeLayout();
				}
			}

		}
	}
	
	@Override
	public void enableFixBar(boolean enable) {
		// TODO Auto-generated method stub
		if (null == fixBar) {
			throw new IllegalArgumentException(
					"No XML attribute 'fixBar' declaration int XListView.");
		}
		
		isEnableFixBar = enable;
		resizeFixBar();
	}

	
	@Override
	public void enableAutoCloseSwipe(boolean auto) {
		// TODO Auto-generated method stub
		isEnableAutoCloseSwipe = auto;
	}

	@Override
	public void enablePullRefresh(boolean enable) {
		// TODO Auto-generated method stub
		if (null == pullHeaderView) {
			throw new IllegalArgumentException(
					"No XML attribute 'headerView' declaration int XListView.");
		}

		isEnablePullRefresh = enable;
		if (enable == false) {
			mPullRefreshState = STATE_NONE;
			mInnerListView.animateHeaderView();
		}
	}

	@Override
	public void enableLoadMore(boolean enable) {
		// TODO Auto-generated method stub
		if (null == moreView) {
			throw new IllegalArgumentException(
					"No XML attribute 'moreView' declaration int XListView.");
		}

		isEnableLoadMore = enable;
		mInnerListView.changeLoadMoreView();
	}

	@Override
	public void enableAutoLoadMore(boolean enable) {
		// TODO Auto-generated method stub
		isEnableAutoLoadMore = enable;
	}

	@Override
	public boolean isEnableFixBar() {
		return isEnableFixBar;
	}
	
	@Override
	public boolean isEnableSwipe() {
		return isEnableSwipe;
	}

	@Override
	public boolean isEnableAutoCloseSwipe() {
		return isEnableAutoCloseSwipe;
	}

	@Override
	public boolean isEnablePullRefresh() {
		return isEnablePullRefresh;
	}

	@Override
	public boolean isEnableLoadMore() {
		return isEnableLoadMore;
	}

	@Override
	public boolean isEnableAutoLoadMore() {
		// TODO Auto-generated method stub
		return isEnableAutoLoadMore;
	}

	@Override
	public int getPullRefreshState() {
		// TODO Auto-generated method stub
		return mPullRefreshState;
	}

	@Override
	public void completeLoadMore() {
		// TODO Auto-generated method stub
		if (mMoreState == MORE_STATE_LOADING) {
			mMoreState = MORE_STATE_NONE;
			if (null != eventTrigger) {
				eventTrigger.onCompleteLoadMore(moreView);
			}
		}
	}

	@Override
	public int getLoadMoreState() {
		// TODO Auto-generated method stub
		return mMoreState;
	}

	@Override
	public int getFixBarMode() {
		return fixBarMode;
	}
	
	@Override
	public void setFixBarMode(int fixBarMode){
		this.fixBarMode = fixBarMode;
		resizeFixBar();
	}
	
	@Override
	public int getPullHeaderMode() {
		return pullHeaderMode;
	}

	@Override
	public void setPullHeaderMode(int pullHeaderMode) {
		this.pullHeaderMode = pullHeaderMode;
		if (pullHeaderMode == PULL_HEADER_MODE_MOVE) {
			if (mPullRefreshState == STATE_REFRESH) {
				headerL = pullHeaderView.getLeft();
				headerT = eventTrigger.getOnHeaderViewRefreshingHeight(pullHeaderView)-pullHeaderView.getMeasuredHeight();
				headerR = pullHeaderView.getRight();
				headerB = eventTrigger.getOnHeaderViewRefreshingHeight(pullHeaderView);
			} else {
				headerL = pullHeaderView.getLeft();
				headerT = -pullHeaderView.getMeasuredHeight();
				headerR = pullHeaderView.getRight();
				headerB = 0;
			}

		} else {
			if (mPullRefreshState == STATE_REFRESH) {
				headerL = pullHeaderView.getLeft();
				headerT = 0;
				headerR = pullHeaderView.getRight();
				headerB = eventTrigger.getOnHeaderViewRefreshingHeight(pullHeaderView);
			} else {
				headerL = pullHeaderView.getLeft();
				headerT = 0;
				headerR = pullHeaderView.getRight();
				headerB = pullHeaderView.getMeasuredHeight();
			}
			
		}

		requestLayout();
	}

	@Override
	public InnerListView getListView() {
		return this.mInnerListView;
	}
	
	@Override
	public int getFixBarMinHeight() {
		return fixBarMinHeight;
	}

	@Override
	public void setFixBarMinHeight(int fixBarMinHeight) {
		if(null!=fixBar){
			if(this.fixBarMinHeight==fixBarMinHeight||(this.fixBarMinHeight==fixBar.getMeasuredHeight()&&fixBarMinHeight>fixBar.getMeasuredHeight())){
				return;
			}
			
			if(fixBarMinHeight>fixBar.getMeasuredHeight()){
				this.fixBarMinHeight = fixBar.getMeasuredHeight();
			}else{
				this.fixBarMinHeight = fixBarMinHeight;
			}
			if(isEnableFixBar){
				resizeFixBar();
			}
		}
	}


	private void resizeFixBar() {
		if (null != fixBar) {
			
			if(isEnableFixBar){
				listHeaderView.setLayoutParams(new AbsListView.LayoutParams(
						LayoutParams.MATCH_PARENT,
						fixBar.getMeasuredHeight()));
				if (mPullRefreshState == STATE_REFRESH) {
					fixBarL = fixBar.getLeft();
					fixBarT = eventTrigger.getOnHeaderViewRefreshingHeight(pullHeaderView);
					fixBarR = fixBar.getMeasuredWidth();
					fixBarB = fixBar.getMeasuredHeight() + eventTrigger.getOnHeaderViewRefreshingHeight(pullHeaderView);
				} else {
					fixBarL = 0;
					fixBarT = 0;
					fixBarR = fixBar.getMeasuredWidth();
					fixBarB = fixBar.getMeasuredHeight();
				}
				
				if(null!=mFixBarListener){
					mFixBarListener.onHeightChange(fixBar, fixBar.getMeasuredHeight());
				}
			}else{
				listHeaderView.setLayoutParams(new AbsListView.LayoutParams(
						LayoutParams.MATCH_PARENT,
						0));
				fixBarL = 0;
				fixBarT = 0;
				fixBarR = fixBar.getMeasuredWidth();
				fixBarB = 0;
			}
			

			
			requestLayout();
			mInnerListView.smoothScrollToPosition(0);
		}
	}

	@Override
	public View getFixBar() {
		// TODO Auto-generated method stub
		return this.fixBar;
	}

	@Override
	public View getMoreView() {
		// TODO Auto-generated method stub
		return this.moreView;
	}

	@Override
	public View getPullHeaderView() {
		// TODO Auto-generated method stub
		return this.pullHeaderView;
	}
}
