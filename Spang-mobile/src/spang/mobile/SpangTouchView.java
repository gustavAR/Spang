package spang.mobile;

import java.util.ArrayList;
import java.util.List;

import utils.Vector2;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;

public class SpangTouchView extends TouchView {
	private Paint paint = new Paint();
	private List<Vector2> positions;
	private GestureDetector gDetector;
	
	public SpangTouchView(Context context, NetworkService service) {
		super(context, service);
		
		paint.setAntiAlias(true);
	    paint.setColor(Color.argb(255, 50, 50, 50));
	    paint.setStyle(Paint.Style.STROKE);
	    paint.setStrokeJoin(Paint.Join.ROUND);
	    paint.setStrokeWidth(8f);
	    
	    gDetector = new GestureDetector(context, ogListener);
	    
	    this.positions = new ArrayList<Vector2>();
	}

	@Override
	public void onDraw(Canvas canvas) {
		for (Vector2 p : positions) {
			canvas.drawCircle(p.getX(), p.getY(), 90, this.paint);
		}	
		
		positions.clear();
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		super.onTouchEvent(event);
		gDetector.onTouchEvent(event);
		
		if(event.getAction() == MotionEvent.ACTION_UP || 
		   event.getAction() == MotionEvent.ACTION_CANCEL) {
			setBackgroundColor(0xFFFFFFFF);
			this.invalidate();
			return true;
		}
		
		int pointers = event.getPointerCount();
		
		for(int i = 0; i < pointers; i++) {
			positions.add(i, new Vector2(event.getX(i), event.getY(i)));
		}
	
		this.invalidate();
		return true;
	}
	
	SimpleOnGestureListener ogListener = new SimpleOnGestureListener() {
		@Override
		public void onLongPress(MotionEvent e) {
			setBackgroundColor(0xFFCCCCCC);
			invalidate();
		}
	};
}