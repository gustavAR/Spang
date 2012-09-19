using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.IO;

namespace Spang_PC_C_sharp
{
    class MessageHandler
    {
        IDictionary<byte, IMessageHandler> handlers;

        public MessageHandler(Dictionary<byte, IMessageHandler> dict)
        {
            this.handlers = dict;
        }

        public void DecodeMessage(byte[] message)
        {
            var memstream = new MemoryStream(message);
            var reader = new BinaryReader(memstream);
            while (memstream.Position < memstream.Length)
            {
                var b = reader.ReadByte();
                var handler = handlers[b];
                handler.Decode(reader);
            }
        }
    }
}
