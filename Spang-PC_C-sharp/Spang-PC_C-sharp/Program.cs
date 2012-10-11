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

            IMessageDecoder messageHandler = new MessageDecoder();
            AndroidPhone phone = new AndroidPhone(messageHandler);
            DesktopController controller = new DesktopController(phone, new OsInterface());

            IServer server = new Server();
            server.ConnectionTimeout = 5000;

            server.Start(1337);

            TouchDecoder decoder = new TouchDecoder();

            TouchEventManager em = new TouchEventManager();
       /*     em.Tap += () =>  Console.WriteLine("Just tapped"); 
            em.MultiTap += (x) =>  Console.WriteLine("Just mulit Tapped Count:{0}", x); 
            em.Up += () => Console.WriteLine("Just Upped"); 
            em.Down += () =>  Console.WriteLine("Just Downed"); 
            em.Move += (x,y) => Console.WriteLine("Just moved: X: {0} , Y: {1}", x, y); */

            em.Tap += () => controller.LeftClick();
            em.Up += () => controller.mouseUp();
            em.MultiTap += (x) => { if (x == 2) controller.RightClick(); };
            em.Down += () => controller.mouseDown();
            em.Move += (x, y) => controller.MoveMouse(moveSpeed(x), moveSpeed(y));
            em.MulitiMove += (c, x, y) => controller.VerticalScroll(y * 10);


            em.Pinch += (x) =>
            {
                InputSimulator.SimulateKeyDown(VirtualKeyCode.CONTROL);
       //         Console.WriteLine("Just pinched! VALUE:{0}", x);

                controller.VerticalScroll(x);

                InputSimulator.SimulateKeyUp(VirtualKeyCode.CONTROL);
            };
            


            TouchStateMachine stateMachine = new TouchStateMachine(em);


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
               // Console.WriteLine("Recived a message of size:{0}", message.Data.Length);

                UnPacker unPacker = new UnPacker(message.Data);
                bytesRecived += unPacker.remaining();
                while(unPacker.remaining() > 0) {
                    int id = unPacker.UnpackByte();
                    if (id == 0)
                    {
                        //  Console.WriteLine("Recived mouse event!");
                        TouchEvent e = decoder.DecodeTouch(unPacker);
                        stateMachine.Update(e);
                    }
                    else if(id == 1)
                    {
                        String s = unPacker.UnpackString();
                        controller.NetworkedText(s);
                    }
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
