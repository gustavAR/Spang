using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Spang.Core.Android
{
    public class HardwareButtonSerializer : Serialization.Serializer<HardwareButtonEvent>
    {
        public override void SerializeInternal(Utils.Packer packer, HardwareButtonEvent message)
        {
            packer.Pack((byte)message.ID);
        }

        public override HardwareButtonEvent DeserializeInternal(Utils.UnPacker unpacker)
        {
            return new HardwareButtonEvent(unpacker.UnpackByte());
        }
    }
}
