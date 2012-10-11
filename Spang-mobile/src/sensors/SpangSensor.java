package sensors;

import java.nio.ByteBuffer;

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