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

import spang.android.network.NetworkedActivity;
import spang.mobile.R;
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
