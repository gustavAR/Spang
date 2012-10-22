/*  Copyright 2012 Joakim Johansson, Lukas Kurtyan, Gustav Alm Rosenblad and Pontus Pall
 *  
 *  This file is part of Spang.

    Spang is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Spang is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Spang.  If not, see <http://www.gnu.org/licenses/>.
 */
package keyboard;

import spang.mobile.R;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.inputmethodservice.KeyboardView;
import android.util.AttributeSet;
import android.util.Log;

/**
 * This view contains a keyboard.
 * As soon as a non-modifier key is pressed,
 * the keyboard will finish the underlying activity
 * with the keycombination (button + modifiers) pressed
 * accessible through the result intent extra KeyCombination
 * 
 * @author Gustav Alm Rosenblad
 *
 */
public class KeyboardForKeycomboView extends KeyboardView implements KeyboardView.OnKeyboardActionListener {

	/**
	 * This is the key with which you can extract the keycombo,
	 * as a string, from the result intent.
	 */
	public static final String KEYCOMBO_EXTRAKEY = "KeyCombination";
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


	private boolean ctrlActive; //Is ctrl currently pressed?
	private boolean altgrActive;//Is altgr currently pressed?
	private boolean shiftActive;//Is shift currently pressed?

	/**
	 * This constructor is mostly used by the android api.
	 * @param context
	 * @param attrs
	 */
	public KeyboardForKeycomboView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.ctrlKeyboard = new ComputerStyleKeyboard(context, R.xml.unicodeqwerty);
		this.setKeyboard(ctrlKeyboard);
		this.setOnKeyboardActionListener(this);
	}

	public void swipeLeft() {}
	public void swipeRight() {}

	/**
	 * Called when the user swipes up.
	 * We will hide the keyboard when this happens.
	 */
	@Override
	public void swipeUp() {
		finishActivity();
	}

	/**
	 * Called when the user swipes down.
	 * We will hide the keyboard when this happens.
	 */
	@Override
	public void swipeDown() {
		finishActivity();
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
			returnResult("" + character);
			resetModifiers();
		}
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
			returnResult(this.keyboardMessageBegin + "F" + (-primaryCode-10) + 
					this.keyboardMessageEnd);//Is there any way we could avoid having the keyboard knowing the keycodelayout?
		} else if (primaryCode == ComputerStyleKeyboard.HIDE_KEYBOARD_KEYCODE){
			finishActivity();
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

	/**
	 * Ends the activity containing this view,
	 * and adds the character with eventual modifiers
	 * as an extra to the result intent
	 * @param character
	 */
	private void returnResult(String character) {
		Intent data = new Intent();
		data.putExtra(KEYCOMBO_EXTRAKEY, addModifierIDs(character));
		((Activity)this.getContext()).setResult(Activity.RESULT_OK, data);
		finishActivity();
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

	/**
	 * Finishes the underlying activity.
	 */
	private void finishActivity() {
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