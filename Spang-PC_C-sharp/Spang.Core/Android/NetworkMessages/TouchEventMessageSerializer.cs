using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Spang.Core.Utils;

namespace Spang.Core.Android
{
    public class TouchEventSerializer : Serialization.Serializer<TouchEvent>
    {
        public override void SerializeInternal(Packer packer, TouchEvent message)
        {
            packer.Pack((byte)message.Touches.Count);
            foreach (var touch in message.Touches)
            {
                packer.Pack((short)touch.Location.X);
                packer.Pack((short)touch.Location.Y);
                packer.Pack((byte)(touch.Pressure * 256.0f));
            }
        }

        public override TouchEvent DeserializeInternal(UnPacker unpacker)
        {
            int count = unpacker.UnpackByte();
            Touch[] touches = new Touch[count];
            for (int i = 0; i < count; i++)
            {
                Touch touch;
                touch.Location.X = unpacker.UnpackShort();
                touch.Location.Y = unpacker.UnpackShort();
                touch.Pressure = (unpacker.UnpackByte() / 256.0f);
                touches[i] = touch;
            }

            return new TouchEvent(touches);
        }
    }
}
