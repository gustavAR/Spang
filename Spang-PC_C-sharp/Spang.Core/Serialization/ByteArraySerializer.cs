using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Spang.Core.Serialization
{
    /// <summary>
    /// Does nothing since a byte array is already serialized. 
    /// </summary>
    public class ByteArraySerializer : Serializer<byte[]>
    {
        public override void SerializeInternal(Utils.Packer packer, byte[] message)
        {
            packer.Pack(message.Length);
            packer.Pack(message);
        }

        public override byte[] DeserializeInternal(Utils.UnPacker unpacker)
        {
            return unpacker.UnpackByteArray(unpacker.UnpackInteger());
        }
    }
}
