import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sensors.LightSensor;
import sensors.SpangSensor;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;

/**
 * Builds a list of all the valid implementations of SpangSensors.
 * @author Gustav Alm Rosenblad & Pontus Pall
 *
 */
public class SensorListBuilder {
	private List<SpangSensor> sensorList = new ArrayList<SpangSensor>();
	private SensorManager manager;
	private Map<Integer, SpangSensor> sensorBindings = new HashMap<Integer, SpangSensor>();
	
	/**
	 * For each new sensor, a binding must be created in the sensorBindings map. 
	 * @param context
	 */
	public SensorListBuilder(Context context) {
		this.manager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE); 
		
		sensorBindings.put(Sensor.TYPE_LIGHT, new LightSensor(context));
	}
	
	/**
	 * Builds and returns a list of all SpangSensors on the current device.
	 * @return a list of all SpangSensors on the current device. 
	 */
	public List<SpangSensor> build() {
		List<Sensor> sensors = manager.getSensorList(Sensor.TYPE_ALL);
		
		for (Sensor sensor : sensors) {
			sensorList.add(sensorBindings.get(sensor.getType()));
		}
		return sensorList;
	}
}
