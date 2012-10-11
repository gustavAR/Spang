package keyboard;

import spang.mobile.NetworkedActivity;
import spang.mobile.R;
import android.os.Bundle;
import android.view.Menu;

/**
* This is just a test.
* It was needed since we have been unable to
* network the emulator to the computer.
* @author Gustav Alm Rosenblad
*
*/
public class KeyboardtestActivity extends NetworkedActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

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
protected void onMessageRecived(byte[] message) {
// TODO Auto-generated method stub

}

}
