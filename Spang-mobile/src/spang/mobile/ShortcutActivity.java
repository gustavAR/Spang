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
package spang.mobile;

import java.util.ArrayList;
import java.util.List;

import spang.android.network.NetworkedActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

/**
 * This activity contains buttons containing keycombinations.
 * When you press the buttons, the keycombination will be sent 
 * over the network and hopefully executed by the connected computer.
 * 
 * The keycombinations (and names) are stored in the default 
 * preferences and may be changed by storing different strings inside
 * key_combination_buttonX (where X is the button index).
 * 
 * @author Gustav Alm Rosenblad
 */
public class ShortcutActivity extends NetworkedActivity {

	private static final int BUTTON_WIDTH = 100;
	private static final int BUTTON_HEIGHT = 100;

	SharedPreferences preferences;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void onResume() {
		super.onResume();
		setContentView(R.layout.activity_shortcut);
		this.preferences = PreferenceManager.getDefaultSharedPreferences(this);

		LinearLayout layout = (LinearLayout)findViewById(R.id.shortcut_base_linear_layout);

		Button[] buttons = this.loadButtons();
		if(buttons.length == 0){
			this.directUserToSettings(layout);
		}else{
			this.populateLayouts(layout, buttons);
		}
	}

	protected void reset() {
		Intent intent = getIntent();
		finish();
		startActivity(intent);
	}

	/**
	 * If the user has yet to create any shortcuts, 
	 * we want to teach them how to do so.
	 * Thus we fill our layout with a text instructing
	 * the user to go to settings,
	 * and a button which takes them there.
	 * @param layout the base layout
	 */
	private void directUserToSettings(LinearLayout layout) {
		TextView textView = new TextView(this);
		textView.setText("No saved shortcuts were found.\nYou can create some in settings.");

		Button buttonView = new Button(this);
		buttonView.setText("Go to settings.");
		buttonView.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Intent intent = new Intent(ShortcutActivity.this, ShortcutPrefsActivity.class);
				startActivity(intent);
			}
		});

		layout.addView(textView);
		layout.addView(buttonView);
	}

	private Button[] loadButtons() {
		List<Button> buttons = new ArrayList<Button>();
		for (int i = 0; ; i++) {//Infinite for-loop. It lets me initialize i, increment it and loop on one line.
			Button button = this.loadButton(i);
			if(button.getText().equals(getString(R.string.shortcut_button_name_not_found)))
				break;
			buttons.add(button);
		}

		return buttons.toArray(new Button[buttons.size()]);
	}

	/**
	 * Loads a button from the saved preferences.
	 * key_combination_button
	 * @param buttonIndex
	 * @return the loaded button
	 */
	private Button loadButton(final int buttonIndex){
		Button button = new Button(this);
		button.setText(this.preferences.getString(
				getString(R.string.shortcut_button_name) + buttonIndex, 
				getString(R.string.shortcut_button_name_not_found)));
		OnClickListener listener = new OnClickListener() {
			String keyCombination = ShortcutActivity.this.preferences.getString(
					getString(R.string.shortcut_button_keycombo) + buttonIndex, 
					getString(R.string.shortcut_button_keycombo_not_found));
			public void onClick(View v) {
				ShortcutActivity.this.sendKeyCombination(keyCombination);
			}
		};

		button.setOnClickListener(listener);
		return button;
	}

	/**
	 * Sends a string representing a key-combination
	 * over the network.
	 * @param keyCombination
	 */
	protected void sendKeyCombination(String keyCombination) {
		this.getNetworkService().send(keyCombination);
	}

	/**
	 * We want our shortcuts to be created programmatically,
	 * and gradually fill the screen as the user creates more
	 * and more of them. To accomplish this, we need to create
	 * layouts dynamically.
	 * 
	 * This method creates a new row of buttons (a new horizontal 
	 * linearlayout) inside the big base vertical linearlayout 
	 * every time we risk venturing outside the screen 
	 * with the next button.
	 * @param baseLayout The base layout inside which we create our rows of buttons.
	 * 			 Should be vertical.
	 * @param buttons The buttons which we will put inside the layouts.
	 */
	@SuppressWarnings("deprecation")
	private void populateLayouts(LinearLayout baseLayout, Button[] buttons) {

		Display display = getWindowManager().getDefaultDisplay();

		int width = display.getWidth();      
		LinearLayout row = new LinearLayout(this);
		row.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT));
		row.setOrientation(LinearLayout.HORIZONTAL);

		TextView txtSample = new TextView(this);

		row.addView(txtSample);
		txtSample.measure(0, 0);

		int widthSoFar = txtSample.getMeasuredWidth();
		for (Button button : buttons) {

			button.setWidth(BUTTON_WIDTH);
			button.setHeight(BUTTON_HEIGHT);

			button.measure(0, 0);
			widthSoFar += button.getMeasuredWidth();

			if (widthSoFar >= width) {
				baseLayout.addView(row);

				row = new LinearLayout(this);
				row.setLayoutParams(new LayoutParams(
						LayoutParams.FILL_PARENT,
						LayoutParams.WRAP_CONTENT));
				row.setOrientation(LinearLayout.HORIZONTAL);

				row.addView(button);
				widthSoFar = button.getMeasuredWidth();
			} else {
				row.addView(button);
			}
		}

		baseLayout.addView(row);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_shortcut, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.shortcut_settings:
			this.goToShortcutSettings(item);
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void goToShortcutSettings(MenuItem item){
		Intent intent = new Intent(this, ShortcutPrefsActivity.class);
		startActivity(intent);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onNetworkServiceConnected() {
		//Don't really care.
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onNetworkServiceDisconnected() {
		Toast.makeText(this,"Disconnected!", Toast.LENGTH_SHORT).show();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onMessageReceived(Object message) {
		//We don't really want t do anything
	}
}
