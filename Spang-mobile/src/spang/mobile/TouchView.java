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
		
		packer.packByte((byte)100); //Temporary ID for touch packets
		packer.packByte((byte)pointers);
		for(int i = 0; i < pointers; i++) {
			packer.packHalfFloat(event.getX(i));
			packer.packHalfFloat(event.getY(i));
			packer.packHalfFloat(event.getPressure(i));	
			packer.packInt((event.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK) >> 
			MotionEvent.ACTION_POINTER_INDEX_SHIFT);
		}

		service.send(packer.getPackedData());
		return true;
	}
}
