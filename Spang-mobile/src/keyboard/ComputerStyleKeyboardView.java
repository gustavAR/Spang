package keyboard;

import spang.android.network.NetworkService;
import spang.mobile.R;
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

	//These are only here to make the code somewhat readable.
	private final String keyboardMessageBegin = this.getContext().getString(R.string.keyboardinputmessage_begin);
	private final String keyboardMessageEnd = this.getContext().getString(R.string.keyboardinputmessage_end);
	private final String keyboardMessageShift = this.getContext().getString(R.string.keyboardinputmessage_shift);
	private final String keyboardMessageCtrl = this.getContext().getString(R.string.keyboardinputmessage_ctrl);
	private final String keyboardMessageAltgr = this.getContext().getString(R.string.keyboardinputmessage_altgr);
	
	/**
	 * This is the currently active keyboard,
	 * the one we show.
	 */
	private ComputerStyleKeyboard ctrlKeyboard;

	private NetworkService network;
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
		ctrlKeyboard.getCtrlKey().on = this.ctrlActive;
		ctrlKeyboard.getAltgrKey().on = this.altgrActive;
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
		if(isModifierKeycode(primaryCode)){
			handleModifierPress(primaryCode);
		}else if (isFunctionKeycode(primaryCode)){
			handleFunctionPress(primaryCode);
		}else{
			char character = (char)primaryCode;
			sendKeyPress("" + character);
			resetModifiers();
		}
		this.updateKeyboardState();
	}

	private boolean isFunctionKeycode(int primaryCode) {
		return primaryCode<0;
	}

	/**
	 * Handles the different function keypresses.
	 * Functions have keycodes below zero.
	 * 
	 * Methods like this one are mostly for readability.
	 * @param primaryCode
	 */
	private void handleFunctionPress(int primaryCode) {
		if(ComputerStyleKeyboard.F12_KEYCODE<=primaryCode && primaryCode<=ComputerStyleKeyboard.F1_KEYCODE){//TODO: Make use of an immutable list? Creating one for the Fkeys is more work.
			sendKeyPress(this.keyboardMessageBegin + "F" + (-primaryCode-10) + 
					this.keyboardMessageEnd);//Is there any way we could avoid having the keyboard knowing the keycodelayout?
		} else if (primaryCode == ComputerStyleKeyboard.HIDE_KEYBOARD_KEYCODE){
			goBackToMain();
		} else {
			throw new IllegalArgumentException("Class KeyboardForKeycomboView does not yet support function keycode " + primaryCode);
		}
	}

	/**
	 * Handles the different modifier keypresses.
	 * Basically, 
	 * 
	 * Methods like this one are mostly for readability.
	 * @param primaryCode
	 */
	private void handleModifierPress(int primaryCode) {
		switch (primaryCode){
		case ComputerStyleKeyboard.SHIFT_KEYCODE:
			this.shiftActive = !this.shiftActive;
			return;
		case ComputerStyleKeyboard.CTRL_KEYCODE:
			this.ctrlActive = !this.ctrlActive;
			return;
		case ComputerStyleKeyboard.ALTGR_KEYCODE:
			this.altgrActive = !this.altgrActive;
			return;
		}
	}

	private boolean isModifierKeycode(int primaryCode) {
		return 	primaryCode == ComputerStyleKeyboard.SHIFT_KEYCODE ||
				primaryCode == ComputerStyleKeyboard.CTRL_KEYCODE ||
				primaryCode == ComputerStyleKeyboard.ALTGR_KEYCODE;
	}
	
	private void sendKeyPress(String character) {
		this.network.send(addModifierIDs(character));
		Log.i("Sent: ", character);
	}

	/**
	 * Resets all modifiers.
	 */
	private void resetModifiers() {
		this.shiftActive = false;
		this.altgrActive = false;
		this.ctrlActive = false;
	}

	/**
	 * This returns the the proper modifiers attached to the input string.
	 * The modifiers describe what kind of modifiers were during input.
	 * @param textInput The keypress.
	 * @return
	 */
	private String addModifierIDs(String textInput) {
		String toSend = "";
		if(!(this.altgrActive || this.ctrlActive || this.shiftActive))
			return textInput;
		toSend += this.keyboardMessageBegin;
		if(this.shiftActive)
			toSend += this.keyboardMessageShift;
		if(this.ctrlActive)
			toSend += this.keyboardMessageCtrl;
		if(this.altgrActive)
			toSend += this.keyboardMessageAltgr;
		toSend += this.keyboardMessageEnd;
		
		toSend += textInput;
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