using System;
using System.Collections.Generic;
using System.Linq;
using System.Windows.Forms;
using System.Net;
using System.Net.Sockets;
using System.Text;
using System.Threading;
using System.Drawing;
using Spang.Core.Utils;
using Spang.Core.Network;

using System.Drawing.Imaging;
using WindowsInput;
using Spang.Core.Decoding;
using Spang.Core.Android;
using Spang.Core.Serialization;

namespace Spang_PC_C_sharp
{
    static class Program
    {        
        /// <summary>
        /// The main entry point for the application.
        /// </summary>
        static void Main()
        {
            #region Generate and show QR

            //Find our local IP-address
            IPHostEntry host;
            string localIP = "";
            host = Dns.GetHostEntry(Dns.GetHostName());
            foreach (IPAddress ip in host.AddressList)
            {
                if (ip.AddressFamily == AddressFamily.InterNetwork)
                {
                    localIP = ip.ToString();
                }
            }

            QRCodeGenerator.ShowQRCode(localIP + "/" + 1337, 120, 120, ImageFormat.Png);//120 are ok dimensions

            #endregion

            AndroidPhone phone = new AndroidPhone();
            DesktopController controller = new DesktopController(phone, new OsInterface());

            SerializeManager serializeManager = new SerializeManager();
            serializeManager.RegisterSerilizer(new TouchEventSerializer());
            serializeManager.RegisterSerilizer(new SensorEventSerializer());
            serializeManager.RegisterSerilizer(new StringSerializer());

            IServer server = new Server(serializeManager);
            server.ConnectionTimeout = 5000;
            server.Start(1337);

            TouchDecoder decoder = new TouchDecoder();

            phone.Tap += () => controller.LeftClick();
            phone.Up += () => controller.mouseUp();
            phone.MultiTap += (x) => { if (x == 2) controller.RightClick(); };
            phone.Down += () => controller.mouseDown();
            phone.Move += (x, y) => controller.MoveMouse(moveSpeed(x), moveSpeed(y));
            phone.MulitiMove += (c, x, y) => controller.VerticalScroll(y * 10);


            phone.Pinch += (x) =>
            {
                InputSimulator.SimulateKeyDown(VirtualKeyCode.CONTROL);
                controller.VerticalScroll(x);
                InputSimulator.SimulateKeyUp(VirtualKeyCode.CONTROL);
            };


            int bytesRecived = 0;
            long totalbytesRecived = 0;
            System.Timers.Timer timer = new System.Timers.Timer(1000);
            timer.AutoReset = true;
            timer.Elapsed += (s,e) =>
            {

                Console.Title = "Total Recived: " + totalbytesRecived + " Current ByteRate: " + ((double)bytesRecived) / 1000;
                totalbytesRecived += bytesRecived;
                bytesRecived = 0;

            };
            timer.Start();

            server.Connected += (x, y) => Console.WriteLine("A connection was recived");                  
            server.Recived += (x, message) =>
            {
                if (message.Message is IPhoneMessage)
                {
                    phone.ProcessMessage((IPhoneMessage)message.Message);
                }
                else 
                {
                    Console.WriteLine(message.Message);
                }

            };
            server.Dissconnected += (x, y) => Console.WriteLine("Oh no we dced ;(");
        }

        private static int moveSpeed(int p)
        {
            return p * ((int)Math.Sqrt(Math.Abs(p))) / 2;
        }
    }
}
