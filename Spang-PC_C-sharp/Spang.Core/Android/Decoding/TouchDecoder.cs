﻿using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Spang.Core.Utils;
using Spang.Core.Android;

namespace Spang.Core.Decoding
{
    public class TouchDecoder
    {
        public TouchEvent DecodeTouch(UnPacker unpacker)
        {
            int length = unpacker.UnpackByte();
            Touch[] touches = new Touch[length];                        
            for (int i = 0; i < length; i++)
            {
                Touch touch;
                touch.Location.X = unpacker.UnpackShort();
                touch.Location.Y = unpacker.UnpackShort();
                touch.Pressure = unpacker.UnpackByte() / 256.0f;
                touches[i] = touch;
            }

            return new TouchEvent(touches);
        }

    }
}
