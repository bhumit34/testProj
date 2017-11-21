package com.schindler.schindler.tag;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

class StatusView extends LinearLayout {

	enum RefreshStatus {
		none, normal, willrefresh, refreshing
	}

	public int height;
	public int width;
	private ProgressBar progressBar = null;
	private TextView textView = null;
	private RefreshStatus refreshStatus = RefreshStatus.none;
	private String normalString = "����ˢ��";
	private String willrefreshString = "�ɿ�ˢ��";
	private String refreshingString = "����ˢ��";

	// private String refreshingString = "Loading...";

	public StatusView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initThis(context);
	}

	public StatusView(Context context) {
		super(context);
		initThis(context);
	}

	private void initThis(Context context) {
		this.setOrientation(LinearLayout.HORIZONTAL);
		this.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);

		progressBar = new ProgressBar(context);
		progressBar.setLayoutParams(new LayoutParams(30, 30));
		textView = new TextView(context);
		textView.setPadding(5, 0, 0, 0);

		this.addView(progressBar);
		this.addView(textView);

		int w = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
		int h = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
		this.measure(w, h);

		height = this.getMeasuredHeight();
		width = this.getMeasuredWidth();

		this.setRefreshStatus(RefreshStatus.normal);
	}

	public RefreshStatus getRefreshStatus() {
		return refreshStatus;
	}

	public void setRefreshStatus(RefreshStatus refreshStatus) {
		if (this.refreshStatus != refreshStatus)
		{
			this.refreshStatus = refreshStatus;
			if (refreshStatus == RefreshStatus.refreshing)
			{
				this.progressBar.setVisibility(View.VISIBLE);
			}
			else
			{
				this.progressBar.setVisibility(View.GONE);
			}
			refreshStatusString();
			this.invalidate();
		}
	}

	private void refreshStatusString() {
		switch (refreshStatus)
		{
		case normal:
			textView.setText(normalString);
			progressBar.setProgress(0);
			break;
		case willrefresh:
			textView.setText(willrefreshString);
			break;
		case refreshing:
			textView.setText(refreshingString);
			break;
		default:
			break;
		}
	}

	public void setStatusStrings(String normalString, String willrefreshString, String refreshingString) {
		this.normalString = normalString;
		this.willrefreshString = willrefreshString;
		this.refreshingString = refreshingString;
		this.refreshStatusString();
	}
}