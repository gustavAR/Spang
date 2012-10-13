package spang.mobile;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.graphics.Point;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Display;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
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

	SharedPreferences preferences;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shortcut);
        LinearLayout layout = (LinearLayout)findViewById(R.id.shortcut_base_linear_layout);
        
		this.preferences = PreferenceManager.getDefaultSharedPreferences(this);
		preferences.registerOnSharedPreferenceChangeListener(new OnSharedPreferenceChangeListener() {
			
			public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
					String key) {
				onPreferenceChanged(sharedPreferences, key);
				
			}
		});
        
    }

    protected void onPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		// TODO Auto-generated method stub
		
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
	 * @return
	 */
    private Button loadButton(final int buttonIndex){
    	Button button = new Button(this);
    	button.setText(this.preferences.getString(
    										getString(R.string.shortcut_button_name) + buttonIndex, 
    										"Button name not found"));
    	
    	OnClickListener listener = new OnClickListener() {
			String keyCombination = ShortcutActivity.this.preferences.getString(
											getString(R.string.shortcut_button_keycombo) + buttonIndex, 
											"Keycombination not found");
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
