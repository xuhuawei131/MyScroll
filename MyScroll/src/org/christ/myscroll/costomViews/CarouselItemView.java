package org.christ.myscroll.costomViews;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class CarouselItemView extends LinearLayout implements Comparable<CarouselItemView> {

	private int index;
	private float currentAngle;
	private float x;
	private float y;
	private float z;
	private boolean drawn;

	public CarouselItemView(Context context) {
		this(context, null, 0);
	}

	public CarouselItemView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public CarouselItemView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public int getIndex() {
		return index;
	}

	public void setCurrentAngle(float currentAngle) {
		this.currentAngle = currentAngle;
	}

	public float getCurrentAngle() {
		return currentAngle;
	}

	public int compareTo(CarouselItemView another) {
		return (int) (another.z - this.z);
	}

	public void setXpx(float x) {
		this.x = x;
	}

	public float getXpx() {
		return x;
	}

	public void setYpx(float y) {
		this.y = y;
	}

	public float getYpx() {
		return y;
	}

	public void setZpx(float z) {
		this.z = z;
	}

	public float getZpx() {
		return z;
	}

	public void setDrawn(boolean drawn) {
		this.drawn = drawn;
	}

	public boolean isDrawn() {
		return drawn;
	}

}
