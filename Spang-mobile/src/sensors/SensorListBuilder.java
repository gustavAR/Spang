package sensors;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.hardware.Sensor;
import android.hardware.SensorManager;

/**
 * Builds a list of all the valid implementations of SpangSensors.
 * @author Gustav Alm Rosenblad & Pontus Pall
 *
 */
public class SensorListBuilder {
	private final List<ISensor> sensorList = new ArrayList<ISensor>();
	private final SensorManager manager;
	@SuppressLint("UseSparseArrays")
	private final Map<Integer, ISensor> sensorBindings = new HashMap<Integer, ISensor>();

	/**
	 * For each new sensor, a binding must be created in the sensorBindings map. 
	 * @param context
	 */
	public SensorListBuilder(SensorManager manager) {
		this.manager = manager;
	
		if(this.manager.getDefaultSensor(LinearAccelerationSensor.SENSOR_TYPE)!=null)
			sensorBindings.put(LinearAccelerationSensor.SENSOR_TYPE, new LinearAccelerationSensor(manager, (byte) 0x04));
		
		if(this.manager.getDefaultSensor(LightSensor.SENSOR_TYPE)!=null)
			sensorBindings.put(LightSensor.SENSOR_TYPE, new LightSensor(manager, (byte) 0x05));
		
		if(this.manager.getDefaultSensor(GyroscopeSensor.SENSOR_TYPE)!=null)
			sensorBindings.put(GyroscopeSensor.SENSOR_TYPE, new GyroscopeSensor(manager, (byte) 0x06));
		
		if(this.manager.getDefaultSensor(MagneticFieldSensor.SENSOR_TYPE)!=null)
			sensorBindings.put(MagneticFieldSensor.SENSOR_TYPE, new MagneticFieldSensor(manager, (byte) 0x07));
		
		if(this.manager.getDefaultSensor(ProximitySensor.SENSOR_TYPE)!=null)
			sensorBindings.put(ProximitySensor.SENSOR_TYPE, new ProximitySensor(manager, (byte) 0xa));
		
		//		sensorBindings.put(Sensor.TYPE_MAGNETIC_FIELD, new MagneticFieldSensor(context));
		//		sensorBindings.put(Sensor.TYPE_GYROSCOPE, new GyroscopeSensor(context, (byte) 0x05));

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
