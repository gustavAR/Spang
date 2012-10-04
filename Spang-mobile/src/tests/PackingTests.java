package tests;


import static org.junit.Assert.*;

import org.junit.Test;

import utils.Packer;
import utils.UnPacker;

public class PackingTests {

	@Test
	public void canPackByte() {		

	}
	
	@Test
	public void canPackShort() {
		short b = 23121;

		Packer packer = new Packer();
		packer.packShort(b);
		UnPacker unpacker = new UnPacker(packer.getPackedData());

		assertSame(b, unpacker.unpackShort());
	}
	
	@Test
	public void canPackInt() {
		int b = 41254121;

		Packer packer = new Packer();
		packer.packInt(b);
		UnPacker unpacker = new UnPacker(packer.getPackedData());

		assertSame(b, unpacker.unpackInt());
	}
	
	@Test
	public void canPackLong() {
		long b = 12345242123141L;

		Packer packer = new Packer();
		packer.packLong(b);
		UnPacker unpacker = new UnPacker(packer.getPackedData());

		assertSame(b, unpacker.unpackLong());
	}
	
	@Test
	public void canPackFloat() {
		float b = 1233E1f;

		Packer packer = new Packer();
		packer.packFloat(b);
		UnPacker unpacker = new UnPacker(packer.getPackedData());

		assertSame(b, unpacker.unpackFloat());
	}
	
	@Test
	public void canPackDouble() {
		double b = 123123E12;

		Packer packer = new Packer();
		packer.packDouble(b);
		UnPacker unpacker = new UnPacker(packer.getPackedData());

		assertSame(b, unpacker.unpackDouble());
	}
	
	@Test
	public void canPackString() {
		String b = "Hello";

		Packer packer = new Packer();
		packer.packString(b);
		UnPacker unpacker = new UnPacker(packer.getPackedData());

		assertSame(b, unpacker.unpackString());
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
			assertSame(array[0], result[0]);
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
			assertSame(array[0], result[0]);
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
			assertSame(array[0], result[0]);
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
			assertSame(array[0], result[0]);
		}
	}
}