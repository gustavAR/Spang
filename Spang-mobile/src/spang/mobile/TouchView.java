package spang.mobile;

import utils.Packer;
import android.content.Context;
import android.view.MotionEvent;
import android.view.View;

public class TouchView extends View {
	
	private NetworkService service;
	private Packer packer;
	
	public TouchView(Context context, NetworkService service) {
		super(context);
		this.service = service;
		this.packer = new Packer(32);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int pointers = event.getPointerCount();
		
		for(int i = 0; i < pointers; i++) {
			float x = event.getX(i);
			float y = event.getY(i);
			float pressure = event.getPressure(i);	
			int eventID = event.getActionMasked();
			
			//PACKA!
		}
		
		//SKICKA!
		
		return true;
	}
}
