package spang.mobile;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import network.IConnection;
import sensors.SensorProcessor;
import utils.Vector2;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;

@SuppressLint({ "NewApi", "UseSparseArrays" })
public class MouseView extends AbstractSpangView{
	private final Paint paint = new Paint();
	private final boolean multiTouchEnabled = Integer.parseInt(Build.VERSION.SDK) >= Build.VERSION_CODES.CUPCAKE;

	private float radius;

	private List<Vector2> pointers = new ArrayList<Vector2>();
	private List<Vector2> prevPointers = new ArrayList<Vector2>();

	private boolean scrolling, normalInputAllowed = true;

	private GestureDetector gestureDetector;

	private SensorProcessor sp;

	private static final int INVALID_POINTER_ID = -1;
	private int fingerOneID = INVALID_POINTER_ID;

	public MouseView(Context context, AttributeSet attrs, IConnection connection) {
		super(context, attrs, connection);

		paint.setAntiAlias(true);
		paint.setStrokeWidth(6f);
		paint.setColor(Color.BLACK);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeJoin(Paint.Join.ROUND);

		this.gestureDetector = new GestureDetector(context, simpleOnGestureListener);

//		this.sp = new SensorProcessor(context, connection);
//	//	this.sp.setActive(Sensor.TYPE_LINEAR_ACCELERATION, true);
//		this.sp.startProcess();
//		
//		pointers.add(0, new Vector2(0, 0));
//		prevPointers.add(0, pointers.get(0));
	}

	@Override
	protected void onDraw(Canvas canvas) {
		for (Vector2 position : pointers) {
			Random rnd = new Random();
			paint.setARGB(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
			canvas.drawCircle(position.getX(), position.getY(), radius, paint);
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if(normalInputAllowed)
			gestureDetector.onTouchEvent(event);

		int eventID = event.getAction();

		fingerOneID = event.getPointerId(0);
		final int activeIndex = event.findPointerIndex(fingerOneID);
		pointers.clear();
		prevPointers.clear();
		pointers.add(activeIndex, new Vector2(event.getX(activeIndex), event.getY(activeIndex)));
		prevPointers.add(activeIndex, pointers.get(activeIndex));
		
		Vector2 position = pointers.get(activeIndex);

		switch(eventID & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:
			prevPointers.add(activeIndex, new Vector2(position.getX(), position.getY()));
			break;
		case MotionEvent.ACTION_MOVE:
			for(int i = 0; i < event.getPointerCount(); i++) {
				pointers.add(i, new Vector2(event.getX(i), event.getY(i)));
			}

			radius = event.getPressure()*100;
			byte[] pressureData = ByteBuffer.allocate(5).order(ByteOrder.LITTLE_ENDIAN)
					.put((byte)13).putInt((int)radius).array();
			connection.sendUDP(pressureData);

			if(scrolling){
				//Vertical Scroll
				Log.d("MOTIONEVENT:", "onScroll");
				byte[] vertData = ByteBuffer.allocate(5).order(ByteOrder.LITTLE_ENDIAN)
						.put((byte)11).putInt((int)(4*(pointers.get(activeIndex).getY() - 
												        prevPointers.get(activeIndex).getY() + 0.5f))).array();
				connection.sendUDP(vertData);

				//			Horizontal Scroll
				//			byte[] horiData = ByteBuffer.allocate(5).order(ByteOrder.LITTLE_ENDIAN)
				//					.put((byte)12).putInt((int)(4*(xPos - xPosPrev + 0.5f))).array();
				//			connection.sendUDP(horiData);
			}

			prevPointers.add(activeIndex, pointers.get(activeIndex));
			break;
		case MotionEvent.ACTION_UP:
			fingerOneID = INVALID_POINTER_ID;
			break;
		case MotionEvent.ACTION_CANCEL:
			fingerOneID = INVALID_POINTER_ID;
			break;
		case MotionEvent.ACTION_POINTER_DOWN:
			Log.d("MOTIONEVENT:", "ACTION_POINTER_DOWN");
			scrolling=true;
			normalInputAllowed=false;
			break;
		case MotionEvent.ACTION_POINTER_UP:
			Log.d("MOTIONEVENT:", "ACTION_POINTER_UP");
			final int pointerIndex = (eventID & MotionEvent.ACTION_POINTER_INDEX_MASK) >> 
			MotionEvent.ACTION_POINTER_INDEX_SHIFT;
			final int pointerID = event.getPointerId(pointerIndex);
			if(pointerID == fingerOneID) {
				final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
				prevPointers.add(newPointerIndex, new Vector2(event.getX(newPointerIndex), event.getY(newPointerIndex)));
				
				fingerOneID = event.getPointerId(newPointerIndex);
			}

			scrolling = false;
			inputTimer(100);
			break;
		}	

		// Schedules a repaint.
		invalidate();
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

