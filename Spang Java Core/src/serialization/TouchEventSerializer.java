package serialization;

import network.messages.Touch;
import network.messages.TouchEvent;
import serialization.Serializer;
import utils.Packer;
import utils.UnPacker;

public class TouchEventSerializer extends Serializer<TouchEvent>{

	public Class<?> getSerializableType() {
		return TouchEvent.class;
	}

	@Override
	protected void serializeInternal(Packer packer, TouchEvent message) {
		packer.packByte((byte)message.data.length);
		for (Touch touch : message.data) {
			packer.packShort((short)touch.X);
			packer.packShort((short)touch.Y);
			packer.packByte((byte)(touch.Pressure * 256.0f));
		}
	}

	@Override
	public TouchEvent deserialize(UnPacker unpacker) {
		int count = unpacker.unpackByte();
		Touch[] touches = new Touch[count];
		for (int i = 0; i < count; i++) {
			float x = unpacker.unpackShort();
			float y = unpacker.unpackShort();
			float pressure = (unpacker.unpackByte() / 256.0f);
			touches[i] = new Touch(x,y,pressure);
		}
		
		return new TouchEvent(touches);				
	}

}
