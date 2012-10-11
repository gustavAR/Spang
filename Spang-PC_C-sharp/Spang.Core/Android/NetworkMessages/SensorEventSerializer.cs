using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Spang.Core.Utils;

namespace Spang.Core.Android.NetworkMessages
{
    class SensorEventSerializer : Serialization.Serializer<SensorEvent>
    {
        public override void Serialize(Packer packer, SensorEvent message)
        {
            packer.Pack((byte)message.SensorID);
            packer.Pack((byte)message.SensorData.Length);
            packer.Pack(message.SensorData);
        }

        public override SensorEvent Deserialize(UnPacker unpacker)
        {
            int id = unpacker.UnpackByte();
            int length = unpacker.UnpackByte();
            float[] data = unpacker.UnpackFloatArray(length);
        }
    }
}
