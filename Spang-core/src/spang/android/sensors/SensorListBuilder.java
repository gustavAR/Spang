/*  Copyright 2012 Joakim Johansson, Lukas Kurtyan, Gustav Alm Rosenblad and Pontus Pall
 *  
 *  This file is part of Spang.

    Spang is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Spang is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Spang.  If not, see <http://www.gnu.org/licenses/>.
 */
package spang.android.sensors;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.hardware.Sensor;
import android.hardware.SensorManager;

/**
 * Builds a list of all the valid implementations of SpangSensors.
 * @author Gustav Alm Rosenblad, Joakim Johansson & Pontus Pall
 *
 */
public class SensorListBuilder {
	private final List<ISensor> sensorList = new ArrayList<ISensor>();
	private final SensorManager manager;
	@SuppressLint("UseSparseArrays")
	private final Map<Integer, ISensor> sensorBindings = new HashMap<Integer, ISensor>();

	/**
	 * For each new sensor, a binding must be created in the sensorBindings map. 
	 * @param resources 
	 * @param context
	 */
	@SuppressWarnings("deprecation")
	public SensorListBuilder(SensorManager manager, Resources resources) {
		this.manager = manager;
		
	
		if(this.manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)!=null)
			sensorBindings.put(Sensor.TYPE_ACCELEROMETER, new SpangSensor(manager, Sensor.TYPE_ACCELEROMETER, (byte) SensorMapping.ACCELEROMETER.code()));
		
		if(this.manager.getDefaultSensor(Sensor.TYPE_LIGHT)!=null)
			sensorBindings.put(Sensor.TYPE_LIGHT, new SpangSensor(manager, Sensor.TYPE_LIGHT, (byte) SensorMapping.LUMINANCE.code()));
		
		if(this.manager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)!=null)
			sensorBindings.put(Sensor.TYPE_GYROSCOPE, new SpangSensor(manager, Sensor.TYPE_GYROSCOPE, (byte) SensorMapping.GYROSCOPE.code()));
		
		if(this.manager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)!=null)
			sensorBindings.put(Sensor.TYPE_MAGNETIC_FIELD, new SpangSensor(manager, Sensor.TYPE_MAGNETIC_FIELD, (byte) SensorMapping.MAGNETICFIELD.code()));
		
		if(this.manager.getDefaultSensor(Sensor.TYPE_PROXIMITY)!=null)
			sensorBindings.put(Sensor.TYPE_PROXIMITY, new SpangSensor(manager, Sensor.TYPE_PROXIMITY, (byte) SensorMapping.PROXIMITY.code()));
		
		if(this.manager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY)!=null)
			sensorBindings.put(Sensor.TYPE_RELATIVE_HUMIDITY, new SpangSensor(manager, Sensor.TYPE_RELATIVE_HUMIDITY, (byte) SensorMapping.HUMIDITY.code()));
		
		if(this.manager.getDefaultSensor(Sensor.TYPE_PRESSURE)!=null)
			sensorBindings.put(Sensor.TYPE_PRESSURE, new SpangSensor(manager, Sensor.TYPE_PRESSURE, (byte) SensorMapping.AIRPRESSURE.code()));
		
		if(this.manager.getDefaultSensor(Sensor.TYPE_GRAVITY)!=null)
			sensorBindings.put(Sensor.TYPE_GRAVITY, new SpangSensor(manager, Sensor.TYPE_GRAVITY, (byte) SensorMapping.GRAVITY.code()));
		
		//Both Magnetic sensor and gravity sensor needed to calculate orientation
		if(this.manager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)!=null && this.manager.getDefaultSensor(Sensor.TYPE_GRAVITY) != null) 
			sensorBindings.put(Sensor.TYPE_ORIENTATION, new OrientationSensor(manager, (byte) SensorMapping.ORIENTATION.code()));

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
