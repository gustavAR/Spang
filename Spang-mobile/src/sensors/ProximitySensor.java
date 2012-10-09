package sensors;

import utils.Packer;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;

/**
 * Class that contains the devices proximity sensor 
 * and simplifies usage of it.
 * @author Joakim Johansson
 *
 */
public class ProximitySensor implements ISensor {
	public static final int SENSOR_TYPE = Sensor.TYPE_PROXIMITY; 
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
	 * If the device has no proximity sensor, a NoSensorException is thrown.
	 * @param context
	 */
	public ProximitySensor(SensorManager manager, byte encodeID) {
		this.sensorManager = manager;
		this.sensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
		this.encodeID = encodeID;
		if(this.sensor == null) {
			throw new NoSensorException("Device has no proximity sensor");
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
	 * @return the ProximitySensor value in cm. 
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
