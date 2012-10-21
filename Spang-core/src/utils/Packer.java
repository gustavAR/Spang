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
 * Helper class that makes it easy to pack primitive data into a byte array.
 *
 *
 * @author Lukas Kurtyan & Pontus Pall
 */
public class Packer {
	private static final int INT_SIZE = Integer.SIZE/8;
	private static final int SHORT_SIZE = Short.SIZE/8;
	private static final int LONG_SIZE = Long.SIZE/8;
	private static final int FLOAT_SIZE = Float.SIZE/8;
	private static final int DOUBLE_SIZE = Double.SIZE/8;
	private static final int DEFAULT_CAPACITY = 4;

	private ByteBuffer internalBuffer;

	public Packer() {
		this(DEFAULT_CAPACITY);
	}

	public Packer(int capacity) {
		this.internalBuffer = ByteBuffer.allocate(capacity).order(ByteOrder.LITTLE_ENDIAN);
	}


	/**
	 * Gets all the packed data.
	 * @return packed data.
	 */
	public byte[] getPackedData() {
		byte[] array = new byte[internalBuffer.position()];

		internalBuffer.rewind();
		internalBuffer.get(array);

		return array;
	}


	/**
	 * Clears all packed data.
	 */
	public void clear() {
		internalBuffer.clear();
	}

	/**
	 * Gets the current size of the packed data.
	 * @return packed size.
	 */
	public int getPackedSize() {
		return this.internalBuffer.position();
	}

	private void increaseSize() {
		//Doubles the size of the bytebuffer.
		ByteBuffer buffer = ByteBuffer.allocate(this.internalBuffer.capacity() * 2).order(ByteOrder.LITTLE_ENDIAN);
		buffer.put(this.internalBuffer.array(), 0, this.internalBuffer.position());
		this.internalBuffer = buffer;
	}

	/**
	 * Packs a single byte.
	 * @param b the byte to pack
	 * @return this packer.
	 */
	public Packer packByte(byte b) {
		this.reziseIfNeeded(1);

		internalBuffer.put(b);
		return this;
	}

	/**
	 * Packs an array of bytes.
	 * @param bytes bytes to pack.
	 * @return this packer.
	 */
	public Packer packByteArray(byte[] bytes) {
		this.reziseIfNeeded(bytes.length);

		this.internalBuffer.put(bytes);	
		return this;
	}

	/**
	 * Packs a single short
	 * @param s short to pack.
	 * @return this packer.
	 */
	public Packer packShort(short s) {
		this.reziseIfNeeded(SHORT_SIZE);

		internalBuffer.putShort(s);
		return this;
	}

	/**
	 * Packs an array of shorts
	 * @param shorts the array to pack.
	 * @return this packer.
	 */
	public Packer packShortArray(short[] shorts) {
		this.reziseIfNeeded(shorts.length * SHORT_SIZE);

		for (int i = 0; i < shorts.length; i++) {
			this.internalBuffer.putShort(shorts[i]);
		}
		return this;
	}

	/**
	 * Packs a single int.
	 * @param i the int to pack.
	 * @return this packer.
	 */
	public Packer packInt(int i) {
		this.reziseIfNeeded(INT_SIZE);

		internalBuffer.putInt(i);
		return this;
	}

	/**
	 * Packs an array of ints
	 * @param ints the array to pack.
	 * @return this packer.
	 */
	public Packer packIntArray(int[] ints) {
		this.reziseIfNeeded(ints.length * INT_SIZE);

		for (int i = 0; i < ints.length; i++) {
			this.internalBuffer.putInt(ints[i]);
		}
		return this;
	}



	/**
	 * Packs a single long.
	 * @param i the long to pack.
	 * @return this packer.
	 */
	public Packer packLong(long l) {
		this.reziseIfNeeded(LONG_SIZE);

		internalBuffer.putLong(l);
		return this;
	}

	/**
	 * Packs an array of longs
	 * @param longs the array to pack.
	 * @return this packer.
	 */
	public Packer packLongArray(long[] longs) {
		this.reziseIfNeeded(longs.length * LONG_SIZE);

		for (int i = 0; i < longs.length; i++) {
			this.internalBuffer.putLong(longs[i]);
		}
		return this;
	}

	/**
	 * Packs a single float.
	 * @param f the float to pack.
	 * @return this packer.
	 */
	public Packer packFloat(float f) {
		this.reziseIfNeeded(FLOAT_SIZE);

		internalBuffer.putFloat(f);
		return this;
	}

	/**
	 * Packs an array of floats
	 * @param floats the array to pack.
	 * @return this packer.
	 */
	public Packer packFloatArray(float[] floats) {
		this.reziseIfNeeded(floats.length * DOUBLE_SIZE);

		for (int i = 0; i < floats.length; i++) {
			this.internalBuffer.putFloat(floats[i]);
		}
		return this;
	}

	/**
	 * Packs a single double.
	 * @param d the double to pack.
	 * @return a double.
	 */
	public Packer packDouble(double d) {
		this.reziseIfNeeded(DOUBLE_SIZE);

		internalBuffer.putDouble(d);
		return this;
	}

	/**
	 * Packs an array of shorts
	 * @param d the array to pack.
	 * @return this packer.
	 */
	public Packer packDoubleArray(double[] d) {
		this.reziseIfNeeded(d.length * DOUBLE_SIZE);

		for (int i = 0; i < d.length; i++) {
			this.internalBuffer.putDouble(d[i]);
		}
		return this;
	}

	/**
	 * Packs a string.
	 * The string is packed in the format:
	 * LENGTH: 32bit Integer.
	 * DATA: String of Size LENGTH in UTF-8 Format.
	 * @param s the string to pack.
	 * @return a string.
	 */
	public Packer packString(String s) {
		try {
			byte[] array = s.getBytes("UTF-8");
			this.reziseIfNeeded(array.length + INT_SIZE);
			internalBuffer.putInt(s.length());
			internalBuffer.put(array);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return this;
	}


	private void reziseIfNeeded(int neededRemainingSize) {
		while(this.internalBuffer.remaining() < neededRemainingSize) {
			this.increaseSize();
		}
	}
}