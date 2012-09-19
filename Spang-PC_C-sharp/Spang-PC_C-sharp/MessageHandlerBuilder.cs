using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Spang_PC_C_sharp
{
    class MessageHandlerBuilder
    {
        public static MessageHandler Build()
        {
            var dict = new Dictionary<byte, IMessageHandler>();

            dict.Add(0, new MouseLeftClicker());
            dict.Add(1, new MouseRightClicker());
            dict.Add(2, new MouseMover());
            dict.Add(3, new Accelerometer());

            return new MessageHandler(dict);
        }
    }
}
