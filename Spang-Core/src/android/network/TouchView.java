package android.network;

import network.Protocol;
import network.messages.Touch;
import network.messages.TouchEvent;
import android.content.Context;
import android.view.MotionEvent;
import android.view.View;

public class TouchView extends View {

	private NetworkService service;

	public TouchView(Context context, NetworkService service) {
		super(context);
		this.service = service;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int pointers = event.getPointerCount();

		int eventID = event.getAction() & MotionEvent.ACTION_MASK;
		if(eventID == MotionEvent.ACTION_UP)
			pointers = 0;
		
		Touch[] touches = new Touch[pointers];
		for(int i = 0; i < pointers; i++) {
			float x = event.getX(i);
			float y = event.getY(i);
			float pressure = event.getPressure(i);
			touches[i] = new Touch(x,y, pressure);
		}
		
		service.sendDirect(new TouchEvent(touches), Protocol.Unordered);
		return true;
	}
}