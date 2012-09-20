package spang.mobile;

import network.IConnection;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

public abstract class AbstractSpangView extends View {

	protected IConnection connection;

	public AbstractSpangView(Context context, AttributeSet attrs, IConnection connection) {
		super(context, attrs);
	
		this.connection = connection;
	}
	
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_VOLUME_UP:
			Log.d("Volume key:", "UP");
			connection.sendUDP(new byte[]{(byte)7});
			return true;

		case KeyEvent.KEYCODE_VOLUME_DOWN:
			Log.d("Volume key:", "DOWN");
			connection.sendUDP(new byte[]{(byte)8});
			return true;
		}	
		return super.onKeyDown(keyCode, event);
	}
}
