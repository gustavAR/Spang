using System;
namespace Spang_PC_C_sharp
{
    interface IMessageDecoder
    {
        void DecodeMessage(byte[] message);
        Phone Phone { set; }
    }
}
