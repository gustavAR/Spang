package spang.mobile;

import network.Protocol;
import utils.Packer;
import android.content.Context;
import android.view.MotionEvent;
import android.view.View;

public class TouchView extends View {
<<<<<<< HEAD

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

int eventID = event.getAction() & MotionEvent.ACTION_MASK;
if(eventID == MotionEvent.ACTION_UP)
pointers = 0;


packer.packByte((byte)this.getContext().getResources().getInteger(R.integer.Touch));
packer.packByte((byte)pointers);
for(int i = 0; i < pointers; i++) {
packer.packShort((short)event.getX(i));
packer.packShort((short)event.getY(i));
packer.packByte((byte)(event.getPressure(i) * 256));	
}

service.sendDirect(packer.getPackedData(), Protocol.Unordered);
packer.clear();
return true;
=======
	
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
		
		
		packer.packByte((byte)this.getContext().getResources().getInteger(R.integer.Touch));
		packer.packByte((byte)pointers);
		for(int i = 0; i < pointers; i++) {
			packer.packShort((short)event.getX(i));
			packer.packShort((short)event.getY(i));
			packer.packByte((byte)(event.getPressure(i) * 256));	
		}

		service.sendDirect(packer.getPackedData(), Protocol.Reliable);
		packer.clear();
		return true;
	}
>>>>>>> origin/master
}
}
