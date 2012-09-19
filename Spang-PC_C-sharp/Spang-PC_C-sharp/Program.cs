using System;
using System.Collections.Generic;
using System.Linq;
using System.Windows.Forms;
using System.Net;
using System.Net.Sockets;
using System.Text;
using System.Threading;

namespace Spang_PC_C_sharp
{
    static class Program
    {
        private static int PORT = 1337;
        private static String ADDRESS = "192.168.33.102";
        
        /// <summary>
        /// The main entry point for the application.
        /// </summary>
        [STAThread]
        static void Main()
        {
          //  Application.EnableVisualStyles();
          //  Application.SetCompatibleTextRenderingDefault(false);
          //  Application.Run(new Form1());

            Server server = new Server();
            IConnection connection = server.ReciveConnection(1337);

            // connection.connect(ADDRESS, PORT);

            new Thread(() =>
            {
                while (true)
                {
                    connection.sendTCP(Encoding.UTF8.GetBytes(Console.ReadLine()));
                }
            }).Start();
            
            while(true) 
            {
                byte[] data = connection.reciveUDP();	
                string message = Encoding.UTF8.GetString(data, 0, data.Length);
                Console.WriteLine(message);
                string[] split = message.Split(';');

                if(message.Contains(";"))
                {
                    int dx = int.Parse(split[0]);
                    int dy = int.Parse(split[1]);
                    Cursor.Position = new System.Drawing.Point(Cursor.Position.X - dx, Cursor.Position.Y - dy);
                }
                else if(message == "click")
                {
                    Console.WriteLine("click");
                    MouseClick();
                } 
		    }
        }

        //Taken from http://social.msdn.microsoft.com/forums/en-US/winforms/thread/86dcf918-0e48-40c2-88ae-0a09797db1ab/ assumed public licence
        [System.Runtime.InteropServices.DllImport("user32.dll")]
        public static extern void mouse_event(int dwFlags, int dx, int dy, int cButtons, int dwExtraInfo);

        public const int MOUSEEVENTF_LEFTDOWN = 0x02;
        public const int MOUSEEVENTF_LEFTUP = 0x04;
        public const int MOUSEEVENTF_RIGHTDOWN = 0x08;
        public const int MOUSEEVENTF_RIGHTUP = 0x10;

        public static void MouseClick()
        {
            int x = Cursor.Position.X;
            int y = Cursor.Position.Y;

            mouse_event(MOUSEEVENTF_LEFTDOWN, x, y, 0, 0);
            mouse_event(MOUSEEVENTF_LEFTUP, x, y, 0, 0);
        }
    }
}
