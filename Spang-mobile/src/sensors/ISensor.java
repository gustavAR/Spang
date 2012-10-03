package sensors;

import java.nio.ByteBuffer;

import android.hardware.SensorEventListener;

/**
 * Simplifies retrieval of values from sensors.
 * @author Pontus Pall & Gustav Alm Rosenblad
 *
 */
public interface ISensor extends SensorEventListener {
	
	/**
	 * Method to start listening to the sensor. Should be called onResume.
	 */
	public void start();
	
	/**
	 * Method to stop listening to the sensor. Should be called onPause.
	 */
	public void stop();
	
	/**
	 * @return the current sensor-values
	 */
	public float[] getValues();
	
	/**
	 * @return the last reported accuracy of the sensor
	 */
	public int getAccuracy();
	
	/**
	 * @return the type of sensor defined by android. 
	 */
	public int getSensorID();

	/**
	 * Checks if the sensor is running.
	 * @return true if sensor is running, false if not.
	 */
	public boolean isRunning();
	
	/**
	 * @param the ByteBuffer with all sensor-input 
	 * Puts the sensor-values in the ByteBuffer.
	 */
	public void encode(ByteBuffer buffer);
	
	/**
	 * @return the constant number of values returned by the sensor.
	 */
	public int getValuesLength();
	
	/**
	 * @return the constant number of bytes needed to encode the values of the sensor. 
	 */
	public int getEncodedLength();
	
	/**
	 * @return the name of the sensor in the device
	 */
	public String getName();
	
	/**
	 * @return the power in mA used by this sensor while in use
	 */
	public float getPowerUsage();
}
