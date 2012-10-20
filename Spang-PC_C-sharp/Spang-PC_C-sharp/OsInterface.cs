using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Windows.Forms;
using CoreAudioApi;

namespace Spang_PC_C_sharp
{
    class OsInterface 
    {
        [System.Runtime.InteropServices.DllImport("user32.dll")]
        private static extern void mouse_event(int dwFlags, int dx, int dy, int cButtons, int dwExtraInfo);

        private MMDeviceEnumerator devEnum;
        private MMDevice defaultDevice;


        public OsInterface() 
        {
            devEnum = new MMDeviceEnumerator();
            defaultDevice = devEnum.GetDefaultAudioEndpoint(EDataFlow.eRender, ERole.eMultimedia);
        }


        /// <summary>
        /// Sends a virtual keypress to the active application.
        /// </summary>
        /// <param name="key">The character to send.</param>
        /// <param name="modifier">Modifiers on the character.</param>
        public void SendKey(char key, KeyModifier modifier = KeyModifier.None)
        {
            string modifiedKey = this.formatKey(key);

            addModifiers(modifiedKey, modifier);

            SendKeys.SendWait(modifiedKey);              
        }

        /// <summary>
        /// Sends a virtual keypress to the active application.
        /// Some keys do not have a proper unicode.
        /// We therefore handle them in this string overload.
        /// 
        /// This method should never be called with a single char.
        /// </summary>
        /// <param name="key">The character to send.</param>
        /// <param name="modifier">Modifiers on the character.</param>
        public void SendKey(string key, KeyModifier modifier = KeyModifier.None)
        {
            key = addModifiers(key, modifier);

            SendKeys.SendWait(key);
        }

        /// <summary>
        /// Adds the proper chars to the beginning of the string 
        /// for use with Sendkeys.
        /// </summary>
        /// <param name="key"></param>
        /// <param name="modifier"></param>
        /// <returns></returns>
        private static string addModifiers(string key, KeyModifier modifier)
        {
            if ((modifier & KeyModifier.Alt) == KeyModifier.Alt)
            {
                key = '%' + key;
            }
            if ((modifier & KeyModifier.Ctrl) == KeyModifier.Ctrl)
            {
                key = '^' + key;
            }
            if ((modifier & KeyModifier.Shift) == KeyModifier.Shift)
            {
                key = '+' + key;
            }
            return key;
        }


        private string formatKey(char key)
        {
            //This method reformats the key if it is any of the special 
            //characters that SendKeys cannot handle.

            switch (key)
            {
                case '\u0008' : //Unicode for backspace.
                    return "{BACKSPACE}";
                case '\u00A0' : //Unicode for Break.
                    return "{BREAK}";
                case '\n' : //Enter
                    return "{ENTER}";
                case '\t' : //Tab
                    return "{TAB}";
                default :
                    return key.ToString();
            }
        }

        /// <summary>
        /// Mutes the mastervolume
        /// </summary>
        public void mute()
        {
            defaultDevice.AudioEndpointVolume.Mute = !defaultDevice.AudioEndpointVolume.Mute;
        }

        /// <summary>
        /// Increases the mastervolume slightly.
        /// </summary>
        public void IncreaseVolume()
        {
            defaultDevice.AudioEndpointVolume.VolumeStepUp();
        }

        /// <summary>
        /// Decreases the mastervolume slightly.
        /// </summary>
        public void DecreaseVolume()
        {
            defaultDevice.AudioEndpointVolume.VolumeStepDown();
        }

        /// <summary>
        /// Sends a mouse event.
        /// </summary>
        /// <param name="mEvent">The event to send.</param>
        public void SendMouseEvent(MouseEvent mEvent)
        {
            this.SendMouseEvent(mEvent, Cursor.Position.X, Cursor.Position.Y, 0, 0);
        }

        /// <summary>
        /// Sends a mouse event with some data assosiated with it.
        /// </summary>
        /// <param name="mEvent">The event to send.</param>
        /// <param name="data">Some data</param>
        /// <param name="extra">Extra data.</param>
        public void SendMouseEvent(MouseEvent mEvent, int data, int extra = 0)
        {
            this.SendMouseEvent(mEvent, 0, 0, data, extra);
        }

        /// <summary>
        /// Sends a mouse event.
        /// </summary>
        /// <param name="mEvent">The event to send.</param>
        /// <param name="x">The x-position to send it to.</param>
        /// <param name="y">The y-position to send it to.</param>
        /// <param name="data">The data to send.</param>
        /// <param name="extra">Any extra data.</param>
        public void SendMouseEvent(MouseEvent mEvent, int x, int y, int data, int extra = 0)
        {
            mouse_event((int)mEvent, x, y, data, extra);
        }
    }

    /// <summary>
    /// Enum stroing the key modifiers.
    /// </summary>
    [Flags]
    public enum KeyModifier
    {
        None = 0x00,
        Shift = 0x01,
        Alt = 0x02,
        Ctrl = 0x04
    }

    /// <summary>
    /// Enum storing int codes for mouse events
    /// </summary>
    [Flags]
    enum MouseEvent : int
    {
        LeftDown = 0x02,
        LeftUp = 0x04,
        RightDown = 0x08,
        RightUp = 0x10,
        MouseWheel = 0x0800,
        MouseHWheel = 0x01000
    }
}
