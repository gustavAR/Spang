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
package spang.android.sensors;

public enum SensorMapping {
	ACCELEROMETER(0),
	GYROSCOPE(1),
	LUMINANCE(2),
	MAGNETICFIELD(3),
	PROXIMITY(4),
	HUMIDITY(5),
	AIRPRESSURE(6),
	GRAVITY(7),
	ORIENTATION(8);
	
	private int code;
 
	private SensorMapping(int c) {
		code = c;
	}
 
	public int code() {
		return code;
	}
}