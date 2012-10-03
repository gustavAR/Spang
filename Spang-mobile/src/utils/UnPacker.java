package utils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import network.exceptions.NotImplementedException;

public class UnPacker {
	private ByteBuffer buffer;	
	
	public UnPacker(byte[] data) {
		this.buffer = ByteBuffer.allocate(data.length).order(ByteOrder.LITTLE_ENDIAN);
	}

	public byte unpackInt8() {
		return this.buffer.get();
	}	
	
	public short unpackInt16() {
		return this.buffer.getShort();
	}
	
	public int unpackInt32() {
		return this.buffer.getInt();
	}
	
	public long unpackInt64() {
		//TODO implement
		throw new NotImplementedException();
	}
	
	public int unpackUInt8() {
		//TODO implement
		throw new NotImplementedException();
	}

	public int unpackUInt16() {
		//TODO implement
		throw new NotImplementedException();
	}

	public long unpackUInt32() {
		//TODO implement
		throw new NotImplementedException();
	}
	
	public float unpackHalfFloat() {
		throw new NotImplementedException();
	}
	
	public float unpackFloat() {
		return this.buffer.getFloat();
	}
	
	public double unpackDouble() {
		return this.buffer.getDouble();
	}	
}