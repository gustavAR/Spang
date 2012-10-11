package keyboard;

import spang.mobile.NetworkService;
import spang.mobile.R;
import utils.Packer;
import android.app.Activity;
import android.content.Context;
import android.inputmethodservice.KeyboardView;
import android.util.AttributeSet;
import android.util.Log;

/**
 * This is the view which contains the keyboard.
 * The keyboard will send events to this view,
 * which sends them to the packer which will
 * send them over the network.
 * 
 * ATTENTION: setPacker(Packer packer) MUST be
 * 			  called with a valid packer before
 * 			  any keypresses are made.
 * @author Gustav Alm Rosenblad
 *
 */
public class ComputerStyleKeyboardView extends KeyboardView implements KeyboardView.OnKeyboardActionListener {

	/**
	 * This is the currently active keyboard,
	 * the one we show.
	 */
	private ComputerStyleKeyboard ctrlKeyboard;

	private NetworkService network;
	private Packer packer;

	private boolean ctrlActive; //Is ctrl currently pressed?
	private boolean altgrActive;//Is altgr currently pressed?
	private boolean shiftActive;//Is shift currently pressed?

	/**
	 * This constructor is mostly used by the android api.
	 * @param context
	 * @param attrs
	 */
	public ComputerStyleKeyboardView(Context context, AttributeSet attrs, NetworkService networkService ) {
		super(context, attrs);
		this.ctrlKeyboard = new ComputerStyleKeyboard(context, R.xml.unicodeqwerty);
		this.setKeyboard(ctrlKeyboard);
		this.setOnKeyboardActionListener(this);
		this.packer = new Packer();
		this.network = networkService;
	}

	/**
	 * This updates the appearance of the keyboard
	 */
	public void updateKeyboardState(){
		if (ctrlActive){
			ctrlKeyboard = new ComputerStyleKeyboard(this.getContext(), R.xml.unicodeqwerty);
		} else if (altgrActive){
			ctrlKeyboard = new ComputerStyleKeyboard(this.getContext(), R.xml.unicodealtgr);
		}
		else if (this.shiftActive){
			ctrlKeyboard = new ComputerStyleKeyboard(this.getContext(), R.xml.unicodeshifted);
		}
		else {
			ctrlKeyboard = new ComputerStyleKeyboard(this.getContext(), R.xml.unicodeqwerty);
		}
		this.setKeyboard(ctrlKeyboard);
		ctrlKeyboard.ctrlKey.on = this.ctrlActive;
		ctrlKeyboard.altgrKey.on = this.altgrActive;
		this.setShifted(this.shiftActive);
	}

	public void swipeLeft() {}
	public void swipeRight() {}

	/**
	 * Called when the user swipes up.
	 * We will hide the keyboard when this happens.
	 */
	@Override
	public void swipeUp() {
		goBackToMain();
	}

	/**
	 * Called when the user swipes down.
	 * We will hide the keyboard when this happens.
	 */
	@Override
	public void swipeDown() {
		goBackToMain();
	}

	/**
	 * Called when a key is pressed.
	 */
	public void onKey(int primaryCode, int[] keyCodes) {
		if(isFunctionKeycode(primaryCode)){
			handleFunctionPress(primaryCode);
		}else{
			char character = (char)primaryCode;
			sendKeyPress("" + character);
			resetModifiers();
		}
		this.updateKeyboardState();
	}

	private void handleFunctionPress(int primaryCode) {
		switch (primaryCode){
		case ComputerStyleKeyboard.SHIFT_KEYCODE:
			this.shiftActive = !this.shiftActive;
			return;
		case ComputerStyleKeyboard.CTRL_KEYCODE:
			this.ctrlActive = !this.ctrlActive;
			return;
		case ComputerStyleKeyboard.HIDE_KEYBOARD_KEYCODE:
			goBackToMain();
			return;
		case ComputerStyleKeyboard.ALTGR_KEYCODE:
			this.altgrActive = !this.altgrActive;
			return;
		default://Was an F-key pressed?
			if(-22<primaryCode && primaryCode<-11)//TODO: Make use of an immutable list? Creating one for the Fkeys is more work.
				sendKeyPress("${" + (-primaryCode-10) + "}");//Is there any way we could avoid having the keyboard knowing the keycodelayout?
		}
	}

	private boolean isFunctionKeycode(int primaryCode) {
		return primaryCode<0;
	}

	private void sendKeyPress(String character) {
		this.packer.packByte((byte)this.getContext().getResources().getInteger(R.integer.Text));
		this.packer.packString(addModifierIDs(character));
		this.network.send(this.packer.getPackedData());
		this.packer.clear();
		Log.i("Sent: ", character);
	}

	private void resetModifiers() {
		this.shiftActive = false;
		this.altgrActive = false;
		this.ctrlActive = false;
	}

	private String addModifierIDs(String character) {
		String toSend = "";
		if(this.shiftActive)//These values will probably not even be used in the final implementation
			toSend += "${s";//TODO: Replace these ugly hardcoded values
		if(this.ctrlActive)
			toSend += "c";//TODO: Replace these ugly hardcoded values
		if(this.altgrActive)
			toSend += "a";//TODO: Replace these ugly hardcoded values
		toSend += character;
		return toSend;
	}

	private void goBackToMain() {
		((Activity)this.getContext()).finish();
	}

	/**
	 * We don't need to do anything here,
	 * since everything is handled in onKey
	 */
	public void onPress(int primaryCode) {

	}

	/**
	 * We don't need to do anything here,
	 * since everything is handled in onKey
	 */
	public void onRelease(int primaryCode) {

	}

	/**
	 * We don't need to do anything here,
	 * since everything is handled in onKey
	 */
	public void onText(CharSequence text) {

	}
}