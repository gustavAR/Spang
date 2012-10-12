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
	 * Unpacks a half float.
	 * @return a float.
	 */
	public float unpackHalfFloat() {
		int hbits = (this.packedData.get() << 8);
		hbits |= this.packedData.get();
		
		return toFloat(hbits);
	}
	
	/**
	 * Unpacks a half float array
	 * @param size the size of the array.
	 * @return a float array
	 */
	public float[] unpackHalfFloatArray(int size) {
		float[] array = new float[size];
		for (int i = 0; i < array.length; i++) {
			array[i] = this.unpackHalfFloat();
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
	
	// Public Domain http://stackoverflow.com/questions/6162651/half-precision-floating-point-in-java
	private static float toFloat( int hbits )
	{
	    int mant = hbits & 0x03ff;            // 10 bits mantissa
	    int exp =  hbits & 0x7c00;            // 5 bits exponent
	    if( exp == 0x7c00 )                   // NaN/Inf
	        exp = 0x3fc00;                    // -> NaN/Inf
	    else if( exp != 0 )                   // normalized value
	    {
	        exp += 0x1c000;                   // exp - 15 + 127
	        if( mant == 0 && exp > 0x1c400 )  // smooth transition
	            return Float.intBitsToFloat( ( hbits & 0x8000 ) << 16
	                                            | exp << 13 | 0x3ff );
	    }
	    else if( mant != 0 )                  // && exp==0 -> subnormal
	    {
	        exp = 0x1c400;                    // make it normal
	        do {
	            mant <<= 1;                   // mantissa * 2
	            exp -= 0x400;                 // decrease exp by 1
	        } while( ( mant & 0x400 ) == 0 ); // while not normal
	        mant &= 0x3ff;                    // discard subnormal bit
	    }                                     // else +/-0 -> +/-0
	    return Float.intBitsToFloat(          // combine all parts
	        ( hbits & 0x8000 ) << 16          // sign  << ( 31 - 15 )
	        | ( exp | mant ) << 13 );         // value << ( 23 - 10 )
	}
}