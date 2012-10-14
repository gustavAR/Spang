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

namespace Spang.Core.Serialization
{
    public interface ISerializer
    {
        void Serialize(Packer packer, Object message);
        Object Deserialize(UnPacker unpacker);
        Type SerializeType { get; }
    }

    public abstract class Serializer<T> : ISerializer
    {
        public abstract void SerializeInternal(Packer packer, T message);
        public abstract T DeserializeInternal(UnPacker unpacker);

        public void Serialize(Packer packer, object message)
        {
            if (message.GetType() != typeof(T))
                throw new InvalidCastException(string.Format("Expected type: {0} but was {1}", message.GetType(), typeof(T)));

            this.SerializeInternal(packer, (T)message);
        }

        public object Deserialize(UnPacker unpacker)
        {
            return DeserializeInternal(unpacker);
        }

        public Type SerializeType
        {
            get { return typeof(T); }
        }


    }
}
