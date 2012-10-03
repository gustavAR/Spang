package spang.mobile;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;

/**
 * TODO: document your custom view class.
 */
public class NetworkView extends View {
	private NetworkService service;	
		
	public NetworkView(Context context,
			NetworkService network) {
		super(context);
		this.service = network;		
		
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		service.send(new byte[] { 1, 2, 3, 4 ,5 ,6 ,7 ,8 ,9 });
		return true;
	}
}