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
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;

/**
 * Activity for creating, deleting and editing shortcuts.
 * @author Gustav Alm Rosenblad
 */
public class ShortcutPrefsActivity extends PreferenceActivity {

	private static final String NEW_SHORTCUT_BUTTON_TEXT = "New shortcut";
	private static final String DEFAULT_SHORTCUT_KEYCOMBO = "";
	private static final String DEFAULT_SHORTCUT_NAME = "";

	private SharedPreferences preferences;
	
	/**
	 * The number of shortcuts in memory.
	 */
	private int numOfShortcuts;

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setPreferenceScreen(getPref());

	}

	/**
	 * This method generates a plethora of preferences,
	 * puts them in a screen, and returns it.
	 * 
	 * The preferences are generated from the shared preferences memory.
	 * @return
	 */
	private PreferenceScreen getPref() {
		@SuppressWarnings("deprecation")
		PreferenceScreen screen = getPreferenceManager().createPreferenceScreen(this);

		this.preferences = PreferenceManager.getDefaultSharedPreferences(this);
		this.preferences.registerOnSharedPreferenceChangeListener(new OnSharedPreferenceChangeListener() {

			@SuppressWarnings("deprecation")
			public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
					String key) {
				setPreferenceScreen(ShortcutPrefsActivity.this.getPref());
			}
		});

		Preference newButton = new Preference(this);
		newButton.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			
			public boolean onPreferenceClick(Preference preference) {
				ShortcutPrefsActivity.this.createNewShortcut();
				return true;
			}
		});
		newButton.setTitle(NEW_SHORTCUT_BUTTON_TEXT);
		screen.addPreference(newButton);
		
		for (int i = 0; ; i++) {//Infinite for-loop. It lets me initialize i, increment it and loop on one line.
			if(!shortCutExists(i)){
				this.numOfShortcuts = i;
				break;
			}

			String name = this.preferences.getString(
					getString(R.string.shortcut_button_name) + i, 
					getString(R.string.shortcut_button_name_not_found));
			String keyCombo = this.preferences.getString(
					getString(R.string.shortcut_button_keycombo) + i, 
					getString(R.string.shortcut_button_keycombo_not_found));
			final int shortCutIndex = i;

			final PreferenceCategory shortCutCategory = new PreferenceCategory(this);
			shortCutCategory.setSummary(name);
			shortCutCategory.setTitle(name);
			
			screen.addPreference(shortCutCategory);

			final EditTextPreference nameEditText = new EditTextPreference(this);
			nameEditText.setDefaultValue(name);
			nameEditText.setText(name);
			nameEditText.setTitle("Name: " + name);
			nameEditText.setKey(getString(R.string.shortcut_button_name) + i);
			nameEditText.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
				
				public boolean onPreferenceChange(Preference preference, Object newValue) {
					shortCutCategory.setTitle((String)newValue);
					nameEditText.setTitle("Name: " + (String)newValue);
					return true;
				}
			});

			Preference comboEditText = new Preference(this);
			comboEditText.setDefaultValue(keyCombo);
			comboEditText.setTitle("Keycombo: " + keyCombo);
			comboEditText.setKey(keyCombo);

			OnPreferenceClickListener cListener = new OnPreferenceClickListener() {
				int index = shortCutIndex;
				public boolean onPreferenceClick(Preference preference) {
					ShortcutPrefsActivity.this.inputKeycombination(index);
					return true;
				}
			};

			comboEditText.setOnPreferenceClickListener(cListener);
			
			Preference deletePref = new Preference(this);
			deletePref.setDefaultValue(keyCombo);
			deletePref.setTitle("Delete");

			OnPreferenceClickListener deleteListener = new OnPreferenceClickListener() {
				int index = shortCutIndex;
				public boolean onPreferenceClick(Preference preference) {
					ShortcutPrefsActivity.this.deleteShortcut(index);
					return true;
				}
			};

			deletePref.setOnPreferenceClickListener(deleteListener);

			shortCutCategory.addPreference(nameEditText);
			shortCutCategory.addPreference(comboEditText);
			shortCutCategory.addPreference(deletePref);
		}
		return screen;
	}

	/**
	 * Creates a new shortcut with default name and keycombo.
	 */
	protected void createNewShortcut() {
		Editor editor = this.preferences.edit();
		editor.putString(getString(R.string.shortcut_button_keycombo) + this.numOfShortcuts, DEFAULT_SHORTCUT_KEYCOMBO);
		editor.putString(getString(R.string.shortcut_button_name) + this.numOfShortcuts, DEFAULT_SHORTCUT_NAME);
		editor.commit();
		
		reset();
	}
	
	/**
	 * Deletes a shortcut.
	 * All shortcuts with a higher index will have their
	 * index decremented by one.
	 * @param shortcutIndex
	 */
	protected void deleteShortcut(int shortcutIndex) {
		Editor editor = this.preferences.edit();
		for(int i = shortcutIndex; i < this.numOfShortcuts;i++){
			String name = this.preferences.getString(
					getString(R.string.shortcut_button_name) + (i + 1), 
					getString(R.string.shortcut_button_name_not_found));
			String keyCombo = this.preferences.getString(
					getString(R.string.shortcut_button_keycombo) + (i + 1), 
					getString(R.string.shortcut_button_keycombo_not_found));
			editor.putString(getString(R.string.shortcut_button_keycombo) + i, keyCombo);
			editor.putString(getString(R.string.shortcut_button_name) + i, name);
		}
		editor.commit();
		reset();
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
	 * Sets the keycombo of the shortcut indexed by the requestcode
	 * from the intent extra with the key KEYCOMBO_EXTRAKEY
	 * (KEYCOMBO_EXTRAKEY is a public constant in KeyboardForKeycomboView).
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
				getString(R.string.shortcut_button_name_not_found)).equals(getString(R.string.shortcut_button_name_not_found));
	}
}
