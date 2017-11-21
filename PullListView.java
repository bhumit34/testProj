package com.schindler.schindler.tag;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;
import com.schindler.schindler.tag.StatusView.RefreshStatus;

@SuppressLint("ClickableViewAccessibility")
@SuppressWarnings("unused")
public class PullListView extends ListView implements OnScrollListener {

	/**
	 * log tag
	 */
	public final static String TAG = "SwipeListView";

	/**
	 * whether debug
	 */
	public final static boolean DEBUG = false;

	/**
	 * Used when user want change swipe list mode on some rows
	 */
	public final static int SWIPE_MODE_DEFAULT = -1;

	/**
	 * Disables all swipes
	 */
	public final static int SWIPE_MODE_NONE = 0;

	/**
	 * Enables both left and right swipe
	 */
	public final static int SWIPE_MODE_BOTH = 1;

	/**
	 * Enables right swipe
	 */
	public final static int SWIPE_MODE_RIGHT = 2;

	/**
	 * Enables left swipe
	 */
	public final static int SWIPE_MODE_LEFT = 3;

	/**
	 * Binds the swipe gesture to reveal a view behind the row (Drawer style)
	 */
	public final static int SWIPE_ACTION_REVEAL = 0;

	/**
	 * Dismisses the cell when swiped over
	 */
	public final static int SWIPE_ACTION_DISMISS = 1;

	/**
	 * Marks the cell as checked when swiped and release
	 */
	public final static int SWIPE_ACTION_CHOICE = 2;

	/**
	 * No action when swiped
	 */
	public final static int SWIPE_ACTION_NONE = 3;

	/**
	 * Default ids for front view
	 */
	public final static String SWIPE_DEFAULT_FRONT_VIEW = "swipelist_frontview";

	/**
	 * Default id for back view
	 */
	public final static String SWIPE_DEFAULT_BACK_VIEW = "swipelist_backview";

	/**
	 * Indicates no movement
	 */
	private final static int TOUCH_STATE_REST = 0;

	/**
	 * State scrolling x position
	 */
	private final static int TOUCH_STATE_SCROLLING_X = 1;

	/**
	 * State scrolling y position
	 */
	private final static int TOUCH_STATE_SCROLLING_Y = 2;

	private int touchState = TOUCH_STATE_REST;

	private float lastMotionX;
	private float lastMotionY;
	private int touchSlop;

	int swipeFrontView = 0;
	int swipeBackView = 0;

	/**
	 * Internal listener for common swipe events
	 */

	/**
	 * Internal touch listener
	 */

	public interface PullListViewListener {

		public boolean onRefreshOrMore(PullListView dynamicListView, boolean isRefresh);
	}

	private StatusView refreshView;
	private StatusView moreView;
	private int itemFlag = -1;
	private boolean isRecorded = false;
	private int downY = -1;
	private final float minTimesToRefresh = 1.5f;
	private final static int ITEM_FLAG_FIRST = 1;
	private final static int ITEM_FLAG_NONE = 0;
	private final static int ITEM_FLAG_LAST = -1;

	private PullListViewListener onRefreshListener;
	private PullListViewListener onMoreListener;
	private boolean doMoreWhenBottom = false;

