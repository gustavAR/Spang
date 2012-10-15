package spang.mobile;

import java.util.List;

import sensors.ISensor;
import sensors.SensorListBuilder;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.view.Menu;

public class ShortcutPreferenceActivity extends PreferenceActivity {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setPreferenceScreen(getPref());
		PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(new OnSharedPreferenceChangeListener() {
			
			public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
					String key) {
				setPreferenceScreen(ShortcutPreferenceActivity.this.getPref());
			}
		});
	}

	private PreferenceScreen getPref() {
		PreferenceScreen screen = getPreferenceManager().createPreferenceScreen(this);

		PreferenceCategory shortcut = new PreferenceCategory(this);
		shortcut.setSummary(name);
		shortcut.setTitle(name);

		screen.addPreference(sensorCategory);

		CheckBoxPreference checkbox = new CheckBoxPreference(this);
		checkbox.setKey("isActivated"+name);								//Key used to get stored checkbox value using shared preferences
		checkbox.setTitle("Activated");
		checkbox.setSummary("Power usage: "+sensor.getPowerUsage()+"mA");

		SeekBarPreference sampleRate = new SeekBarPreference(this, "Sample rate","Hz",1, MAXIMUM_SAMPLE_RATE);
		sampleRate.setTitle("Sample rate");
		sampleRate.setSummary("Current value: "+sampleRate.getProgress());
		sampleRate.setKey("sampleRate"+name);

		sensorCategory.addPreference(checkbox);
		sensorCategory.addPreference(sampleRate);
		return screen;
	}
}
