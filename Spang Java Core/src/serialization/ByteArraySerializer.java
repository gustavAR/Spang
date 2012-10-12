package serialization;

import utils.Packer;
import utils.UnPacker;

public class ByteArraySerializer extends Serializer<byte[]> {
	
	@Override
	public Class<?> getSerializableType() {
		return byte[].class;
	}

	@Override
	protected void serializeInternal(Packer packer, byte[] message) {
		packer.packInt(message.length);
		packer.packByteArray(message);
		
	}

	@Override
	public byte[] deserialize(UnPacker unpacker) {
		int size = unpacker.unpackInt();
		return unpacker.unpackByteArray(size);
	}

}
