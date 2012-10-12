package sensors;

import utils.Packer;

/**
 * Simplifies retrieval of values from sensors.
 * @author Pontus Pall & Gustav Alm Rosenblad
 *
 */
public interface ISensor {
	
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
	 * @param the Packer with all sensor-input 
	 * Puts the sensor-values in the Packer.
	 */
	public void encode(Packer packer);
	
	/**
	 * @return the name of the sensor in the device
	 */
	public String getName();
	
	/**
	 * @return the power in mA used by this sensor while in use
	 */
	public float getPowerUsage();
}
