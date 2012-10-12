using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Spang.Core.Utils;

namespace Spang.Core.Serialization
{
    public class SerializeManager
    {
        private int nextID = 0;

        private Dictionary<int, Type> idToType;
        private Dictionary<Type, int> typeToID;

        private Dictionary<Type, ISerializer> serializerByType;

        public SerializeManager()
        {
            this.idToType = new Dictionary<int, Type>();
            this.typeToID = new Dictionary<Type, int>();
            this.serializerByType = new Dictionary<Type, ISerializer>();
        }

        public void RegisterSerilizer(ISerializer serializer)
        {
            int id = nextID++;
            this.idToType.Add(id, serializer.SerializeType);
            this.typeToID.Add(serializer.SerializeType, id);
            this.serializerByType.Add(serializer.SerializeType, serializer);
        }

        public void Serialize(Packer packer, object message)
        {
            if (!this.serializerByType.ContainsKey(message.GetType()))
                throw new ArgumentException("Cannot serialize type: " + message.GetType());

            this.PackID(packer, this.typeToID[message.GetType()]);

            ISerializer serializer = this.serializerByType[message.GetType()];
            serializer.Serialize(packer, message);
        }

        public object Deserialize(UnPacker unpacker)
        {
            int id = this.UnpackID(unpacker);
            ISerializer serializer = this.serializerByType[this.idToType[id]];
            return serializer.Deserialize(unpacker);
        }

        private int UnpackID(UnPacker unpacker)
        {
            if (this.nextID < sbyte.MaxValue)
            {
                return unpacker.UnpackByte();
            }
            else if (this.nextID < short.MaxValue)
            {
                return unpacker.UnpackShort();
            }
            else
            {
                return unpacker.UnpackInteger();
            }
        }

        private void PackID(Packer packer, int id)
        {
            if (this.nextID < sbyte.MaxValue)
            {
                packer.Pack((sbyte)id);
            }
            else if (this.nextID < short.MaxValue)
            {
                packer.Pack((short)id);
            }
            else
            {
                packer.Pack(id);
            }
        }
    }
}
