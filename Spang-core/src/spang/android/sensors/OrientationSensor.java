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

import utils.Packer;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class OrientationSensor implements ISensor, SensorEventListener {
	private SensorManager manager;
	private float[] gravitationValues;
	private float[] magneticFieldValues;
	
	private float[] orientationValues = new float[3];
	private float[] rotationMatrix = new float[9];
	
	
	private final byte encodeID;
	
	public OrientationSensor(SensorManager manager, byte encodeID) {
		this.manager = manager;
		this.encodeID = encodeID;
	}

	public void start() {
		manager.registerListener(this, manager.getDefaultSensor(Sensor.TYPE_GRAVITY), Sensor.TYPE_GRAVITY);
		manager.registerListener(this, manager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), Sensor.TYPE_MAGNETIC_FIELD);
	}

	public void stop() {
		manager.unregisterListener(this, manager.getDefaultSensor(Sensor.TYPE_GRAVITY));
		manager.unregisterListener(this, manager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD));
	}

	public float[] getValues() {
		if(SensorManager.getRotationMatrix(rotationMatrix, null, gravitationValues, magneticFieldValues))
			SensorManager.getOrientation(rotationMatrix, orientationValues);
		return orientationValues;
	}

	public int getAccuracy() {
		return -1;
	}

	public int getSensorID() {
		return Sensor.TYPE_ORIENTATION; //Just as good as any
	}

	public void encode(Packer packer) {
		packer.packByte(encodeID);
		packer.packFloatArray(getValues());
	}

	public String getName() {
		return "(virtual) Orientation sensor";
	}

	public float getPowerUsage() {
		return manager.getDefaultSensor(Sensor.TYPE_GRAVITY).getPower()+
				manager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD).getPower();
	}

	public void onAccuracyChanged(Sensor sensor, int accuracy) {}

	public void onSensorChanged(SensorEvent event) {
		if(event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
			this.magneticFieldValues = event.values;
		else
			this.gravitationValues = event.values;
		
	}

	

}
