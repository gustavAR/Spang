package spang.mobile;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.inputmethodservice.Keyboard;

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
        if (key.codes[0] == -2)//is ctrl
        {
        	this.ctrlKey = key;
        } else if (key.codes[0] == -4)//is altgr
        {
        	this.altgrKey = key;
        }
        return key;
    }
    
	public Key getCtrlKey() {
		return ctrlKey;
	}

	public Key getAltgrKey() {
		return altgrKey;
	}

}
