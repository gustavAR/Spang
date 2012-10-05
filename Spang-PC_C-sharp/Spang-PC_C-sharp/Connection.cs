using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Net.Sockets;
using System.Net;
using System.Collections.Concurrent;
using System.Threading;
using System.Diagnostics;

namespace Spang_PC_C_sharp
{
    class Connection : IConnection
    {
        private readonly int port;
        private readonly IPEndPoint remote;
        private readonly UdpClient client;
        private readonly AccTimer reliableTimer;
        private readonly IDictionary<Protocol, ProtocolManager> protocols;

        public Connection(UdpClient client, IPEndPoint remote)
        {
            this.client = client;
            this.port = ((IPEndPoint)client.Client.LocalEndPoint).Port;
            this.remote = remote;
            this.reliableTimer = new AccTimer(this);
            new Thread(reliableTimer.DoWork).Start();

            this.protocols = new Dictionary<Protocol, ProtocolManager>();
            this.protocols.Add(Protocol.Unordered, new UnorderedProtocol(this));
            this.protocols.Add(Protocol.Ordered, new OrderedProtocol(this));
            this.protocols.Add(Protocol.Reliable, new ReliableProtocol(this));
        }

        public void Send(byte[] data)
        {
            this.Send(data, Protocol.Unordered);
        }

        public void Send(byte[] data, Protocol protocol)
        {
            this.protocols[protocol].sendMessage(data);
        }

        private void SendInternal(byte[] data)
        {
            client.Send(data, data.Length);
        }

        public byte[] Receive()
        {
            while (true)
            {
                IPEndPoint ep = new IPEndPoint(IPAddress.Any, port);
                byte[] recived = client.Receive(ref ep);

                if (recived.Length == 0)
                    return recived;

                Protocol protocol = protocolFromID((recived[0]));

                recived = this.protocols[protocol].processRecivedMessage(recived);
                if (recived != null)
                    return recived;

            }
        }

        private Protocol protocolFromID(int id)
        {
            foreach (var protocol in this.protocols.Keys)
            {
                if ((((int)protocol) & id) == (int)protocol)
                {
                    return protocol;
                }
            }

            throw new ArgumentException();
        }

        public int ReciveTimeout
        {
            get
            {
                return this.client.Client.ReceiveTimeout;
            }
            set
            {
                this.client.Client.ReceiveTimeout = value;
            }
        }

        public int SendTimeout
        {
            get
            {
                return this.client.Client.SendTimeout;
            }
            set
            {
                this.client.Client.SendTimeout = value;
            }
        }

        public void Close()
        {
            this.client.Close();
        }

        public bool Connected
        {
            get { return this.client.Client.Connected; }
        }

        public System.Net.IPEndPoint RemoteEndPoint
        {
            get { return this.remote; }
        }

        public System.Net.IPEndPoint LocalEndPoint
        {
            get { return (IPEndPoint)this.client.Client.RemoteEndPoint; }
        }



        private class MessageResendTimer
        {
            public static TimeSpan TargetInterval = TimeSpan.FromMilliseconds(100.0d); //Resends after 100 milisec. 
            public Stopwatch stopWatch = new Stopwatch();
            public int accnumber;
            public byte[] message;
        }

        private class AccTimer : ContinuousWorker
        {
            private IDictionary<int, MessageResendTimer> resenders;
            private Connection connection;
            public AccTimer(Connection connection)
            {
                this.resenders = new ConcurrentDictionary<int, MessageResendTimer>();
                this.connection = connection;
            }

            public void addResender(MessageResendTimer resender)
            {
                resenders.Add(resender.accnumber, resender);
            }

            public void removeResender(int accNum)
            {
                resenders.Remove(accNum);
            }

            private void updateAndSend() 
            {
    		    foreach (var resender in this.resenders.Values)
	            {
                    if (resender.stopWatch.Elapsed >= MessageResendTimer.TargetInterval)
                    {
                        resender.stopWatch.Restart();
                        this.connection.SendInternal(resender.message);
                    }
			    }		
            }

