package spang.mobile;

import java.util.List;

import sensors.ISensor;
import sensors.SensorListBuilder;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;


public class PrefsActivity extends PreferenceActivity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setPreferenceScreen(getPref());
	}

	private PreferenceScreen getPref() {
		PreferenceScreen screen = getPreferenceManager().createPreferenceScreen(this);

		SensorManager manager = (SensorManager) getSystemService(SENSOR_SERVICE);
		List<ISensor> sensors = new SensorListBuilder(manager).build(); 

		for (ISensor sensor : sensors) {
			if(sensor != null){
				String name = sensor.getName();
				PreferenceCategory sensorCategory = new PreferenceCategory(this);
				sensorCategory.setSummary(name);
				sensorCategory.setTitle(name);
				
				screen.addPreference(sensorCategory);
				
				CheckBoxPreference checkbox = new CheckBoxPreference(this);
				checkbox.setKey(""+sensor.getSensorID());
				checkbox.setTitle("Activated");
				checkbox.setSummary("Power usage: "+sensor.getPowerUsage()+"mA");
				
				SeekBarPreference sampleRate = new SeekBarPreference(this, "Sample rate","Hz",1, 60);
				sampleRate.setTitle("Sample rate");
				sampleRate.setSummary("Current value: "+sampleRate.getProgress());
				
				sensorCategory.addPreference(checkbox);
				sensorCategory.addPreference(sampleRate);
				//screen.addPreference(checkbox);
			}
		}
		return screen;
	}
}
