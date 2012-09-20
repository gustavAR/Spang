using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Runtime.InteropServices;
using CoreAudioApi;

namespace Spang_PC_C_sharp
{
    class VolumeChanger
    {
        private static MMDeviceEnumerator devEnum;
        private static MMDevice defaultDevice;

        public static void init()
        {
            devEnum = new MMDeviceEnumerator();
            defaultDevice = devEnum.GetDefaultAudioEndpoint(EDataFlow.eRender, ERole.eMultimedia);
        }

        public static void mute()
        {
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
