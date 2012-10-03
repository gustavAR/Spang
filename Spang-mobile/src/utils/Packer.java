package utils;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class Packer {
	private static final int INT_SIZE = Integer.SIZE/8;
	private static final int SHORT_SIZE = Short.SIZE/8;
	private static final int LONG_SIZE = Long.SIZE/8;
	private static final int FLOAT_SIZE = Float.SIZE/8;
	private static final int DOUBLE_SIZE = Double.SIZE/8;
	
	private ByteBuffer packet;
	
	public Packer(int capacity) {
		this.packet = ByteBuffer.allocate(capacity).order(ByteOrder.LITTLE_ENDIAN);
	}

	public byte[] getPackedData() {
		byte[] array = new byte[packet.position()];
		
		packet.rewind();
		packet.get(array);
		
		return array;
	}


	public void clear() {
		packet.clear();
	}
	
	public int packedSize() {
		return this.packet.position();
	}

	private void increaseSize() {
		ByteBuffer buffer = ByteBuffer.allocate(this.packet.capacity() * 2).order(ByteOrder.LITTLE_ENDIAN);
		buffer.put(this.packet.array(), 0, this.packet.position());
		this.packet = buffer;
	}

	
	public void pack(byte b) {
		if(packet.remaining() < 1) {
			this.increaseSize();
		}
		
		packet.put(b);
	}
	

	public void pack(byte[] message) {
		this.packet.put(message);		
	}
	
	public void pack(int i) {
		if(packet.remaining() < INT_SIZE) {
			this.increaseSize();
		}
		
		packet.putInt(i);
	}
	
	public void pack(short s) {
		if(packet.remaining() < SHORT_SIZE) {
			this.increaseSize();
		}
		
		packet.putShort(s);
	}
	
	public void pack(long l) {
		if(packet.remaining() < LONG_SIZE) {
			this.increaseSize();
		}
		
		packet.putLong(l);
	}
	
	public void packHalfFloat(float f) {
		if(packet.remaining() < FLOAT_SIZE / 2) {
			this.increaseSize();
		}
		//Converting to 
		int halfFloat = fromFloat(f);
		packet.put((byte)((halfFloat >>> 8)&  0xFF));
		packet.put((byte)((halfFloat) &  0xFF));
	}
	
	
	public void pack(float f) {
		if(packet.remaining() < FLOAT_SIZE) {
			this.increaseSize();
		}
		
		packet.putFloat(f);
	}
	
	public void pack(double d) {
		if(packet.remaining() < DOUBLE_SIZE) {
			this.increaseSize();
		}
		
		packet.putDouble(d);
	}
	
	public void pack(String s) {
		try {
			byte[] array = s.getBytes("UTF-8");
			if(packet.remaining() < array.length + 4) {
				this.increaseSize();
			}
			packet.putInt(s.length());
			packet.put(array);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
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
	
	
	//Public Domain http://stackoverflow.com/questions/6162651/half-precision-floating-point-in-java
	private static int fromFloat( float fval )
	{
	    int fbits = Float.floatToIntBits( fval );
	    int sign = fbits >>> 16 & 0x8000;          // sign only
	    int val = ( fbits & 0x7fffffff ) + 0x1000; // rounded value

	    if( val >= 0x47800000 )               // might be or become NaN/Inf
	    {                                     // avoid Inf due to rounding
	        if( ( fbits & 0x7fffffff ) >= 0x47800000 )
	        {                                 // is or must become NaN/Inf
	            if( val < 0x7f800000 )        // was value but too large
	                return sign | 0x7c00;     // make it +/-Inf
	            return sign | 0x7c00 |        // remains +/-Inf or NaN
	                ( fbits & 0x007fffff ) >>> 13; // keep NaN (and Inf) bits
	        }
	        return sign | 0x7bff;             // unrounded not quite Inf
	    }
	    if( val >= 0x38800000 )               // remains normalized value
	        return sign | val - 0x38000000 >>> 13; // exp - 127 + 15
	    if( val < 0x33000000 )                // too small for subnormal
	        return sign;                      // becomes +/-0
	    val = ( fbits & 0x7fffffff ) >>> 23;  // tmp exp for subnormal calc
	    return sign | ( ( fbits & 0x7fffff | 0x800000 ) // add subnormal bit
	         + ( 0x800000 >>> val - 102 )     // round depending on cut off
	      >>> 126 - val );   // div by 2^(1-(exp-127+15)) and >> 13 | exp=0
	}
}