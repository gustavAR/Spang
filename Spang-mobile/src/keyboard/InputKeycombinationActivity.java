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

    /**
     * {@inheritDoc}
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        KeyboardForKeycomboView kFKV= new KeyboardForKeycomboView(this, null);
		kFKV.setId(R.layout.activity_input_keycombination);
		setContentView(kFKV);
		Toast.makeText(this, "Please input desired keycombination", Toast.LENGTH_SHORT).show();
	}

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_input_keycombination, menu);
        return true;
    }
}
