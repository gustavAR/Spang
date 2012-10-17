package spang.mobile;

import keyboard.InputKeycombinationActivity;
import keyboard.KeyboardForKeycomboView;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;

public class ShortcutPrefsActivity extends PreferenceActivity {

	private static final String SHORTCUT_NAME_NOT_FOUND = "Shortcut name was not found";
	private static final String SHORTCUT_KEYCOMBO_NOT_FOUND = "Shortcut keycombo was not found";

	private SharedPreferences preferences;
	private int numOfShortcuts;

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setPreferenceScreen(getPref());

	}

	//TODO: How should we do this? It's not really that clear.
	private PreferenceScreen getPref() {
		PreferenceScreen screen = getPreferenceManager().createPreferenceScreen(this);

		this.preferences = PreferenceManager.getDefaultSharedPreferences(this);
		this.preferences.registerOnSharedPreferenceChangeListener(new OnSharedPreferenceChangeListener() {

			public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
					String key) {
				setPreferenceScreen(ShortcutPrefsActivity.this.getPref());
			}
		});

		String trooll = "TROLOLOL";
		Editor editor = this.preferences.edit();
		editor.putString(getString(R.string.shortcut_button_keycombo) + 0, trooll);
		String nTOTOTOTOTOame = "nameHerre LOL";
		editor.putString(getString(R.string.shortcut_button_name) + 0, nTOTOTOTOTOame);
		editor.commit();
		
		
		for (int i = 0; ; i++) {//Infinite for-loop. It lets me initialize i, increment it and loop on one line.
			if(!shortCutExists(i)){
				this.numOfShortcuts = i;
				break;
			}

			String name = this.preferences.getString(
					getString(R.string.shortcut_button_name) + i, 
					SHORTCUT_NAME_NOT_FOUND);
			String keyCombo = this.preferences.getString(
					getString(R.string.shortcut_button_keycombo) + i, 
					SHORTCUT_KEYCOMBO_NOT_FOUND);
			final int shortCutIndex = i;

			PreferenceCategory shortCutCategory = new PreferenceCategory(this);
			shortCutCategory.setSummary(name);
			shortCutCategory.setTitle(name);

			EditTextPreference nameEditText = new EditTextPreference(this);
			nameEditText.setDefaultValue(name);
			nameEditText.setText(name);
			nameEditText.setTitle("Name");
			nameEditText.setKey(getString(R.string.shortcut_button_name) + i);

			EditTextPreference comboEditText = new EditTextPreference(this);
			comboEditText.setDefaultValue(keyCombo);
			comboEditText.setText(keyCombo);
			comboEditText.setTitle("Keycombo");
			comboEditText.setKey(getString(R.string.shortcut_button_keycombo) + i);

			OnPreferenceClickListener cListener = new OnPreferenceClickListener() {
				int index = shortCutIndex;
				public boolean onPreferenceClick(Preference preference) {
					ShortcutPrefsActivity.this.inputKeycombination(index);
					return true;
				}
			};

			comboEditText.setOnPreferenceClickListener(cListener);
		}
		return screen;
	}

	/**
	 * This will open up a separate actvity which 
	 * displays a keyboard and prompts the user 
	 * to input the desired keycombintion.
	 * 
	 * We put the index in the requestcode,
	 * since that is the easiest way to have the
	 * shortcut index persist.
	 * 
	 * @param index Index of the shortcut whose keycombo we want to edit
	 */
	protected void inputKeycombination(int index) {
		Intent intent = new Intent(this, InputKeycombinationActivity.class);
		startActivityForResult(intent, index);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode == Activity.RESULT_OK){
			String keyCombo = data.getStringExtra(KeyboardForKeycomboView.KEYCOMBO_EXTRAKEY);
			Editor editor = this.preferences.edit();
			editor.putString(getString(R.string.shortcut_button_keycombo) + requestCode, keyCombo);
			editor.commit();

			reset();
		}
	}

	/**
	 * Resets this activity without animating the reset
	 */
	private void reset() {
		Intent intent = getIntent();
		overridePendingTransition(0, 0);
		intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
		finish();

		overridePendingTransition(0, 0);
		startActivity(intent);
	}

	private boolean shortCutExists(int i) {
		return !this.preferences.getString(
				getString(R.string.shortcut_button_name) + i, 
				SHORTCUT_NAME_NOT_FOUND).equals(SHORTCUT_NAME_NOT_FOUND);
	}
}
