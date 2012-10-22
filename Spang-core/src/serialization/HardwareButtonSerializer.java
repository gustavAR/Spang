package serialization;

import utils.Packer;
import utils.UnPacker;
import network.messages.HardwareButtonEvent;

public class HardwareButtonSerializer extends Serializer<HardwareButtonEvent>{

	public HardwareButtonSerializer() {
		super(HardwareButtonEvent.class);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void serializeInternal(Packer packer, HardwareButtonEvent message) {
		packer.packByte((byte)message.ID);
	}

	@Override
	public HardwareButtonEvent deserialize(UnPacker unpacker) {
		return new HardwareButtonEvent(unpacker.unpackByte());
	}
}
