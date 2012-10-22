package keyboard;

import spang.mobile.R;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.widget.Toast;

/**
 * Only contains a KeyboardForKeycomboView.
 * This activity expects to be used by calling
 * startActivityForResult.
 * 
 * @author Gustav Alm Rosenblad
 *
 */
public class InputKeycombinationActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        KeyboardForKeycomboView kFKV= new KeyboardForKeycomboView(this, null);
		kFKV.setId(R.layout.activity_input_keycombination);
		setContentView(kFKV);
		Toast.makeText(this, "Please input desired keycombination", Toast.LENGTH_SHORT).show();
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_input_keycombination, menu);
        return true;
    }
}
