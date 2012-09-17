import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sensors.SpangSensor;
import android.content.Context;

public class SensorProcessor {
	private Map<SpangSensor, Boolean> sensors = new HashMap<SpangSensor, Boolean>();
	
	public SensorProcessor(Context context) {
		SensorListBuilder builder = new SensorListBuilder(context);
		List<SpangSensor> list = builder.build();
		
		for (SpangSensor spangSensor : list) {
			sensors.put(spangSensor, false);
		}
	}
	
	public void startProcess() {
//		Runnable runnable = new Runnable() {
//			@Override
//			public void run() {
//				for (SpangSensor sensor : sensors.keySet()) {
//					if(sensors.get(sensor)) {
//						
//					}
//				}
//			}
//		};
	}
	
	public void setActive(int sensorID, boolean value) {
		for (SpangSensor sensor : sensors.keySet()) {
			if(sensor.getSensorID() == sensorID) {
				sensors.put(sensor, value);
				if(value){
					sensor.start();
				} else {
					sensor.stop();
				}
			}
		}
	}
}
