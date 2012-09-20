using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Runtime.InteropServices;
using CoreAudioApi;

namespace Spang_PC_C_sharp
{
    class VolumeDown : IMessageHandler
    {
        public void Decode(System.IO.BinaryReader reader)
        {
            VolumeChanger.DecreaseVolume();
        }
    }

    class VolumeUP : IMessageHandler
    {
        public void Decode(System.IO.BinaryReader reader)
        {
            VolumeChanger.IncreaseVolume();
        }
    }

    class VolumeChanger
    {
        private static MMDeviceEnumerator devEnum;
        private static MMDevice defaultDevice;
        public static void mute()
        {
            devEnum = new MMDeviceEnumerator();
            defaultDevice = devEnum.GetDefaultAudioEndpoint(EDataFlow.eRender, ERole.eMultimedia);
            defaultDevice.AudioEndpointVolume.Mute = !defaultDevice.AudioEndpointVolume.Mute;  
        }

        public static void IncreaseVolume()
        {
            defaultDevice.AudioEndpointVolume.VolumeStepUp();
        }

        public static void DecreaseVolume()
        {
            defaultDevice.AudioEndpointVolume.VolumeStepDown();
        }
    }
}
