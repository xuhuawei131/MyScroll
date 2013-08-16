package org.christ.myscroll.listener;

import org.christ.myscroll.costomViews.MyScrollView;

import android.widget.BaseAdapter;

public interface OnSelectedListener {
	public void OnItemClick(MyScrollView<? extends BaseAdapter> view, int position);
}
