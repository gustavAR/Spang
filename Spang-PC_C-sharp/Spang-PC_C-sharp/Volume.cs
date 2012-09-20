using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Runtime.InteropServices;

namespace Spang_PC_C_sharp
{
    class VolumeDown : IMessageHandler
    {
        public void Decode(System.IO.BinaryReader reader)
        {
            VolumeChanger.ChangeVolume(-(short.MaxValue / 10));
        }
    }

    class VolumeUP : IMessageHandler
    {
        public void Decode(System.IO.BinaryReader reader)
        {
            VolumeChanger.ChangeVolume(short.MaxValue/10);
        }
    }

    class VolumeChanger
    {
        [DllImport("winmm.dll")]
        public static extern int waveOutGetVolume(IntPtr hwo, out uint dwVolume);

        [DllImport("winmm.dll")]
        public static extern int waveOutSetVolume(IntPtr hwo, uint dwVolume);

        public static void ChangeVolume(short delta)
        {
            uint CurrVol = 0;
         
             waveOutGetVolume(IntPtr.Zero, out CurrVol);
             ushort CalcVol = (ushort)(CurrVol & 0x0000ffff);
             CalcVol = (ushort) ((int)CalcVol + delta);

             uint NewVolumeAllChannels = (((uint)CalcVol & 0x0000FFFF) | ((uint)CalcVol << 16));
             
             waveOutSetVolume(IntPtr.Zero, NewVolumeAllChannels);
        }

        public void SetVolume(uint volume)
        {

        }
    }
}
