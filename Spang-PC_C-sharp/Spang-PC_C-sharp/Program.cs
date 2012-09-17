using System;
using System.Collections.Generic;
using System.Linq;
using System.Windows.Forms;
using System.Net;
using System.Net.Sockets;
using System.Text;

namespace Spang_PC_C_sharp
{
    static class Program
    {
        private static int PORT = 1337;
        private static String ADDRESS = "";
        
        /// <summary>
        /// The main entry point for the application.
        /// </summary>
        [STAThread]
        static void Main()
        {
            Application.EnableVisualStyles();
            Application.SetCompatibleTextRenderingDefault(false);
            Application.Run(new Form1());

            Connection connection = new Connection();
            connection.connect(ADDRESS, PORT);
            
            while(true) 
            {
			    byte[] data = connection.reciveUDP();	
			    string message = Encoding.ASCII.GetString(data, 0, data.Length);
                Console.WriteLine(message);
                if (message == "move")
                {
                    Cursor.Position = new System.Drawing.Point(Cursor.Position.X + 10, Cursor.Position.Y - 10);
                }
		    }
        }
    }
}
