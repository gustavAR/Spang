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
            dict.Add(4, new Luminance());
            dict.Add(5, new Gyroscope());
            dict.Add(6, new MagneticField());
            dict.Add(7, new VolumeUP());
            dict.Add(8, new VolumeDown());
            dict.Add(9, new Proximity());
            dict.Add(10, new NetworkedText());

            return new MessageHandler(dict);
        }
    }
}
