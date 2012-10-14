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
package android.sensors;

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
