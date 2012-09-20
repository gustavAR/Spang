package spang.mobile;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import network.IConnection;
import sensors.SensorProcessor;
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

public class MouseView extends AbstractSpangView{
	private final Paint paint = new Paint();
	private final boolean multiTouchEnabled = Integer.parseInt(Build.VERSION.SDK) >= Build.VERSION_CODES.CUPCAKE;

	private float xPos, yPos, radius;
	private float xPosPrev, yPosPrev;

	private boolean scrolling, normalInputAllowed = true;

	private GestureDetector gestureDetector;

	private SensorProcessor sp;

	public MouseView(Context context, AttributeSet attrs, IConnection connection) {
		super(context, attrs, connection);


		paint.setAntiAlias(true);
		paint.setStrokeWidth(6f);
		paint.setColor(Color.BLACK);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeJoin(Paint.Join.ROUND);

		this.gestureDetector = new GestureDetector(context, simpleOnGestureListener);

		this.sp = new SensorProcessor(context, connection);
		this.sp.setActive(Sensor.TYPE_LINEAR_ACCELERATION, true);
		this.sp.startProcess();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawCircle(xPos, yPos, radius, paint);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if(normalInputAllowed)
			gestureDetector.onTouchEvent(event);
		xPos = event.getX();
		yPos = event.getY();
		radius = event.getPressure()*100;

		int eventID = event.getAction();

		byte[] pressureData = ByteBuffer.allocate(5).order(ByteOrder.LITTLE_ENDIAN)
				.put((byte)13).putInt((int)radius).array();
		connection.sendUDP(pressureData);

		if(scrolling){
			//Vertical Scroll
			Log.d("MOTIONEVENT:", "onScroll");
			byte[] vertData = ByteBuffer.allocate(5).order(ByteOrder.LITTLE_ENDIAN)
					.put((byte)11).putInt((int)(4*(yPos - yPosPrev + 0.5f))).array();
			connection.sendUDP(vertData);

//			Horizontal Scroll
//			byte[] horiData = ByteBuffer.allocate(5).order(ByteOrder.LITTLE_ENDIAN)
//					.put((byte)12).putInt((int)(4*(xPos - xPosPrev + 0.5f))).array();
//			connection.sendUDP(horiData);
		}
		if((eventID & 0x000000ff) == MotionEvent.ACTION_POINTER_DOWN){
			Log.d("MOTIONEVENT:", "ACTION_POINTER_DOWN");
			scrolling=true;
			normalInputAllowed=false;
			return true;
		} 
		else if((eventID & 0x000000ff) == MotionEvent.ACTION_POINTER_UP){
			Log.d("MOTIONEVENT:", "ACTION_POINTER_UP");
			scrolling = false;
			inputTimer(100);
			return true;
		}
		// Schedules a repaint.
		invalidate();

		xPosPrev = xPos;
		yPosPrev = yPos;
		return true;
	}

	private void inputTimer(final int milliseconds){
		Runnable runnable = new Runnable() {
			public void run() {
				try {
					Thread.sleep(milliseconds);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				normalInputAllowed = true;
			}
		};
		new Thread(runnable).start();
	}

	private GestureDetector.SimpleOnGestureListener simpleOnGestureListener = new GestureDetector.SimpleOnGestureListener(){


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
			connection.sendUDP(new byte[]{(byte)1});
		}

		public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
				float distanceY) {
			Log.d("MOTIONEVENT:", "dX = " + distanceX + "   dY = " + distanceY);
			byte[] data = ByteBuffer.allocate(9).order(ByteOrder.LITTLE_ENDIAN)
					.put((byte)2).putInt((int)(distanceX + 0.5f)).putInt((int)(distanceY + 0.5f)).array();
			connection.sendUDP(data);
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

