package serialization;

import serialization.Serializer;
import utils.Packer;
import utils.UnPacker;

public class StringSerializer extends Serializer<String>{

	public Class<?> getSerializableType() {
		return String.class;
	}

	@Override
	protected void serializeInternal(Packer packer, String message) {
		packer.packString(message);
	}

	@Override
	public String deserialize(UnPacker unpacker) {
		return unpacker.unpackString();
	}

}
