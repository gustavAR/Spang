using System;
using Spang.Core.Android;
namespace Spang.Core.Decoding
{
    public interface IMessageDecoder
    {
        void DecodeMessage(byte[] message);
        IPhone Phone { set; }
    }
}
