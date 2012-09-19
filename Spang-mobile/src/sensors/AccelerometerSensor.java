package sensors;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;

/**
 * Class that contains the devices Accelerometer-sensor 
 * and simplifies usage of it.
 * @author Pontus Pall & Gustav Alm Rosenblad
 *
 */
public class AccelerometerSensor implements SpangSensor {
	public static final int SENSOR_TYPE = Sensor.TYPE_ACCELEROMETER;
	public static final byte ENCODE_ID = 0x03;
	public static final int VALUES_LENGTH = 3;
	public static final int ENCODED_LENGTH = VALUES_LENGTH * 4 + 1;

	private float[] values = new float[3];
	private int accuracy;
	private boolean isActive;

	private SensorManager sensorManager;
	private Sensor sensor;

	/**
	 * Doesn't start listening to the sensor. Only gets the accelerometer-sensor from the device.
	 * If the device has no accelerometer sensor, a NoSensorException is thrown.
	 * @param context
	 */
	public AccelerometerSensor(Context context) {
		this.sensorManager = (SensorManager) context
				.getSystemService(Context.SENSOR_SERVICE);
		this.sensor = sensorManager
				.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

		if (this.sensor == null) {
			throw new NoSensorException("Device has no Accelerometer-sensor");
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void start() {
		sensorManager.registerListener(this, sensor,
				SensorManager.SENSOR_DELAY_NORMAL);
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
	 * @return the Accelerometer-sensor value. 
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
	public void encode(ByteBuffer buffer) {
		buffer.put(ENCODE_ID)
					.putFloat(this.values[0]).putFloat(this.values[1]).putFloat(this.values[2]);
	}
}
