using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Net;
using System.Net.Sockets;

namespace Spang_PC_C_sharp
{
	
    class Connection : IConnection
    {
        //The lowest number a port can take without using reserved port numbers.
	    private static int LOW_PORT = 1024;
	    //The largest port number a port can take.
	    private static int HIGH_PORT = 65536;
	    //The time after an application times out. 
	    private static int CONNECTION_TIMEOUT = 15000;
	    //The maximum size of a data packet;
	    private static int DATA_CAPACITY = 1024;

	    
        private UdpClient socket;
        private IPEndPoint address;
        private string hostName;
        private int port;
        
        public Connection()  
	    {
            this.address = null;
		    this.socket = null;
		    this.port = -1;
        }

        public void connect(string hostName, int port)
        {
            if(!canConnect())
            {
                throw new FormatException("The connection is already connected.");
            }

            this.address = new IPEndPoint(IPAddress.Parse(hostName),port);
            this.port = port;
        }

        public void sendUDP(byte[] data)
        {
            socket.Send(data, data.Length,address);
        }
        
        public byte[] reciveUDP()
        {
            return socket.Receive(ref address);
        }
        
        private bool canConnect()
        {
            return this.socket != null;
        }

        public void reconnect()
        {
            this.socket.Connect(address);
        }

        public void sendTCP(byte[] data)
        {
            throw new NotImplementedException();
        }

        public byte[] reciveTCP()
        {
            throw new NotImplementedException();
        }
    }
}
