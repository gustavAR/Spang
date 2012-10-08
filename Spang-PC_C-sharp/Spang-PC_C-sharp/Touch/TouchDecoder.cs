using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Spang_PC_C_sharp.Touch_Manager
{
    class TouchDecoder
    {
        public TouchEvent DecodeTouch(UnPacker unpacker)
        {
            TouchEvent e = new TouchEvent();
            e.Pointers = new List<Vector3>();

            int length = unpacker.UnpackByte();
            for (int i = 0; i < length; i++)
            {
                Vector3 state = new Vector3();
                state.X = unpacker.UnpackShort();
                state.Y = unpacker.UnpackShort();
                state.Z = unpacker.UnpackByte();
                e.Pointers.Add(state);
            }

            return e;
        }

    }
}
