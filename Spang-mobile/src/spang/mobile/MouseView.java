package spang.mobile;

import java.net.InetAddress;
import java.net.UnknownHostException;

import network.Client;
import network.IConnection;
import network.NetworkException;
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
	
//	private SensorProcessor processor;

	private GestureDetector gestureDetector;
	
	private static final int PORT = 1337;
	private static final String ADDR = "129.16.177.89";
	private IConnection connection;

	public MouseView(Context context, AttributeSet attrs) {
		super(context, attrs);

		paint.setAntiAlias(true);
		paint.setStrokeWidth(6f);
		paint.setColor(Color.BLACK);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeJoin(Paint.Join.ROUND);
		
		this.gestureDetector = new GestureDetector(context, simpleOnGestureListener);
		
		
		Client client = new Client();
	       
        try {
			this.connection= client.connectTo(InetAddress.getByName(ADDR), PORT);
		} catch (UnknownHostException e) {
			throw new NetworkException(e);
		}
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
			dX = previousTouchX - eventX;
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
    	String message = "dx" + dX + "dy" + dY;
    	byte[] data = message.getBytes();
    	connection.sendUDP(data);
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
	    	String message = "click";
	    	byte[] data = message.getBytes();
	    	connection.sendUDP(data);
			return true;
		}
	};
} 
