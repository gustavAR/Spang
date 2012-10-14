/*  Copyright 2012 Joakim Johansson, Lukas Kurtyan, Gustav Alm Rosenblad and Pontus Pall
 *  
 *  This file is part of Spang.

    Spang is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Spang is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Spang.  If not, see <http://www.gnu.org/licenses/>.
 */
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
