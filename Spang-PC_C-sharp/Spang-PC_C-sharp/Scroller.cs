using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Spang_PC_C_sharp
{
    /// <summary>
    /// Class for vertical scrolling
    /// </summary>
    class VScroller : IMessageHandler
    {
        public void Decode(System.IO.BinaryReader reader)
        {
            MouseEventSender.SendEvent(MouseEvent.MouseWheel, reader.ReadInt32(), 0);
        }
    }
    
    /// <summary>
    /// Class for horizontal scrolling
    /// </summary>
    class HScroller : IMessageHandler
    {
        public void Decode(System.IO.BinaryReader reader)
        {
            MouseEventSender.SendEvent(MouseEvent.MouseWheel, reader.ReadInt32(), 0x01000);
        }
    }
}
