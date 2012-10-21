package spang.mobile;

import java.util.List;

import spang.android.sensors.ISensor;
import spang.android.sensors.SensorListBuilder;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;


public class PrefsActivity extends PreferenceActivity{
	
	private static int MAXIMUM_SAMPLE_RATE = 20;

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setPreferenceScreen(getPref());
	}

	private PreferenceScreen getPref() {
		PreferenceScreen screen = getPreferenceManager().createPreferenceScreen(this);
		
		SensorManager manager = (SensorManager) getSystemService(SENSOR_SERVICE);
		List<ISensor> sensors = new SensorListBuilder(manager, this.getResources()).build(); 			//Gets list of all available sensors

		for (ISensor sensor : sensors) {
			if(sensor != null){
				String name = sensor.getName();
				PreferenceCategory sensorCategory = new PreferenceCategory(this);
				sensorCategory.setSummary(name);
				sensorCategory.setTitle(name);
				
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
			}
		}
		return screen;
	}
}
