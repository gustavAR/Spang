package keyboard;

import spang.mobile.NetworkedActivity;
import spang.mobile.R;
import android.os.Bundle;
import android.view.Menu;

/**
 * Activity which holds a view of a keyboard.
 * When you push buttons on the keyboard,
 * the pushes will be sent over the network.
 * @author Gustav Alm Rosenblad
 *
 */
public class KeyboardNetworkedActivity extends NetworkedActivity {

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_keyboardtest, menu);
		return true;
	}

	@Override
	protected void onNetworkServiceConnected() {
		ComputerStyleKeyboardView cKV = new ComputerStyleKeyboardView(this, null, this.getNetworkService());
		cKV.setId(R.layout.activity_keyboardtest);
		setContentView(cKV);
	}

	@Override
	protected void onNetworkSerivceDissconnected() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onMessageRecived(Object message) {
		// TODO Auto-generated method stub

	}

}
