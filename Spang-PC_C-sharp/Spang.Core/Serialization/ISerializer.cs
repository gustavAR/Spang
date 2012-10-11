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
        public abstract void Serialize(Packer packer, T message);
        public abstract T Deserialize(UnPacker unpacker);

        public sealed void Serialize(Packer packer, object message)
        {
            if (message.GetType() != typeof(T))
                throw new InvalidCastException(string.Format("Expected type: {0} but was {1}", message.GetType(), typeof(T)));

            this.Serialize(packer, (T)message);
        }

        public Type SerializeType
        {
            get { return typeof(T); }
        }
    }
}
