package spang.mobile;

import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.inputmethodservice.KeyboardView.OnKeyboardActionListener;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;

public class KeyboardService extends InputMethodService 
implements OnKeyboardActionListener {

	private int mLastDisplayWidth;
	private boolean mCapsLock;
	private long mLastShiftTime;
	private long mMetaState;

	private ControlKeyboard controlKeyboard;
	private KeyboardView mInputView;


	/**
	 * Main initialization of the input method component.  Be sure to call
	 * to super class.
	 */
	@Override public void onCreate() {
		super.onCreate();
	}

	/**
	 * This is the point where you can do all of your UI initialization.  It
	 * is called after creation and any configuration change.
	 */
	@Override public void onInitializeInterface() {
		if (controlKeyboard != null) {
			// Configuration changes can happen after the keyboard gets recreated,
			// so we need to be able to re-build the keyboards if the available
			// space has changed.
			int displayWidth = getMaxWidth();
			if (displayWidth == mLastDisplayWidth) return;
			mLastDisplayWidth = displayWidth;
		}
		controlKeyboard = new ControlKeyboard(this, R.xml.qwerty);
	}

	/**
	 * Called by the framework when your view for creating input needs to
	 * be generated.  This will be called the first time your input method
	 * is displayed, and every time it needs to be re-created such as due to
	 * a configuration change.
	 */
	@Override public View onCreateInputView() {
		mInputView = (KeyboardView) getLayoutInflater().inflate(
				R.layout.input, null);
		mInputView.setOnKeyboardActionListener(this);
		mInputView.setKeyboard(controlKeyboard);
		return mInputView;
	}

	/**
	 * Called by the framework when your view for showing candidates needs to
	 * be generated, like {@link #onCreateInputView}.
	 */
	@Override public View onCreateCandidatesView() {
		return null;
	}

	/**
	 * This is the main point where we do our initialization of the input method
	 * to begin operating on an application.  At this point we have been
	 * bound to the client, and are now receiving all of the detailed information
	 * about the target of our edits.
	 */
	@Override public void onStartInput(EditorInfo attribute, boolean restarting) {
		super.onStartInput(attribute, restarting);

		// Reset our state.  We want to do this even if restarting, because
		// the underlying state of the text editor could have changed in any way.

		if (!restarting) {
			// Clear shift states.
			mMetaState = 0;
		}


		updateShiftKeyState(attribute);
	}

	/**
	 * This is called when the user is done editing a field.  We can use
	 * this to reset our state.
	 */
	@Override public void onFinishInput() {
		super.onFinishInput();

		if (mInputView != null) {
			mInputView.closing();
		}
	}

	@Override public void onStartInputView(EditorInfo attribute, boolean restarting) {
		super.onStartInputView(attribute, restarting);
		// Apply the selected keyboard to the input view.
		mInputView.setKeyboard(controlKeyboard);
		mInputView.closing();
	}



	/**
	 * Use this to monitor key events being delivered to the application.
	 * We get first crack at them, and can either resume them or let them
	 * continue to the app.
	 */
	@Override public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			// The InputMethodService already takes care of the back
			// key for us, to dismiss the input method if it is shown.
			// However, our keyboard could be showing a pop-up window
			// that back should dismiss, so we first allow it to do that.
			if (event.getRepeatCount() == 0 && mInputView != null) {
				if (mInputView.handleBack()) {
					return true;
				}
			}
			break;

		case KeyEvent.KEYCODE_DEL:
			// Special handling of the delete key: if we currently are
			// composing text for the user, we want to modify that instead
			// of let the application to the delete itself.


		case KeyEvent.KEYCODE_ENTER:
			// Let the underlying text editor always handle these.
			return false;

		default:
			
		}

		return super.onKeyDown(keyCode, event);
	}

	/**
	 * Use this to monitor key events being delivered to the application.
	 * We get first crack at them, and can either resume them or let them
	 * continue to the app.
	 */
	@Override public boolean onKeyUp(int keyCode, KeyEvent event) {
		// If we want to do transformations on text being entered with a hard
		// keyboard, we need to process the up events to update the meta key
		// state we are tracking.
		return super.onKeyUp(keyCode, event);
	}

	/**
	 * Helper to update the shift state of our keyboard based on the initial
	 * editor state.
	 */
	private void updateShiftKeyState(EditorInfo attr) {
		if (attr != null 
				&& mInputView != null && controlKeyboard == mInputView.getKeyboard()) {
			int caps = 0;
			EditorInfo ei = getCurrentInputEditorInfo();
			if (ei != null && ei.inputType != EditorInfo.TYPE_NULL) {
				caps = getCurrentInputConnection().getCursorCapsMode(attr.inputType);
			}
			mInputView.setShifted(mCapsLock || caps != 0);
		}
	}

	/**
	 * Helper to determine if a given character code is alphabetic.
	 */
	private boolean isAlphabet(int code) {
		if (Character.isLetter(code)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Helper to send a key down / key up pair to the current editor.
	 */
	private void keyDownUp(int keyEventCode) {
		getCurrentInputConnection().sendKeyEvent(
				new KeyEvent(KeyEvent.ACTION_DOWN, keyEventCode));
		getCurrentInputConnection().sendKeyEvent(
				new KeyEvent(KeyEvent.ACTION_UP, keyEventCode));
	}

	/**
	 * Helper to send a character to the editor as raw key events.
	 */
	private void sendKey(int keyCode) {
		switch (keyCode) {
		case '\n':
			keyDownUp(KeyEvent.KEYCODE_ENTER);
			break;
		default:
			if (keyCode >= '0' && keyCode <= '9') {
				keyDownUp(keyCode - '0' + KeyEvent.KEYCODE_0);
			} else {
				getCurrentInputConnection().commitText(String.valueOf((char) keyCode), 1);
			}
			break;
		}
	}

	// Implementation of KeyboardViewListener

	public void onKey(int primaryCode, int[] keyCodes) {
		if (primaryCode == Keyboard.KEYCODE_SHIFT) {
			handleShift();
		} else if (primaryCode == Keyboard.KEYCODE_CANCEL) {
			handleClose();
			return;
		} else {
			handleCharacter(primaryCode, keyCodes);
		}
	}

	private void handleShift() {
		Keyboard currentKeyboard = mInputView.getKeyboard();
		if (controlKeyboard == currentKeyboard) {
			// Alphabet keyboard
			checkToggleCapsLock();
			mInputView.setShifted(mCapsLock || !mInputView.isShifted());
		} 
	}

	private void handleCharacter(int primaryCode, int[] keyCodes) {
		if (isInputViewShown()) {
			if (mInputView.isShifted()) {
				primaryCode = Character.toUpperCase(primaryCode);
			}
		}
		if (isAlphabet(primaryCode)) {
			updateShiftKeyState(getCurrentInputEditorInfo());
		} else {
			getCurrentInputConnection().commitText(
					String.valueOf((char) primaryCode), 1);
		}
	}

	private void handleClose() {
		requestHideSelf(0);
		mInputView.closing();
	}

	private void checkToggleCapsLock() {
		long now = System.currentTimeMillis();
		if (mLastShiftTime + 800 > now) {
			mCapsLock = !mCapsLock;
			mLastShiftTime = 0;
		} else {
			mLastShiftTime = now;
		}
	}

	public void swipeRight() {
	}

	public void swipeLeft() {
	}

	public void swipeDown() {
		handleClose();
	}

	public void swipeUp() {
	}

	public void onPress(int primaryCode) {
	}

	public void onRelease(int primaryCode) {
	}

	public void onText(CharSequence text) {
		// TODO Auto-generated method stub
		
	}
}
