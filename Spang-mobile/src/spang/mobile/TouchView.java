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
		
		int eventID = event.getAction()  & MotionEvent.ACTION_MASK;
		if(eventID == MotionEvent.ACTION_UP)
			pointers = 0;
		
		
		packer.packByte((byte)0); //Temporary ID for touch packets
		packer.packByte((byte)pointers);
		for(int i = 0; i < pointers; i++) {
			packer.packShort((short)event.getX(i));
			packer.packShort((short)event.getY(i));
			packer.packByte((byte)(event.getPressure(i) * 256));	
		}

		service.send(packer.getPackedData());
		packer.clear();
		return true;
	}
}
