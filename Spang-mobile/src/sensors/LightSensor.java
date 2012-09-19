package sensors;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;

/**
 * Class that contains the devices light-sensor 
 * and simplifies usage of it.
 * @author Pontus Pall & Gustav Alm Rosenblad
 *
 */
public class LightSensor implements SpangSensor {
	public static final int SENSOR_TYPE = Sensor.TYPE_LIGHT; 
	public static final byte ENCODE_ID = 0x01;
	public static final int VALUES_LENGTH = 1;
	public static final int ENCODED_LENGTH = VALUES_LENGTH * 4 + 1; 
	
	private float[] values = new float[1];
	private int accuracy;
	private boolean isActive;
	
	private SensorManager lSensorManager;
	private Sensor lSensor;
	
	/**
	 * Doesn't start listening to the sensor. Only gets the light-sensor from the device.
	 * If the device has no light sensor, a NoSensorException is thrown.
	 * @param context
	 */
	public LightSensor(Context context) {
		this.lSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
		this.lSensor = lSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
		
		if(this.lSensor == null) {
			throw new NoSensorException("Device has no light-sensor");
		}
	}
	/**
	 * {@inheritDoc}
	 */	
	
	public void start() {
		lSensorManager.registerListener(this, lSensor, SensorManager.SENSOR_DELAY_NORMAL);
		this.isActive = true;
	}
	
	/**
	 * {@inheritDoc}
	 */	
	
	public void stop() {
		lSensorManager.unregisterListener(this);
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
	public void encode(ByteBuffer buffer) {
		buffer.put(ENCODE_ID)
					.putFloat(this.values[0]);
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
}
