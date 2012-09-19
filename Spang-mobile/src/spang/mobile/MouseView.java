package spang.mobile;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public class MouseView extends View{
	private final Paint paint = new Paint();

	private float previousTouchX;
	private float previousTouchY;
	private float dX;
	private float dY;

	private GestureDetector gestureDetector;

	public MouseView(Context context, AttributeSet attrs) {
		super(context, attrs);

		paint.setAntiAlias(true);
		paint.setStrokeWidth(6f);
		paint.setColor(Color.WHITE);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeJoin(Paint.Join.ROUND);
		
		 this.gestureDetector = new GestureDetector(context, simpleOnGestureListener);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawLine(previousTouchX, previousTouchY, previousTouchX + dX, previousTouchY + dY, paint);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		gestureDetector.onTouchEvent(event);
		
		float eventX = event.getX();
		float eventY = event.getY();

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			previousTouchX = eventX;
			previousTouchY = eventY;
			return true;
		case MotionEvent.ACTION_MOVE:
			dX = previousTouchX + eventX;
			dY = previousTouchY - eventY;
			previousTouchX = eventX;
			previousTouchY = eventY;

			this.sendMovementData();
			break;
		case MotionEvent.ACTION_UP:
			this.dX = 0;
			this.dY = 0;
			break;
		default:
			return false;
		}

		// Schedules a repaint.
		invalidate();
		return true;
	}

	private void sendMovementData() {
		Log.d("MOTIONEVENT:", "dX = " + dX + "   dY = " + dY);
	}

	GestureDetector.SimpleOnGestureListener simpleOnGestureListener = new GestureDetector.SimpleOnGestureListener(){

		public boolean onDown(MotionEvent e) {
			Log.d("MOTIONEVENT:", "onDown");
			return true;
		}

		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			Log.d("MOTIONEVENT:", "onFling");
			return true;
		}

		public void onLongPress(MotionEvent e) {
			Log.d("MOTIONEVENT:", "onLongPress");
		}

		public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
				float distanceY) {
			Log.d("MOTIONEVENT:", "onScroll");
			return true;
		}

		public void onShowPress(MotionEvent e) {
			Log.d("MOTIONEVENT:", "onShowPress");
		}

		public boolean onSingleTapUp(MotionEvent e) {
			Log.d("MOTIONEVENT:", "onSingleTapUp");
			return true;
		}
	};
} 
