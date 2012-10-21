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
package utils;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Helper class that makes it easy to unpack data that was packed by the 
 * utils.Packer class.
 * @author Lukas Kurtyan & Pontus Pall
 *
 */
public class UnPacker {
	private ByteBuffer packedData;	
	
	public UnPacker(byte[] unpackdata) {
		this.packedData = ByteBuffer.wrap(unpackdata).order(ByteOrder.LITTLE_ENDIAN);
	}
		
	/**
	 * Gets the number of bytes left of the packed data.
	 * @return number of bytes left of packed data.
	 */
	public int remaining() {
		return this.packedData.remaining();
	}

	/**
	 * Unpacks a byte.
	 * @return a byte
	 */
	public byte unpackByte() {
		return this.packedData.get();
	}	
	
	/**
	 * Unpacks a byte array.
	 * @param size the length of the array.
	 * @return a byte array.
	 */
	public byte[] unpackByteArray(int size) {
		byte[] array = new byte[size];
		this.packedData.get(array);
		return array;
	}
	
	/**
	 * Unpacks a short.
	 * @return a short.
	 */
	public short unpackShort() {
		return this.packedData.getShort();
	}
	
	/**
	 * Unpacks a short array. 
	 * @param size the size of the array
	 * @return a short array.
	 */
	public short[] unpackShortArray(int size) {
		short[] array = new short[size];
		for (int i = 0; i < array.length; i++) {
			array[i] = this.unpackShort();
		}
		return array;
	}
	
	/**
	 * Unpacks an Integer.
	 * @return an Integer.
	 */
	public int unpackInt() {
		return this.packedData.getInt();
	}
	
	/**
	 * Unpacks an Integer array.
	 * @param size the size of the array.
	 * @return an Integer array.
	 */
	public int[] unpackIntArray(int size) {
		int[] array = new int[size];
		for (int i = 0; i < array.length; i++) {
			array[i] = this.unpackInt();
		}
		return array;
	}
	
	/**
	 * Unpacks a long.
	 * @return a long.
	 */
	public long unpackLong() {
		return this.packedData.getLong();
	}
	
	/**
	 * Unpacks a long array.
	 * @param size the size of the array.
	 * @return a long array.
	 */
	public long[] unpackLongArray(int size) {
		long[] array = new long[size];
		for (int i = 0; i < array.length; i++) {
			array[i] = this.unpackLong();
		}
		return array;
	}
	

	/**
	 * Unpacks a float.
	 * @return a float.
	 */
	public float unpackFloat() {
		return this.packedData.getFloat();
	}
	
	/**
	 * Unpacks a float array.
	 * @param size the size of the array.
	 * @return the size of the array.
	 */
	public float[] unpackFloatArray(int size) {
		float[] array = new float[size];
		for (int i = 0; i < array.length; i++) {
			array[i] = this.unpackFloat();
		}
		return array;
	}
	
	/**
	 * Unpacks a double.
	 * @return a double.
	 */
	public double unpackDouble() {
		return this.packedData.getDouble();
	}	
	
	/**
	 * Unpacks a double array.
	 * @param size the size of the array.
	 * @return a double array.
	 */
	public double[] unpackDoubleArray(int size) {
		double[] array = new double[size];
		for (int i = 0; i < array.length; i++) {
			array[i] = this.unpackDouble();
		}
		return array;
	}
	
	public String unpackString() {
		int length = this.unpackInt();
		byte[] bytes = this.unpackByteArray(length);
		
		try {
			return new String(bytes, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new IllegalArgumentException("UTF-8 not supported on the implementation.");
		}	
	}
}