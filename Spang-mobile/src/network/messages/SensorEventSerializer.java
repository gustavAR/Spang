package network.messages;

import serialization.Serializer;
import utils.Packer;
import utils.UnPacker;

public class SensorEventSerializer extends Serializer<SensorEvent>{

	public Class<?> getSerializableType() {
		return SensorEvent.class;
	}

    public void serializeInternal(Packer packer, SensorEvent message)
    {
        packer.packByte((byte)message.SensorID);
        packer.packShort((byte)message.SensorData.length);
        packer.packFloatArray(message.SensorData);
    }

    public SensorEvent deserialize(UnPacker unpacker)
    {
        int id = unpacker.unpackByte();
        int length = unpacker.unpackByte();
        float[] data = unpacker.unpackFloatArray(length);

        return new SensorEvent(id, data);
    }

}
