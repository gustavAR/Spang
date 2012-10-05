package keyboard;

import spang.mobile.R;
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
