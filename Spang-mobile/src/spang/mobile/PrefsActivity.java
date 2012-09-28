package spang.mobile;

import java.util.List;

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
		List<Sensor> sensors = manager.getSensorList(Sensor.TYPE_ALL);


		for (Sensor sensor : sensors) {
			if(sensor != null){
				String name = sensor.getName();
				PreferenceCategory sensorCategory = new PreferenceCategory(this);
				sensorCategory.setSummary(name);
				sensorCategory.setTitle(name);
				
				screen.addPreference(sensorCategory);
				
				CheckBoxPreference checkbox = new CheckBoxPreference(this);
				checkbox.setKey("checkBox"+name);
				checkbox.setTitle("Activated");
				checkbox.setSummary("Power usage: "+sensor.getPower()+"mA");

				sensorCategory.addPreference(checkbox);
				//screen.addPreference(checkbox);
			}
		}
		return screen;
	}
}
