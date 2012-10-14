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
package android.sensors;

import utils.Packer;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

/**
 * Class that contains a device sensor 
 * and simplifies usage of it.
 * @author Pontus Pall, Gustav Alm Rosenblad, Lukas Kurtyan & Joakim Johansson
 *
 */
public class SpangSensor implements ISensor, SensorEventListener {

	private float[] values;
	private int accuracy;
	private byte encodeID;

	private SensorManager sensorManager;
	private Sensor sensor;

	/**
	 * Doesn't start listening to the sensor. Only gets the sensor from the device.
	 * If the device has no sensor of the specified type, a NoSensorException is thrown.
	 * @param manager A SensorManager
	 * @param sensorType Int value representing the type of the sensor
	 * @param encodeID The id the sensor is encoded with for the network
	 */
	public SpangSensor(SensorManager manager, int sensorType, byte encodeID) {
		this.sensorManager = manager;
		this.sensor = sensorManager.getDefaultSensor(sensorType);
		this.encodeID = encodeID;
		
		if (this.sensor == null) {
			throw new NoSensorException("Device has no MagneticField-sensor");
		}
	}

	/**
	 * {@inheritDoc}
	 */	
	public void start() {
		sensorManager.registerListener(this, sensor,
				SensorManager.SENSOR_DELAY_NORMAL);
	}

	/**
	 * {@inheritDoc}
	 */	
	public void stop() {
		sensorManager.unregisterListener(this);
	}

	/**
	 * {@inheritDoc}
	 * @return the sensor value. 
	 * Return value occupies only 3 positions in the array.
	 */
	public float[] getValues() {
		return values;
	}

	/**
	 * {@inheritDoc}
	 */
	
	public void onAccuracyChanged(Sensor unused, int newAccuracy) {
		accuracy = newAccuracy;
	}

	/**
	 * {@inheritDoc}
	 */
	
	public void onSensorChanged(SensorEvent event) {
		values = event.values.clone();
	}

	/**
	 * {@inheritDoc}
	 */
	
	public int getAccuracy() {
		return this.accuracy;
	}

	/**
	 * {@inheritDoc}
	 */
	
	public int getSensorID() {
		return this.sensor.getType();
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void encode(Packer packer) {
		if(this.values == null)
			return;
		packer.packByte(encodeID);
		packer.packFloatArray(this.values);
	}
	
	/**
	 * {@inheritDoc}
	 */
	public String getName() {
		return this.sensor.getName();
	}
	
	/**
	 * {@inheritDoc}
	 */
	public float getPowerUsage() {
		return this.sensor.getPower();
	}
}