	public PullListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initThis(context);
	}

	public PullListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initThis(context);
	}

	public PullListView(Context context, int swipeBackView, int swipeFrontView) {
		super(context);
		initThis(context);
	}

	private void initThis(Context context)
	{
		refreshView = new StatusView(context);
		moreView = new StatusView(context);
		refreshView.setStatusStrings("", "Pull to refresh...", "Loading...");
		moreView.setStatusStrings("", "Pull to load...", "Loading...");
		this.addHeaderView(refreshView, null, false);
		this.addFooterView(moreView, null, false);

		this.setOnScrollListener(this);

		doneRefresh();
		doneMore();

	}

	public PullListViewListener getOnRefreshListener()
	{
		return onRefreshListener;
	}

	public void setOnRefreshListener(PullListViewListener onRefreshListener)
	{
		this.onRefreshListener = onRefreshListener;
	}

	public PullListViewListener getOnMoreListener()
	{
		return onMoreListener;
	}

	public void setOnMoreListener(PullListViewListener onMoreListener)
	{
		this.onMoreListener = onMoreListener;
	}

	public boolean getDoMoreWhenBottom()
	{
		return doMoreWhenBottom;
	}

	public void setDoMoreWhenBottom(boolean doMoreWhenBottom)
	{
		this.doMoreWhenBottom = doMoreWhenBottom;
	}

	@Override
	public void onScroll(AbsListView l, int t, int oldl, int count)
	{
		if (t == 0)
			itemFlag = ITEM_FLAG_FIRST;
		else if ((t + oldl) == count)
		{
			itemFlag = ITEM_FLAG_LAST;
			if (doMoreWhenBottom && onMoreListener != null && moreView.getRefreshStatus() != RefreshStatus.refreshing)
			{
				doMore();
			}
		}
		else
		{
			itemFlag = ITEM_FLAG_NONE;
		}
	}

	@Override
	public void onScrollStateChanged(AbsListView arg0, int arg1)
	{

	}

	@Override
	public boolean onTouchEvent(MotionEvent ev)
	{
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			if (isRecorded == false && (itemFlag == ITEM_FLAG_FIRST && onRefreshListener != null && refreshView.getRefreshStatus() == RefreshStatus.normal
					|| itemFlag == ITEM_FLAG_LAST && onMoreListener != null && moreView.getRefreshStatus() == RefreshStatus.normal))
			{
				downY = (int) ev.getY(0);
				isRecorded = true;
			}
			break;
		case MotionEvent.ACTION_UP:
		{
			isRecorded = false;
			if (onRefreshListener != null && refreshView.getRefreshStatus() == RefreshStatus.willrefresh)
			{
				doRefresh();
			}
			else if (refreshView.getRefreshStatus() == RefreshStatus.normal)
			{
				refreshView.setPadding(0, -1 * refreshView.height, 0, 0);
			}

			if (onMoreListener != null && moreView.getRefreshStatus() == RefreshStatus.willrefresh)
			{
				doMore();
			}
			else if (moreView.getRefreshStatus() == RefreshStatus.normal)
			{
				moreView.setPadding(0, 0, 0, -1 * moreView.height);
			}
			break;
		}
		case MotionEvent.ACTION_MOVE:
		{
			if (isRecorded == false && (itemFlag == ITEM_FLAG_FIRST && onRefreshListener != null && refreshView.getRefreshStatus() == RefreshStatus.normal
					|| itemFlag == ITEM_FLAG_LAST && onMoreListener != null && moreView.getRefreshStatus() == RefreshStatus.normal))
			{
				downY = (int) ev.getY(0);
				isRecorded = true;
			}
			else if (isRecorded)
			{
				int nowY = (int) ev.getY(0);
				int offset = nowY - downY;
				if (offset > 0 && itemFlag == ITEM_FLAG_FIRST)
				{
					setSelection(0);
					if (offset >= (minTimesToRefresh * refreshView.height))
					{
						refreshView.setRefreshStatus(RefreshStatus.willrefresh);
					}
					else
					{
						refreshView.setRefreshStatus(RefreshStatus.normal);
					}

					refreshView.setPadding(0, -1 * (refreshView.height - offset), 0, 0);
				}
				else if (itemFlag == ITEM_FLAG_LAST)
				{
					setSelection(this.getCount());
					if (offset <= -1 * (minTimesToRefresh * moreView.height))
					{
						moreView.setRefreshStatus(RefreshStatus.willrefresh);
					}
					else
					{
						moreView.setRefreshStatus(RefreshStatus.normal);
					}
					moreView.setPadding(0, 0, 0, -1 * (moreView.height + offset));
				}
			}
			break;
		}
		default:
			break;
		}
		return super.onTouchEvent(ev);
	}

	private void doRefresh()
	{
		refreshView.setRefreshStatus(RefreshStatus.refreshing);
		refreshView.setPadding(0, 0, 0, 0);
		if (onRefreshListener.onRefreshOrMore(this, true))
			doneRefresh();
	}

	private void doMore()
	{
		moreView.setRefreshStatus(RefreshStatus.refreshing);
		moreView.setPadding(0, 0, 0, 0);
		if (onMoreListener.onRefreshOrMore(this, false))
			doneMore();
	}

	public void doneRefresh()
	{
		refreshView.setRefreshStatus(RefreshStatus.normal);
		refreshView.setPadding(0, -1 * refreshView.height, 0, 0);
	}

	public void doneMore()
	{
		moreView.setRefreshStatus(RefreshStatus.normal);
		moreView.setPadding(0, 0, 0, -1 * moreView.height);
	}

	public RefreshStatus getRefreshStatus()
	{
		return refreshView.getRefreshStatus();
	}

	public RefreshStatus getMoreStatus()
	{
		return moreView.getRefreshStatus();
	}

}
