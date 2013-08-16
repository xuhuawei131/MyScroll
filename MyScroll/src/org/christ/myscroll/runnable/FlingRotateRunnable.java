package org.christ.myscroll.runnable;

import org.christ.myscroll.Rotator;
import org.christ.myscroll.costomViews.MyScrollView;

import android.content.Context;
import android.widget.BaseAdapter;

public class FlingRotateRunnable implements Runnable {
	private MyScrollView<? extends BaseAdapter> view;
	private int mAnimationDuration = 200;
	private boolean mShouldStopFling;

	/**
	 * Tracks the decay of a fling rotation
	 */
	public Rotator mRotator;

	/**
	 * Angle value reported by mRotator on the previous fling
	 */
	private float mLastFlingAngle;

	/**
	 * Constructor
	 */
	public FlingRotateRunnable(Context context, MyScrollView<? extends BaseAdapter> view) {
		mRotator = new Rotator(context);
		this.view = view;
	}

	private void startCommon() {
		// Remove any pending flings
		view.removeCallbacks(this);
	}

	public void startUsingVelocity(float initialVelocity) {
		if (initialVelocity == 0)
			return;

		startCommon();

		mLastFlingAngle = 0.0f;

		mRotator.fling(initialVelocity);

		view.post(this);
	}

	public void startUsingDistance(float deltaAngle) {
		if (deltaAngle == 0)
			return;

		startCommon();

		mLastFlingAngle = 0;
		synchronized (this) {
			mRotator.startRotate(0.0f, -deltaAngle, mAnimationDuration);
		}
		view.post(this);
	}

	public void stop(boolean scrollIntoSlots) {
		view.removeCallbacks(this);
		endFling(scrollIntoSlots);
	}

	private void endFling(boolean scrollIntoSlots) {
		/*
		 * Force the scroller's status to finished (without setting its position to the end)
		 */
		synchronized (this) {
			mRotator.forceFinished(true);
		}

		if (scrollIntoSlots)
			view.scrollIntoSlots();
	}

	public void run() {
		if (view.getChildCount() == 0) {
			endFling(true);
			return;
		}

		mShouldStopFling = false;

		final Rotator rotator;
		final float angle;
		boolean more;
		synchronized (this) {
			rotator = mRotator;
			more = rotator.computeAngleOffset();
			angle = rotator.getCurrAngle();
		}

		// Flip sign to convert finger direction to list items direction
		// (e.g. finger moving down means list is moving towards the top)
		float delta = mLastFlingAngle - angle;

		// ////// Shoud be reworked
		view.trackMotionScroll(delta);

		if (more && !mShouldStopFling) {
			mLastFlingAngle = angle;
			view.post(this);
		} else {
			mLastFlingAngle = 0.0f;
			endFling(true);
		}

	}

}