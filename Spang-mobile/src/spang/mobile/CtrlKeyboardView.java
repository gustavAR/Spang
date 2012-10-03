package spang.mobile;

import java.nio.ByteBuffer;

import network.IClient;
import android.content.Context;
import android.inputmethodservice.KeyboardView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

public class CtrlKeyboardView extends KeyboardView implements KeyboardView.OnKeyboardActionListener {

	ControlKeyboard ctrlKeyboard;
	IClient client = null;
	
	public CtrlKeyboardView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.ctrlKeyboard = new ControlKeyboard(context, R.xml.qwerty);
		this.setKeyboard(ctrlKeyboard);
		this.setOnKeyboardActionListener(this);
	}
	
	public void setClient(IClient client){
		this.client = client;
	}
	
	public void swipeLeft() {}
	public void swipeRight() {}
	
	@Override
	public void swipeUp() {
		// TODO Auto-generated method stub
		super.swipeUp();
	}
	
	@Override
	public void swipeDown() {
		// TODO Auto-generated method stub
		super.swipeUp();
	}

	public void onKey(int primaryCode, int[] keyCodes) {
		/*this.client.sendTCP(
				ByteBuffer.allocate(5).put((byte)15).putInt(primaryCode).array());*/
		Log.i("ONKEY", "" + primaryCode + " was pressed.");
	}

	public void onPress(int primaryCode) {
	}

	public void onRelease(int primaryCode) {
	}

	public void onText(CharSequence text) {
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent me) {
		return super.onTouchEvent(me);
	}
}
