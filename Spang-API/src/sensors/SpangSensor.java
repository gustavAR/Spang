package sensors;

import android.hardware.SensorEventListener;

/**
 * Simplifies retrieval of values from sensors.
 * @author Pontus Pall & Gustav Alm Rosenblad
 *
 */
public interface SpangSensor extends SensorEventListener {
	
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
	 * @return the sensor-values encoded and ready for sending over network.
	 */
	public byte[] encode();
	
	/**
	 * @return the constant number of values returned by the sensor.
	 */
	public int getValuesLength();
	
	/**
	 * @return the constant number of bytes needed to encode the values of the sensor. 
	 */
	public int getEncodedLength();
}
