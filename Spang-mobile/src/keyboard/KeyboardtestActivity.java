package keyboard;

import spang.mobile.R;
import utils.Packer;
import android.app.Activity;
import android.inputmethodservice.KeyboardView;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;

/**
 * This is just a test.
 * It was needed since we have been unable to
 * network the emulator to the computer.
 * @author Gustav Alm Rosenblad
 *
 */
public class KeyboardtestActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CtrlKeyboardView cKV = new CtrlKeyboardView(this, null);
        cKV.setId(R.layout.activity_keyboardtest);
        cKV.setPacker(new Packer(1024));//TODO: This packer is just there so we don't crash.
		setContentView(cKV);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_keyboardtest, menu);
        return true;
    }

    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {
    	return super.onTouchEvent(event);
    }

}
