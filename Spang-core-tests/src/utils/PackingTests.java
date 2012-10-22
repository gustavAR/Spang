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

import static org.junit.Assert.*;

import java.util.Random;

import org.junit.BeforeClass;
import org.junit.Test;


public class PackingTests {

	private static Random random;
	
	@BeforeClass
	public static void setup() {
		random = new Random();		
	}
	
	
	@Test
	public void canPackByte() {		
		byte b = (byte)(Byte.MIN_VALUE + random.nextInt(Byte.MAX_VALUE));

		Packer packer = new Packer();
		packer.packByte(b);
		UnPacker unpacker = new UnPacker(packer.getPackedData());

		assertEquals(b, unpacker.unpackByte());
	}
	
	@Test
	public void canPackShort() {
		short b = (short)(Short.MIN_VALUE + random.nextInt(Short.MAX_VALUE));

		Packer packer = new Packer();
		packer.packShort(b);
		UnPacker unpacker = new UnPacker(packer.getPackedData());

		assertEquals(b, unpacker.unpackShort());
	}
	
	@Test
	public void canPackInt() {
		int b = random.nextInt();

		Packer packer = new Packer();
		packer.packInt(b);
		UnPacker unpacker = new UnPacker(packer.getPackedData());

		assertEquals(b, unpacker.unpackInt());
	}
	
	@Test
	public void canPackLong() {
		long b = random.nextLong();

		Packer packer = new Packer();
		packer.packLong(b);
		UnPacker unpacker = new UnPacker(packer.getPackedData());

		assertEquals(b, unpacker.unpackLong());
	}
	
	@Test
	public void canPackFloat() {
		float b = random.nextFloat() * random.nextInt();

		Packer packer = new Packer();
		packer.packFloat(b);
		UnPacker unpacker = new UnPacker(packer.getPackedData());

		assertEquals(b, unpacker.unpackFloat(),0);
	}
	
	@Test
	public void canPackDouble() {
		double b = random.nextDouble() * random.nextLong();

		Packer packer = new Packer();
		packer.packDouble(b);
		UnPacker unpacker = new UnPacker(packer.getPackedData());

		assertEquals(b, unpacker.unpackDouble(), 0);
	}
	
	@Test
	public void canPackString() {
		String b = "Hello";

		Packer packer = new Packer();
		packer.packString(b);
		UnPacker unpacker = new UnPacker(packer.getPackedData());

		assertEquals(b, unpacker.unpackString());
	}
	
	
	@Test
	public void canPackByteArray() {
		byte[] array = { 12, 31, 34 };
		
		Packer packer = new Packer();
		packer.packByteArray(array);
		UnPacker unpacker = new UnPacker(packer.getPackedData());
		
		byte[] result = unpacker.unpackByteArray(3);
		for (int i = 0; i < result.length; i++) {
			assertSame(array[0], result[0]);
		}
	}	
	
	@Test
	public void canPackShortArray() {
		short[] array = { 12341, 12351, 23445 };

		Packer packer = new Packer();
		packer.packShortArray(array);
		UnPacker unpacker = new UnPacker(packer.getPackedData());

		short[] result = unpacker.unpackShortArray(3);
		for (int i = 0; i < result.length; i++) {
			assertEquals(array[0], result[0]);
		}
	}
	
	@Test
	public void canPackIntArray() {
		int[] array = { 654123, 123971, 1235762 };

		Packer packer = new Packer();
		packer.packIntArray(array);
		UnPacker unpacker = new UnPacker(packer.getPackedData());

		int[] result = unpacker.unpackIntArray(3);
		for (int i = 0; i < result.length; i++) {
			assertEquals(array[0], result[0]);
		}
	}
	
	@Test
	public void canPackLongArray() {
		long[] array = { 123124124124L, 12354124124113L, 897971238513123L };

		Packer packer = new Packer();
		packer.packLongArray(array);
		UnPacker unpacker = new UnPacker(packer.getPackedData());

		long[] result = unpacker.unpackLongArray(3);
		for (int i = 0; i < result.length; i++) {
			assertEquals(array[0], result[0]);
		}
	}
	@Test
	public void canPackFloatArray() {
		float[] array = { 12313.0f, 213123.0f, 23124341.0f };

		Packer packer = new Packer();
		packer.packFloatArray(array);
		UnPacker unpacker = new UnPacker(packer.getPackedData());

		float[] result = unpacker.unpackFloatArray(3);
		for (int i = 0; i < result.length; i++) {
			assertEquals(array[0], result[0], 0);
		}
	}
	
	@Test
	public void canPackDoubleArray() {
		double[] array = { 123123124E-123, 1231412445E123, 4218768761237862314E-246 };

		Packer packer = new Packer();
		packer.packDoubleArray(array);
		UnPacker unpacker = new UnPacker(packer.getPackedData());

		double[] result = unpacker.unpackDoubleArray(3);
		for (int i = 0; i < result.length; i++) {
			assertEquals(array[0], result[0], 0);
		}
	}
}