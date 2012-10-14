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

public enum SensorMapping {
	TOUCH (0), 
	TEXT(1),
	ACCELEROMETER(2),
	GYROSCOPE(3),
	LUMINANCE(4),
	MAGNETICFIELD(5),
	PROXIMITY(6),
	HUMIDITY(7),
	AIRPRESSURE(8),
	GRAVITY(9),
	ORIENTATION(10);
	
	private int code;
 
	private SensorMapping(int c) {
		code = c;
	}
 
	public int code() {
		return code;
	}
}
