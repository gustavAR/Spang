using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Net.Sockets;
using System.Net;

namespace Spang_PC_C_sharp
{
    class ConnectionListener
    {
        private const int DEFAULT_CONNECTION_TIMEOUT = 1000;

        public int ConnectTimeout
        {
            get;
            set; 
        }

        public ConnectionListener()
        {
            this.ConnectTimeout = DEFAULT_CONNECTION_TIMEOUT;
        }

        public IConnection ReciveConnection(int port)
        {

            Connection connection;
            while (true)
            {
                IPEndPoint endpoint = ReciveConnectionRequest(port);
                UdpClient client = SendConnectionAck(endpoint);

                if (RecivedConnectionAck(endpoint, client))
                {
                    connection = new Connection(client, endpoint);
                    break;
                }
                else
                {
                    client.Close();
                }
            }

            return connection;
        }

        private bool RecivedConnectionAck(IPEndPoint endpoint, UdpClient client)
        {
            try
            {
                //Recive any message and we know that we have connected.
                int reciveTimeout = client.Client.ReceiveTimeout;
                client.Client.ReceiveTimeout = this.ConnectTimeout;
                client.Receive(ref endpoint);
                client.Client.ReceiveTimeout = reciveTimeout;
                return true; 
            }
            catch (Exception)
            {
                return false;
            }
        }

        private UdpClient SendConnectionAck(IPEndPoint endpoint)
        {
            UdpClient client = new UdpClient(new IPEndPoint(IPAddress.Any, 0));
            client.Connect(endpoint);

            Packer packer = new Packer(4);
            packer.Pack(((IPEndPoint)client.Client.LocalEndPoint).Port);      
            client.Send(packer.GetPackedData(), 4);
            return client;
        }

        private IPEndPoint ReciveConnectionRequest(int port)
        {

            UdpClient listener = new UdpClient(new IPEndPoint(IPAddress.Any, 1337));
            IPEndPoint endpoint = new IPEndPoint(IPAddress.Any, port);
            listener.Receive(ref endpoint);
            listener.Close();
            return endpoint;
        }
    }
}
