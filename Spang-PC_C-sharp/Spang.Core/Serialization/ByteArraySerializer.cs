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
