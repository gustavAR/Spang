package sensors;

import java.nio.ByteBuffer;

import utils.Packer;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;

/**
 * Class that contains the devices air pressure sensor 
 * and simplifies usage of it.
 * @author Joakim Johansson
 *
 */
public class AirPressureSensor implements ISensor {
	public static final int SENSOR_TYPE = Sensor.TYPE_PRESSURE; 
	public static final int VALUES_LENGTH = 1;
	public static final int ENCODED_LENGTH = VALUES_LENGTH * 4 + 1; 
	
	private float[] values = new float[1];
	private int accuracy;
	private boolean isActive;
	private byte encodeID;
	
	private SensorManager sensorManager;
	private Sensor sensor;
	
	/**
	 * Doesn't start listening to the sensor. Only gets the light-sensor from the device.
	 * If the device has no light sensor, a NoSensorException is thrown.
	 * @param context
	 */
	public AirPressureSensor(SensorManager manager, byte encodeID) {
		this.sensorManager = manager;
		this.sensor = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
		this.encodeID = encodeID;
		if(this.sensor == null) {
			throw new NoSensorException("Device has no air pressure sensor");
		}
	}
	/**
	 * {@inheritDoc}
	 */	
	
	public void start() {
		sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
		this.isActive = true;
	}
	
	/**
	 * {@inheritDoc}
	 */	
	
	public void stop() {
		sensorManager.unregisterListener(this);
		this.isActive = false;
	}

	/**
	 * {@inheritDoc}
	 * @return the light-sensor value in lx. 
	 * Return value occupies only the first position of the array.
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
		return SENSOR_TYPE;
	}
	
	/**
	 * {@inheritDoc}
	 */
	
	public boolean isRunning() {
		return isActive;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void encode(Packer packer) {
		packer.packFloat(this.values[0]);
	}
	
	/**
	 * {@inheritDoc}
	 */
	
	public int getValuesLength() {
		return VALUES_LENGTH;
	}
	
	/**
	 * {@inheritDoc}
	 */
	
	public int getEncodedLength() {
		return ENCODED_LENGTH;
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
