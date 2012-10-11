package keyboard;

import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.inputmethodservice.Keyboard;

/**
 * This keyboard will 
 * @author Gustav Alm Rosenblad
 *
 */
public class ControlKeyboard extends Keyboard {
    
	public static final int SHIFT_KEYCODE = -1;
	public static final int CTRL_KEYCODE = -2;
	public static final int HIDE_KEYBOARD_KEYCODE = -3;
	public static final int ALTGR_KEYCODE = -4;
	
	public static final int F1_KEYCODE = -11;
	public static final int F2_KEYCODE = -12;
	public static final int F3_KEYCODE = -13;
	public static final int F4_KEYCODE = -14;
	public static final int F5_KEYCODE = -15;
	public static final int F6_KEYCODE = -16;
	public static final int F7_KEYCODE = -17;
	public static final int F8_KEYCODE = -18;
	public static final int F9_KEYCODE = -19;
	public static final int F10_KEYCODE = -20;
	public static final int F11_KEYCODE = -21;
	public static final int F12_KEYCODE = -22;
	
	Key ctrlKey;
	Key altgrKey;
	
    public ControlKeyboard(Context context, int xmlLayoutResId) {
        super(context, xmlLayoutResId);
    }

    public ControlKeyboard(Context context, int layoutTemplateResId, 
            CharSequence characters, int columns, int horizontalPadding) {
        super(context, layoutTemplateResId, characters, columns, horizontalPadding);
    }

    @Override
    protected Key createKeyFromXml(Resources res, Row parent, int x, int y, 
            XmlResourceParser parser) {
        Key key = new Key(res, parent, x, y, parser);
        if (key.codes[0] == CTRL_KEYCODE)//is ctrl
        {
        	this.ctrlKey = key;
        } else if (key.codes[0] == ALTGR_KEYCODE)//is altgr
        {
        	this.altgrKey = key;
        }
        return key;
    }
    
    /**
     * @return the ctrl modifier key of this keyboard
     */
	public Key getCtrlKey() {
		return ctrlKey;
	}

	/**
     * @return the altgr modifier key of this keyboard
     */
	public Key getAltgrKey() {
		return altgrKey;
	}

}