            protected override void DoWorkInternal()
            {
                try
                {
                    Thread.Sleep(1000);
                    updateAndSend();
                }
                catch (Exception exe)
                {
                    this.StopWorking();
                }
            }
        }

        private abstract class ProtocolManager
        {
            protected abstract int getHeaderLength();
            protected abstract void fixMessage(Packer packer, byte[] message);
            protected abstract byte[] processMessage(UnPacker unPacker);
            protected Connection connection;

            public ProtocolManager(Connection connection)
            {
                this.connection = connection;
            }


            public void sendMessage(byte[] message)
            {
                Packer packer = new Packer(message.Length + getHeaderLength());
                fixMessage(packer, message);
                this.connection.SendInternal(packer.getPackedData());
            }

            public byte[] processRecivedMessage(byte[] recivedMessage)
            {
                return this.processMessage(new UnPacker(recivedMessage));
            }
        }

        private class UnorderedProtocol : ProtocolManager
        {
            protected override int getHeaderLength()
            {
                return 1;
            }

            protected override void fixMessage(Packer packer, byte[] message)
            {
                packer.Pack((byte)Protocol.Unordered);
                packer.Pack(message);
            }

            protected override byte[] processMessage(UnPacker unPacker)
            {
                unPacker.UnpackByte(); //Remove id byte from message.
                return unPacker.UnpackByteArray(unPacker.remaining());
            }

            public UnorderedProtocol(Connection connection) : base(connection) 
            { }
        }

        private class OrderedProtocol : ProtocolManager
        {
            volatile int lastRecivedSequenceNumber;
            volatile int sendReciveSequenceNumber;

            public OrderedProtocol(Connection connection)
                : base(connection)
            { }

            protected override int getHeaderLength()
            {
                return 5;
            }

            protected override void fixMessage(Packer packer, byte[] message)
            {
                packer.Pack((byte)Protocol.Ordered);
                packer.Pack(sendReciveSequenceNumber++);
                packer.Pack(message);
            }

            protected override byte[] processMessage(UnPacker unPacker)
            {
                unPacker.UnpackByte();
                int seqNum = unPacker.UnpackInteger();
                if (seqNum < lastRecivedSequenceNumber)
                {
                    return null; //Discard the message if it is old. 
                }
                else
                {
                    lastRecivedSequenceNumber = seqNum;
                    return unPacker.UnpackByteArray(unPacker.remaining());
                }
            }
        }

        private class ReliableProtocol : ProtocolManager
        {
            private const int ACK_MESSAGE = 0x10;
            volatile int sendAccNum;


            protected override int getHeaderLength()
            {
                return 5;
            }

            protected override void fixMessage(Packer packer, byte[] message)
            {
                int toSendAccNum = this.sendAccNum++;
                packer.Pack((byte)Protocol.Reliable);
                packer.Pack(toSendAccNum);
                packer.Pack(message);

                MessageResendTimer timer = new MessageResendTimer();
                timer.accnumber = toSendAccNum;
                timer.message = packer.getPackedData();
                timer.stopWatch.Start();

                connection.reliableTimer.addResender(timer);
            }

            protected override byte[] processMessage(UnPacker unPacker)
            {
                int flags = unPacker.UnpackByte();
                int accnum = unPacker.UnpackInteger();

                if ((flags & ACK_MESSAGE) == ACK_MESSAGE)
                {
                    connection.reliableTimer.removeResender(accnum);
                    return null;
                }
                else
                {
                    sendAckMessage(accnum);
                    return unPacker.UnpackByteArray(unPacker.remaining());
                }
            }

            private void sendAckMessage(int accnum)
            {
                Packer packer = new Packer(5);
                packer.Pack((byte)(((int)Protocol.Reliable) | ACK_MESSAGE));
                packer.Pack(accnum);
                this.connection.SendInternal(packer.getPackedData());
            }

            public ReliableProtocol(Connection connection) : base(connection) { }
        }

    }
}