using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Spang.Core.Utils;

namespace Spang.Core.Serialization
{
    public class StringSerializer : Serializer<String> 
    {
        public override void SerializeInternal(Packer packer, string message)
        {
            packer.Pack(message);
        }

        public override string DeserializeInternal(UnPacker unpacker)
        {
            return unpacker.UnpackString();
        }
    }
}
