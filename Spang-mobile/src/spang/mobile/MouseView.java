package spang.mobile;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import network.IConnection;
import sensors.SensorProcessor;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public class MouseView extends View{
	private final Paint paint = new Paint();
	private final boolean multiTouchEnabled = Integer.parseInt(Build.VERSION.SDK) <= Build.VERSION_CODES.CUPCAKE;
	
	private float xPos, yPos;

	private boolean scrolling;

	private GestureDetector gestureDetector;

	private IConnection connection;

	private SensorProcessor sp;

	public MouseView(Context context, AttributeSet attrs, IConnection connection) {
		super(context, attrs);


		paint.setAntiAlias(true);
		paint.setStrokeWidth(6f);
		paint.setColor(Color.BLACK);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeJoin(Paint.Join.ROUND);

		this.gestureDetector = new GestureDetector(context, simpleOnGestureListener);

		this.connection= connection;

		this.sp = new SensorProcessor(context, connection);
		this.sp.setActive(Sensor.TYPE_LINEAR_ACCELERATION, true);
		this.sp.startProcess();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawCircle(xPos, yPos, 42, paint);
	}

	@SuppressLint("NewApi")
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		gestureDetector.onTouchEvent(event);
		xPos = event.getX();
		yPos = event.getY();
		
		// Schedules a repaint.
		invalidate();
		return true;
	}

	private GestureDetector.SimpleOnGestureListener simpleOnGestureListener = new GestureDetector.SimpleOnGestureListener(){

		@SuppressLint("NewApi")
		public boolean onDown(MotionEvent e) {
			Log.d("MOTIONEVENT:", "onDown");
			if(multiTouchEnabled && e.getPointerCount()==2)
				scrolling=true;
			return true;
		}

		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			Log.d("MOTIONEVENT:", "onFling");
			scrolling = false;
			return true;
		}

		public void onLongPress(MotionEvent e) {
			Log.d("MOTIONEVENT:", "onLongPress");
			connection.sendUDP(new byte[]{(byte)1});
		}

		public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
				float distanceY) {
			if(scrolling){
				//Vertical Scroll
				Log.d("MOTIONEVENT:", "onScroll");
				byte[] vertData = ByteBuffer.allocate(5).order(ByteOrder.LITTLE_ENDIAN)
						.put((byte)11).putInt((int)distanceY).array();
				connection.sendUDP(vertData);


				//Horizontal Scroll
				byte[] horiData = ByteBuffer.allocate(5).order(ByteOrder.LITTLE_ENDIAN)
						.put((byte)12).putInt((int)distanceX).array();
				connection.sendUDP(horiData);
			}else{
				Log.d("MOTIONEVENT:", "dX = " + distanceX + "   dY = " + distanceY);
				byte[] data = ByteBuffer.allocate(9).order(ByteOrder.LITTLE_ENDIAN)
						.put((byte)2).putInt((int)distanceX).putInt((int)distanceY).array();
				connection.sendUDP(data);
			}
			return true;
		}

		public void onShowPress(MotionEvent e) {
			Log.d("MOTIONEVENT:", "onShowPress");
		}

		public boolean onSingleTapUp(MotionEvent e) {
			Log.d("MOTIONEVENT:", "onSingleTapUp");
			connection.sendUDP(new byte[]{(byte)0});
			return true;
		}
	};
}

