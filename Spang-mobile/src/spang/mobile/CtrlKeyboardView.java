package spang.mobile;

import network.IClient;
import utils.Packer;
import android.content.Context;
import android.inputmethodservice.KeyboardView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.View;

public class CtrlKeyboardView extends KeyboardView implements KeyboardView.OnKeyboardActionListener {

	KeyCharacterMap map;
	ControlKeyboard ctrlKeyboard;
	Packer packer;
	IClient client = null;
	private boolean ctrlActive;
	private boolean altgrActive;
	private boolean shiftActive;

	public CtrlKeyboardView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.ctrlKeyboard = new ControlKeyboard(context, R.xml.unicodeqwerty);
		this.setKeyboard(ctrlKeyboard);
		this.setOnKeyboardActionListener(this);
	}

	public void updateKeyboardState(){
		if (ctrlActive){
			ctrlKeyboard = new ControlKeyboard(this.getContext(), R.xml.unicodeqwerty);
		} else if (altgrActive){
			ctrlKeyboard = new ControlKeyboard(this.getContext(), R.xml.unicodealtgr);
		}
		else if (this.shiftActive){
			ctrlKeyboard = new ControlKeyboard(this.getContext(), R.xml.unicodeshifted);
		}
		else {
			ctrlKeyboard = new ControlKeyboard(this.getContext(), R.xml.unicodeqwerty);
		}
		this.setKeyboard(ctrlKeyboard);
		ctrlKeyboard.ctrlKey.on = this.ctrlActive;
		ctrlKeyboard.altgrKey.on = this.altgrActive;
		this.setShifted(this.shiftActive);
	}

	public void setClient(IClient client){
		this.client = client;
	}

	public void swipeLeft() {}
	public void swipeRight() {}

	@Override
	public void swipeUp() {
		goBackToMain();
	}

	@Override
	public void swipeDown() {
		goBackToMain();
	}


	public void onKey(int primaryCode, int[] keyCodes) {
		/*this.client.sendTCP(
				ByteBuffer.allocate(5).put((byte)15).putInt(primaryCode).array());*/
		char character = (char)primaryCode;
		switch (primaryCode){
		case -1:
			this.shiftActive = !this.shiftActive;
			break;
		case -2:
			this.ctrlActive = !this.ctrlActive;
			break;
		case -3:
			goBackToMain();
			break;
		case -4:
			this.altgrActive = !this.altgrActive;
			break;
		default:
			this.shiftActive = false;
			this.altgrActive = false;
			this.ctrlActive = false;
			ctrlKeyboard.getModifierKeys().get(0).on = true;
			Log.i("CHAR", "" + (char)primaryCode);
			}
		this.updateKeyboardState();
	}

	private void goBackToMain() {
		//TODO
	}

	public void onPress(int primaryCode) {
	}

	public void onRelease(int primaryCode) {
	}

	public void onText(CharSequence text) {
	}

	public boolean onKey(View arg0, int arg1, KeyEvent arg2) {
		return false;
	}
}
