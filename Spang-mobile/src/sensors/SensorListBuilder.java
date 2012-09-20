package sensors;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;

/**
 * Builds a list of all the valid implementations of SpangSensors.
 * @author Gustav Alm Rosenblad & Pontus Pall
 *
 */
public class SensorListBuilder {
	private List<ISensor> sensorList = new ArrayList<ISensor>();
	private SensorManager manager;
	private Map<Integer, ISensor> sensorBindings = new HashMap<Integer, ISensor>();

	/**
	 * For each new sensor, a binding must be created in the sensorBindings map. 
	 * @param context
	 */
	public SensorListBuilder(Context context) {
		this.manager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE); 

		
		if(this.manager.getDefaultSensor(LinearAccelerationSensor.SENSOR_TYPE)!=null)
			sensorBindings.put(LinearAccelerationSensor.SENSOR_TYPE, new LinearAccelerationSensor(context));
		if(this.manager.getDefaultSensor(LightSensor.SENSOR_TYPE)!=null)
			sensorBindings.put(LightSensor.SENSOR_TYPE, new LightSensor(context));

		//		sensorBindings.put(Sensor.TYPE_MAGNETIC_FIELD, new MagneticFieldSensor(context));
		//		sensorBindings.put(Sensor.TYPE_GYROSCOPE, new GyroscopeSensor(context));

	}

	/**
	 * Builds and returns a list of all SpangSensors on the current device.
	 * @return a list of all SpangSensors on the current device. 
	 */
	public List<ISensor> build() {
		List<Sensor> sensors = manager.getSensorList(Sensor.TYPE_ALL);

		for (Sensor sensor : sensors) {
			ISensor spangSensor = sensorBindings.get(sensor.getType());
			if(spangSensor != null)
				sensorList.add(spangSensor);
		}
		return sensorList;
	}
}
