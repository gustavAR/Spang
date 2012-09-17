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
	 * Method to get the current sensor-values.
	 * @return the current sensor-values
	 */
	public float[] getValues();
	
	/**
	 * Gets the last reported accuracy of the sensor.
	 * @return the last reported accuracy of the sensor
	 */
	public int getAccuracy();
	
	/**
	 * Gets the type of sensor defined by android. 
	 * @return the type of sensor defined by android. 
	 */
	public int getSensorID();

	/**
	 * Checks if the sensor is running.
	 * @return true if sensor is running, false if not.
	 */
	public boolean isRunning();
	
	/**
	 * Returns the sensor-values encoded and ready for sending over network.
	 * @return the sensor-values encoded and ready for sending over network.
	 */
	public byte[] encode();
}
