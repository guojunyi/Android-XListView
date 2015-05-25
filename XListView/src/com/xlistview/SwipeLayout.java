package com.xlistview;

import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;

import com.nineoldandroids.animation.ValueAnimator;
import com.nineoldandroids.animation.ValueAnimator.AnimatorUpdateListener;

public class SwipeLayout extends ViewGroup {
	Context mContext;
	private int mTouchSlop;
	private int xDiff, yDiff;
	private boolean mIsDragged;
	private int mLastMotionX, mLastMotionY;
	private boolean flagY;

	private View fontView;
	private View backView;
	private boolean isOpen;
	private float sizeFactor;
	private boolean swipeEnable;

	private SwipeLayoutListener mSwipeLayoutListener;
	private XListView listView;
	boolean isAnimating;

	private int position;

	public SwipeLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		mContext = context;
		initView();
	}

	public void initView() {
		isOpen = false;
		swipeEnable = true;
		isAnimating = false;
		final ViewConfiguration configuration = ViewConfiguration.get(mContext);
		final Resources res = mContext.getResources();
		final DisplayMetrics metrics = res.getDisplayMetrics();

		final float density = metrics.density;
		sizeFactor = density * 20;
		mTouchSlop = configuration.getScaledTouchSlop();
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		// TODO Auto-generated method stub
		if (getChildCount() > 1) {
			if (isOpen) {
				fontView.layout(
						-backView.getMeasuredWidth(),
						0,
						fontView.getMeasuredWidth()
								- backView.getMeasuredWidth(),
						fontView.getMeasuredHeight());
			} else {

				fontView.layout(0, 0, fontView.getMeasuredWidth(),
						fontView.getMeasuredHeight());
			}

			backView.layout(
					this.getMeasuredWidth() - backView.getMeasuredWidth(), 0,
					this.getMeasuredWidth(), backView.getMeasuredHeight());
		} else {
			if (null != fontView) {
				fontView.layout(0, 0, fontView.getMeasuredWidth(),
						fontView.getMeasuredHeight());
			}
		}

	}

	@Override
	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		measureChildren(widthMeasureSpec, heightMeasureSpec);

		if (getChildCount() > 1) {
			int height = fontView.getMeasuredHeight() > backView
					.getMeasuredHeight() ? fontView.getMeasuredHeight()
					: backView.getMeasuredHeight();
			setMeasuredDimension(getMeasuredWidth(), height);
		} else {
			setMeasuredDimension(getMeasuredWidth(),
					fontView.getMeasuredHeight());
		}

	}

	@Override
	public void onFinishInflate() {
		super.onFinishInflate();

		if (getChildCount() > 1) {
			backView = this.getChildAt(0);
			fontView = this.getChildAt(1);
			swipeEnable = true;
		} else if (getChildCount() > 0) {
			fontView = this.getChildAt(0);
			fontView.setEnabled(true);
			fontView.setFocusable(true);
			fontView.setClickable(true);
			swipeEnable = false;
		} else {
			swipeEnable = false;
		}
	}

	public XListView getListView() {
		return listView;
	}

	public void setListView(XListView listView) {
		this.listView = listView;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub

		if (null != listView && !listView.isEnableSwipe()) {
			return super.onInterceptTouchEvent(ev);
		}

		if (!swipeEnable) {
			return super.onTouchEvent(ev);
		}

		if (isOpen) {
			if (ev.getRawX() < this.getMeasuredWidth()
					- backView.getMeasuredWidth()) {
				close(true);
				return true;
			} else {
				return super.onInterceptTouchEvent(ev);
			}

		} else {
			if (null != listView && listView.isEnableAutoCloseSwipe()
					&& null != listView.getListAdapter()) {
				if (listView.getListAdapter() instanceof BaseSwipeAdapter) {
					BaseSwipeAdapter adapter = (BaseSwipeAdapter) listView
							.getListAdapter();
					if (adapter.existOpenSwipeLayout()) {
						adapter.closeAllSwipeLayout();
						return true;
					}
				}
				
			}
		}

		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mLastMotionX = (int) ev.getRawX();
			mLastMotionY = (int) ev.getY();
			break;

		case MotionEvent.ACTION_MOVE:

			if (!isAnimating) {
				xDiff = (int) (ev.getRawX() - mLastMotionX);
				yDiff = Math.abs((int) (ev.getY() - mLastMotionY));

				if (!mIsDragged && Math.abs(xDiff) > mTouchSlop) {
					if (null != mSwipeLayoutListener) {
						mSwipeLayoutListener.onStartDragging();
					}
					mIsDragged = true;
					mLastMotionX = (int) ev.getRawX();
					return true;
				}
			}

			break;
		}

		return super.onInterceptTouchEvent(ev);
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if (null != listView && !listView.isEnableSwipe()) {
			return super.onTouchEvent(ev);
		}

		if (!swipeEnable) {
			return super.onTouchEvent(ev);
		}

		if (isOpen) {
			if (ev.getRawX() < this.getMeasuredWidth()
					- backView.getMeasuredWidth()) {
				close(true);
			}

			return false;
		}

		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mLastMotionX = (int) ev.getRawX();
			mLastMotionY = (int) ev.getY();
			break;

		case MotionEvent.ACTION_MOVE:
			if (!isAnimating) {
				xDiff = (int) (ev.getRawX() - mLastMotionX);
				yDiff = Math.abs((int) (ev.getY() - mLastMotionY));
				if (mIsDragged) {
					moveX(xDiff);

					mLastMotionX = (int) ev.getRawX();
				}

				if (!flagY && !mIsDragged && yDiff > mTouchSlop) {
					flagY = true;
					mIsDragged = false;
				}

				if (!flagY && !mIsDragged && Math.abs(xDiff) > mTouchSlop) {
					if (null != mSwipeLayoutListener) {
						mSwipeLayoutListener.onStartDragging();
					}
					mIsDragged = true;
					mLastMotionX = (int) ev.getRawX();
				}
			}
			break;
		case MotionEvent.ACTION_UP:
			if (mIsDragged && null != mSwipeLayoutListener) {
				mSwipeLayoutListener.onStopDragging();
			}

			if (!isOpen && mIsDragged) {
				if (-fontView.getLeft() > sizeFactor) {
					open(true);
				} else {
					close(true);
				}
			}
			mIsDragged = false;
			flagY = false;

			break;
		case MotionEvent.ACTION_CANCEL:
			if (mIsDragged && null != mSwipeLayoutListener) {
				mSwipeLayoutListener.onStopDragging();
			}
			mIsDragged = false;
			flagY = false;

			close(true);
			break;
		}

		return !flagY || mIsDragged;
	}

	private void moveX(int diff) {
		if (fontView.getLeft() >= 0 && diff > 0) {
			fontView.layout(0, fontView.getTop(), fontView.getMeasuredWidth(),
					fontView.getBottom());
			return;
		}

		if (-fontView.getLeft() >= backView.getMeasuredWidth() && diff < 0) {
			fontView.layout(-backView.getMeasuredWidth(), fontView.getTop(),
					fontView.getMeasuredWidth() - backView.getMeasuredWidth(),
					fontView.getBottom());
			return;
		}

		if (fontView.getLeft() + diff > 0) {
			fontView.layout(0, fontView.getTop(), fontView.getMeasuredWidth(),
					fontView.getBottom());
			return;
		}

		if (-(fontView.getLeft() + diff) > backView.getMeasuredWidth()) {
			fontView.layout(-backView.getMeasuredWidth(), fontView.getTop(),
					fontView.getMeasuredWidth() - backView.getMeasuredWidth(),
					fontView.getBottom());
			return;
		}

		fontView.layout(fontView.getLeft() + diff, fontView.getTop(),
				fontView.getMeasuredWidth() + fontView.getLeft() + diff,
				fontView.getBottom());

	}

	Handler mHandler = new Handler(Looper.getMainLooper()) {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			fontView.layout(msg.what, fontView.getTop(),
					fontView.getMeasuredWidth() + msg.what,
					fontView.getBottom());
		}

	};

	public void close(boolean isAnimate) {
		if (isAnimate) {
			isAnimating = true;
			ValueAnimator animator = ValueAnimator.ofInt(fontView.getLeft(), 0);
			animator.setDuration(200);
			animator.addUpdateListener(new AnimatorUpdateListener() {

				@Override
				public void onAnimationUpdate(ValueAnimator animation) {
					// TODO Auto-generated method stub
					int frameValue = (Integer) animation.getAnimatedValue();
					mHandler.sendEmptyMessage(frameValue);
					if (frameValue == 0) {
						isAnimating = false;
						if (isOpen && null != mSwipeLayoutListener) {
							mSwipeLayoutListener.onClose();
						}
						isOpen = false;
					}
				}
			});
			animator.start();
		} else {
			isOpen = false;
			if (isOpen && null != mSwipeLayoutListener) {
				mSwipeLayoutListener.onClose();
			}
			requestLayout();

		}

	}

	public void open(boolean isAnimate) {
		if (isAnimate) {
			isAnimating = true;
			ValueAnimator animator = ValueAnimator.ofInt(fontView.getLeft(),
					-backView.getMeasuredWidth());
			animator.setDuration(200);
			animator.addUpdateListener(new AnimatorUpdateListener() {

				@Override
				public void onAnimationUpdate(ValueAnimator animation) {
					// TODO Auto-generated method stub
					int frameValue = (Integer) animation.getAnimatedValue();
					mHandler.sendEmptyMessage(frameValue);
					if (frameValue == -backView.getMeasuredWidth()) {
						if (!isOpen && null != mSwipeLayoutListener) {
							mSwipeLayoutListener.onOpen();
						}
						isOpen = true;
						isAnimating = false;
					}
				}
			});
			animator.start();
		} else {
			requestLayout();
			if (!isOpen && null != mSwipeLayoutListener) {
				mSwipeLayoutListener.onOpen();
			}
			isOpen = true;
		}

	}

	public void setSwipeEnable(boolean enable) {
		if (null != backView) {
			swipeEnable = enable;
			if (swipeEnable == false) {
				close(true);
			}
		} else {

		}
	}

	public interface SwipeLayoutListener {
		public void onOpen();

		public void onClose();

		public void onStartDragging();

		public void onStopDragging();

		public void onTouch();
	}

	public void setSwipeLayoutListener(SwipeLayoutListener swipeLayoutListener) {
		this.mSwipeLayoutListener = swipeLayoutListener;
	}

	public boolean isOpened() {
		return this.isOpen;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	@Override
	public void setOnClickListener(OnClickListener listener) {
		throw new IllegalArgumentException(
				"SwipeLayout not support setOnClickListener.");
	}

	@Override
	public void setOnLongClickListener(OnLongClickListener listener) {
		throw new IllegalArgumentException(
				"SwipeLayout not support setOnLongClickListener.");
	}

	@Override
	public void setOnTouchListener(OnTouchListener listener) {
		throw new IllegalArgumentException(
				"SwipeLayout not support setOnTouchListener.");
	}
}
