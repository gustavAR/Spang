using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Spang.Core.Utils;

namespace Spang.Core.Android
{
    public class SensorEventSerializer : Serialization.Serializer<SensorEvent>
    {
        public override void SerializeInternal(Packer packer, SensorEvent message)
        {
            packer.Pack((byte)message.SensorID);
            packer.Pack((byte)message.SensorData.Length);
            packer.Pack(message.SensorData);
        }

        public override SensorEvent DeserializeInternal(UnPacker unpacker)
        {
            int id = unpacker.UnpackByte();
            int length = unpacker.UnpackByte();
            float[] data = unpacker.UnpackFloatArray(length);

            return new SensorEvent(id, data);
        }
    }
}
