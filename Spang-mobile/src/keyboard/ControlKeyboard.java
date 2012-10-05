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
        if (key.codes[0] == R.integer.ctrl)//is ctrl
        {
        	this.ctrlKey = key;
        } else if (key.codes[0] == R.integer.altgr)//is altgr
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
