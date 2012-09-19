using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Windows.Forms;
using System.IO;

namespace Spang_PC_C_sharp
{
    /// <summary>
    /// Class that handles a left mouse button click message
    /// </summary>
    class MouseLeftClicker : IMessageHandler
    {
        public void Decode(BinaryReader reader)
        {
            MouseEventSender.SendEvent(MouseEvent.LeftDown);
            MouseEventSender.SendEvent(MouseEvent.LeftUp);

            Console.WriteLine("Clicked left");
        }

    }

    /// <summary>
    /// Class that handles a right mouse button click message
    /// </summary>
    class MouseRightClicker : IMessageHandler
    {
        public void Decode(BinaryReader reader)
        {
            MouseEventSender.SendEvent(MouseEvent.RightDown);
            MouseEventSender.SendEvent(MouseEvent.RightUp);

            Console.WriteLine("Clicked right");
        }
    }

    /// <summary>
    /// Class that sends events to the operating system
    /// </summary>
    static class MouseEventSender
    {
        //Taken from http://social.msdn.microsoft.com/forums/en-US/winforms/thread/86dcf918-0e48-40c2-88ae-0a09797db1ab/ assumed public licence
        [System.Runtime.InteropServices.DllImport("user32.dll")]
        private static extern void mouse_event(int dwFlags, int dx, int dy, int cButtons, int dwExtraInfo);

        public static void SendEvent(MouseEvent mouseEvent)
        {
            int x = Cursor.Position.X;
            int y = Cursor.Position.Y;

            mouse_event((int)mouseEvent, x, y, 0, 0);
        }
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
        RightUp = 0x10
    }
}
