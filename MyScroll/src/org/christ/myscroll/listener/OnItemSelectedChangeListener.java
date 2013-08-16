package org.christ.myscroll.listener;

import org.christ.myscroll.costomViews.MyScrollView;

import android.widget.BaseAdapter;

public interface OnItemSelectedChangeListener {

	public void onItemChange(MyScrollView<? extends BaseAdapter> view, int positon, int oldPosition);

}
