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
package network.messages;

public class Touch {
	
	public final float X;
	public final float Y;
	public final float Pressure;

	public Touch(float x, float y, float pressure) {
		this.X = x;
		this.Y = y;
		this.Pressure = pressure;
	}
	
	public boolean equals(Object object) {
		if(object instanceof Touch) {
			Touch other = (Touch)object;
			return other.X == this.X && 
				   other.Y == this.Y &&
				   other.Pressure + 0.01f > this.Pressure &&
				   other.Pressure - 0.01f < this.Pressure;
		}
		return false;
	}
	
}
