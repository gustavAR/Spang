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
* Packs a half float value.
* NOTE: The precision of this format is horrible but it saves alot of space.
* Use this when saving space is are more important then precision.
* @param f the float to pack.
* @return this packer.
*/
public Packer packHalfFloat(float f) {
this.reziseIfNeeded(FLOAT_SIZE / 2);
//Converting to
int halfFloat = fromFloat(f);
internalBuffer.put((byte)((halfFloat >>> 8) & 0xFF));
internalBuffer.put((byte)((halfFloat) & 0xFF));
return this;
}

/**
* Packs an array of half floats
* @param f the array to pack.
* @return this packer.
*/
public Packer packHalfFloatArray(float[] f) {
this.reziseIfNeeded(f.length * FLOAT_SIZE / 2);


for (int i = 0; i < f.length; i++) {
int halfFloat = fromFloat(f[i]);
internalBuffer.put((byte)((halfFloat >>> 8) & 0xFF));
internalBuffer.put((byte)((halfFloat) & 0xFF));	
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

//Public Domain http://stackoverflow.com/questions/6162651/half-precision-floating-point-in-java
private static int fromFloat( float fval )
{
int fbits = Float.floatToIntBits( fval );
int sign = fbits >>> 16 & 0x8000; // sign only
int val = ( fbits & 0x7fffffff ) + 0x1000; // rounded value

if( val >= 0x47800000 ) // might be or become NaN/Inf
{ // avoid Inf due to rounding
if( ( fbits & 0x7fffffff ) >= 0x47800000 )
{ // is or must become NaN/Inf
if( val < 0x7f800000 ) // was value but too large
return sign | 0x7c00; // make it +/-Inf
return sign | 0x7c00 | // remains +/-Inf or NaN
( fbits & 0x007fffff ) >>> 13; // keep NaN (and Inf) bits
}
return sign | 0x7bff; // unrounded not quite Inf
}
if( val >= 0x38800000 ) // remains normalized value
return sign | val - 0x38000000 >>> 13; // exp - 127 + 15
if( val < 0x33000000 ) // too small for subnormal
return sign; // becomes +/-0
val = ( fbits & 0x7fffffff ) >>> 23; // tmp exp for subnormal calc
return sign | ( ( fbits & 0x7fffff | 0x800000 ) // add subnormal bit
+ ( 0x800000 >>> val - 102 ) // round depending on cut off
>>> 126 - val ); // div by 2^(1-(exp-127+15)) and >> 13 | exp=0
}
}