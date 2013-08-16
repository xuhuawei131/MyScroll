package org.christ.myscroll.costomViews;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.christ.myscroll.listener.OnItemSelectedChangeListener;
import org.christ.myscroll.listener.OnSelectedListener;
import org.christ.myscroll.runnable.FlingRotateRunnable;

import android.content.Context;
import android.graphics.Camera;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Transformation;
import android.widget.BaseAdapter;

public class MyScrollView<T extends BaseAdapter> extends ViewGroup implements GestureDetector.OnGestureListener {
	private static int OFFSET_LEFT;
	private static int OFFSET_TOP;
	private T mAdapter;
	private Camera mCamera;
	private float mTheta = (float) (15.0f * (Math.PI / 180.0));
	private int mSelectedPosition = 0;
	private GestureDetector mGestureDetector;
	private OnItemSelectedChangeListener mItemSelectedListener;
	private OnSelectedListener mSelectedListener;

	private FlingRotateRunnable mFlingRunnable;
	private Rect mTouchFrame;
	private int mDownTouchPosition;
	private CarouselItemView mDownTouchView;

	// 构造方法开始

	public MyScrollView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mCamera = new Camera();
		mGestureDetector = new GestureDetector(this);
		setChildrenDrawingOrderEnabled(true);
		setStaticTransformationsEnabled(true);
		mFlingRunnable = new FlingRotateRunnable(getContext(), this);
	}

	public MyScrollView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public MyScrollView(Context context) {
		this(context, null);
	}

	// 构造方法结束

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		OFFSET_LEFT = getWidth() / 2;
		OFFSET_TOP = getHeight() / 2;
		int count = getChildCount();
		float angleUnit = 360.0f / count;

		float angleOffset = mSelectedPosition * angleUnit;
		for (int i = 0; i < count; i++) {
			final CarouselItemView child = (CarouselItemView) getChildAt(i);
			if (child.getVisibility() != View.GONE) {
				child.measure(0, 0);
				float angle = angleUnit * i - angleOffset;
				if (angle < 0.0f)
					angle = 360.0f + angle;
				child.setCurrentAngle(angle);
				Calculate3DPosition(child, (int) (child.getMeasuredWidth() * count / 8), angle);
				layoutView(child);
			}
		}
	}

	private void layoutView(CarouselItemView child) {
		int left = (int) child.getXpx() + OFFSET_LEFT - child.getMeasuredWidth() / 2;
		int top = OFFSET_TOP - child.getMeasuredHeight() / 2;
		int right = (int) child.getXpx() + child.getMeasuredWidth() + OFFSET_LEFT - child.getMeasuredWidth() / 2;
		int bottom = child.getMeasuredHeight() + (OFFSET_TOP - child.getMeasuredHeight() / 2);
		child.layout(left, top, right, bottom);
	}

	public void setAdapter(T adapter) {
		if (adapter == null) {
			throw new IllegalArgumentException("Adapter is null!");
		}
		this.mAdapter = adapter;
		removeAllViews();
		requestLayout();
		int count = mAdapter.getCount();
		for (int i = 0; i < count; i++) {
			CarouselItemView item = new CarouselItemView(getContext());
			item.addView(mAdapter.getView(i, null, this));
			item.setIndex(i);
			addView(item, i);
		}
	}

	public T getAdapter() {
		return mAdapter;
	}

	public void scrollIntoSlots() {

		// Nothing to do
		if (getChildCount() == 0)
			return;

		// get nearest item to the 0 degrees angle
		// Sort itmes and get nearest angle
		float angle;
		int position;

		ArrayList<CarouselItemView> arr = new ArrayList<CarouselItemView>();

		for (int i = 0; i < getAdapter().getCount(); i++)
			arr.add(((CarouselItemView) getChildAt(i)));

		Collections.sort(arr, new Comparator<CarouselItemView>() {
			@Override
			public int compare(CarouselItemView c1, CarouselItemView c2) {
				int a1 = (int) c1.getCurrentAngle();
				if (a1 > 180)
					a1 = 360 - a1;
				int a2 = (int) c2.getCurrentAngle();
				if (a2 > 180)
					a2 = 360 - a2;
				return (a1 - a2);
			}

		});

		angle = arr.get(0).getCurrentAngle();

		// Make it minimum to rotate
		if (angle > 180.0f)
			angle = -(360.0f - angle);

		// Start rotation if needed
		if (angle != 0.0f) {
			mFlingRunnable.startUsingDistance(-angle);
		} else {
			// Set selected position
			position = arr.get(0).getIndex();
			// setSelectedPositionInt(position);
			// onFinishedMovement();
		}

	}

	@Override
	protected boolean getChildStaticTransformation(View child, Transformation transformation) {

		transformation.clear();
		transformation.setTransformationType(Transformation.TYPE_MATRIX);

		// Center of the item
		float centerX = (float) child.getWidth() / 2, centerY = (float) child.getHeight() / 2;

		// Save camera
		mCamera.save();

		// Translate the item to it's coordinates
		final Matrix matrix = transformation.getMatrix();
		mCamera.translate(0, 0, ((CarouselItemView) child).getZpx());
		child.invalidate();

		// Align the item
		mCamera.getMatrix(matrix);
		matrix.preTranslate(-centerX, -centerY);
		matrix.postTranslate(centerX, centerY);

		// Restore camera
		mCamera.restore();

		return true;
	}

	private void Calculate3DPosition(CarouselItemView child, int diameter, float angleOffset) {
		angleOffset = angleOffset * (float) (Math.PI / 180.0f);

		float x = -(float) (diameter / 2 * Math.sin(angleOffset));
		float z = diameter / 2 * (1.0f - (float) Math.cos(angleOffset) * 0.8f);
		float y = -getHeight() / 4 + (float) (z * Math.sin(mTheta));

		child.setXpx(-x);
		child.setZpx(z);
		child.setYpx(y);

		if (child.getZpx() > 400) {
			child.setVisibility(View.INVISIBLE);
		} else {
			child.setVisibility(View.VISIBLE);
		}

	}

	@Override
	protected int getChildDrawingOrder(int childCount, int i) {

		// Sort Carousel items by z coordinate in reverse order
		ArrayList<CarouselItemView> sl = new ArrayList<CarouselItemView>();
		for (int j = 0; j < childCount; j++) {
			CarouselItemView view = (CarouselItemView) getChildAt(j);
			if (i == 0)
				view.setDrawn(false);
			sl.add((CarouselItemView) getChildAt(j));
		}

		Collections.sort(sl);
		int topIndex = sl.get(sl.size() - 1).getIndex();
		if (topIndex != mSelectedPosition) {
			if (mItemSelectedListener != null)
				mItemSelectedListener.onItemChange(this, topIndex, mSelectedPosition);
			mSelectedPosition = topIndex;
		}

		// Get first undrawn item in array and get result index
		int idx = 0;

		for (CarouselItemView civ : sl) {
			if (!civ.isDrawn()) {
				civ.setDrawn(true);
				idx = civ.getIndex();
				break;
			}
		}

		return idx;

	}

	public void trackMotionScroll(float deltaAngle) {
		int count = getChildCount();
		if (count == 0) {
			return;
		}

		for (int i = 0; i < getAdapter().getCount(); i++) {
			CarouselItemView child = (CarouselItemView) getChildAt(i);
			float angle = child.getCurrentAngle();
			angle += deltaAngle;
			while (angle > 360.0f)
				angle -= 360.0f;
			while (angle < 0.0f)
				angle += 360.0f;
			child.setCurrentAngle(angle);
			Calculate3DPosition(child, (int) (child.getMeasuredWidth() * count / 8), angle);
			layoutView(child);
		}
		// invalidate();
	}

	public void scrollToChild(int i) {

		CarouselItemView view = (CarouselItemView) getChildAt(i);
		float angle = view.getCurrentAngle();

		if (angle == 0)
			return;

		if (angle > 180.0f)
			angle = 360.0f - angle;
		else
			angle = -angle;

		mFlingRunnable.startUsingDistance(angle);

	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// Give everything to the gesture detector

		int action = event.getAction();
		if (action == MotionEvent.ACTION_UP) {
			// Helper method for lifted finger

			if (mDownTouchView != null) {
				if (mDownTouchView.equals(pointToView((int) event.getX(), (int) event.getY())))
					onSelected();
			}
			onUp();
		} else if (action == MotionEvent.ACTION_CANCEL) {
			onUp();
		} else if (action == MotionEvent.ACTION_DOWN) {
			removeCallbacks(mFlingRunnable);
			mFlingRunnable.mRotator.forceFinished(true);
		}

		boolean retValue = mGestureDetector.onTouchEvent(event);

		return retValue;
	}

	private void onUp() {
		if (mFlingRunnable.mRotator.isFinished()) {
			scrollIntoSlots();
		}
		dispatchUnpress();
	}

	private void dispatchUnpress() {

		for (int i = getChildCount() - 1; i >= 0; i--) {
			getChildAt(i).setPressed(false);
		}

		setPressed(false);
	}

	@Override
	public boolean onDown(MotionEvent e) {
		mDownTouchView = pointToView((int) e.getX(), (int) e.getY());
		if (mDownTouchView != null) {
			mDownTouchPosition = mDownTouchView.getIndex();
			mDownTouchView.setPressed(true);
		}
		return true;
	}

	@Override
	public void onShowPress(MotionEvent e) {
	}

	public void onSelected() {
		if (mDownTouchView == null)
			return;
		// An item tap should make it selected, so scroll to this child.
		scrollToChild(mDownTouchView.getIndex());

		// Also pass the click so the client knows, if it wants to.
		if (mDownTouchPosition == mSelectedPosition) {
			if (mSelectedListener != null) {
				mSelectedListener.OnItemClick(this, mSelectedPosition);
			}
		}
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {

		return false;
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
		removeCallbacks(mFlingRunnable);
		trackMotionScroll(/* -1 * */(int) -distanceX);
		return true;
	}

	@Override
	public void onLongPress(MotionEvent e) {
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
		mFlingRunnable.startUsingVelocity((int) -velocityX);
		return true;
	}

	public void setOnItemSelectedChangeListener(OnItemSelectedChangeListener listener) {
		this.mItemSelectedListener = listener;
	}

	public void setOnSelectedListener(OnSelectedListener listener) {
		this.mSelectedListener = listener;
	}

	public CarouselItemView pointToView(int x, int y) {
		// All touch events are applied to selected item
		Rect frame = mTouchFrame;
		if (frame == null) {
			mTouchFrame = new Rect();
			frame = mTouchFrame;
		}

		int childCount = getChildCount();
		ArrayList<CarouselItemView> sl = new ArrayList<CarouselItemView>();
		for (int j = 0; j < childCount; j++) {
			sl.add((CarouselItemView) getChildAt(j));
		}
		Collections.sort(sl);

		for (int i = childCount - 1; i > 0; i--) {
			CarouselItemView child = sl.get(i);
			if (child.getVisibility() == View.VISIBLE) {
				child.getHitRect(frame);
				if (frame.contains(x, y)) {
					return child;
				}
			}
		}
		return null;
	}

}