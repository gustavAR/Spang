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
		byte[] array = new byte[packet.position() + 1];
		
		packet.rewind();
		packet.get(array);
		packet.clear();
		
		return array;
	}
	
	public void pack(byte b) {
		if(packet.remaining() < 1) {
			this.increaseSize();
		}
		
		packet.put(b);
	}
	
	private void increaseSize() {
		ByteBuffer buffer = ByteBuffer.allocate(this.packet.capacity() * 2).order(ByteOrder.LITTLE_ENDIAN);
		buffer.put(this.packet.array(), 0, this.packet.position() + 1);
		this.packet = buffer;
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
}