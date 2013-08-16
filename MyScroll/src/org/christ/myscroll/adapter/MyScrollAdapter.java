package org.christ.myscroll.adapter;

import org.christ.myscroll.R;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

public class MyScrollAdapter extends BaseAdapter {
	private Context context;

	public MyScrollAdapter(Context context) {
		this.context = context;
	}

	@Override
	public int getCount() {

		return 16;
	}

	@Override
	public Object getItem(int position) {

		return null;
	}

	@Override
	public long getItemId(int position) {

		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ImageView image = new ImageView(context);
		image.setImageResource(R.drawable.coach_1);
		image.setScaleType(ScaleType.CENTER_INSIDE);
		// image.setBackgroundColor(Color.RED);
		image.setLayoutParams(new LayoutParams(300, 700));
		return image;
	}

}
