package spang.mobile;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.graphics.Point;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Display;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.textservice.TextInfo;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

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
public class ShortcutActivity extends Activity {

	private static final String KEYCOMBINATION_NOT_FOUND = "Keycombination not found";
	private static final String BUTTON_NAME_NOT_FOUND = "Button name not found";

	SharedPreferences preferences;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
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

	/**
	 * If the user has yet to create any shortcuts, 
	 * we want to teach them how to do so.
	 * Thus we fill our layout with a text instructing
	 * the user to go to settings,
	 * and a button which takes them there.
	 * @param layout
	 */
	private void directUserToSettings(LinearLayout layout) {
		TextView textView = new TextView(this);
		textView.setText("No saved shortcuts were found.\nYou can create some in settings.");
		
		Button buttonView = new Button(this);
		buttonView.setText("Go to settings.");
		buttonView.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				Intent intent = new Intent(ShortcutActivity.this, ShortcutPreferenceActivity.class);
				
			}
		});
		
		layout.addView(textView);
		layout.addView(buttonView);
	}

	private Button[] loadButtons() {
		List<Button> buttons = new ArrayList<Button>();
		int i = 0;
		while(true){
			Button button = this.loadButton(i);
			if(button.getText().equals(BUTTON_NAME_NOT_FOUND))
				break;
			buttons.add(button);
			i++;
		}

		return (Button[]) buttons.toArray();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_shortcut, menu);
		return true;
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
				BUTTON_NAME_NOT_FOUND));

		OnClickListener listener = new OnClickListener() {
			String keyCombination = ShortcutActivity.this.preferences.getString(
					getString(R.string.shortcut_button_keycombo) + buttonIndex, 
					KEYCOMBINATION_NOT_FOUND);
			public void onClick(View v) {
				ShortcutActivity.this.sendKeyCombination(keyCombination);
			}
		};

		button.setOnClickListener(listener);
		return button;
	}

	protected void sendKeyCombination(String keyCombination) {

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
	 * @param ll The base layout inside which we create our rows of buttons.
	 * 			 Should be vertical.
	 * @param buttons The buttons which we will put inside the layouts.
	 */
	private void populateLayouts(LinearLayout ll, Button[] buttons) {

		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();

		int width = display.getWidth();       
		int maxHeight = display.getHeight();
		if (buttons.length > 0) {
			LinearLayout llAlso = new LinearLayout(this);
			llAlso.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
					LayoutParams.WRAP_CONTENT));
			llAlso.setOrientation(LinearLayout.HORIZONTAL);

			TextView txtSample = new TextView(this);

			llAlso.addView(txtSample);
			txtSample.measure(0, 0);

			int widthSoFar = txtSample.getMeasuredWidth();
			for (Button button : buttons) {
				TextView txtSamItem = new TextView(this, null,
						android.R.attr.textColorLink);
				txtSamItem.setPadding(10, 0, 0, 0);
				txtSamItem.setTag(button);

				txtSamItem.measure(0, 0);
				widthSoFar += txtSamItem.getMeasuredWidth();

				if (widthSoFar >= width) {
					ll.addView(llAlso);

					llAlso = new LinearLayout(this);
					llAlso.setLayoutParams(new LayoutParams(
							LayoutParams.FILL_PARENT,
							LayoutParams.WRAP_CONTENT));
					llAlso.setOrientation(LinearLayout.HORIZONTAL);

					llAlso.addView(txtSamItem);
					widthSoFar = txtSamItem.getMeasuredWidth();
				} else {
					llAlso.addView(txtSamItem);
				}
			}

			ll.addView(llAlso);
		}
	}
}
