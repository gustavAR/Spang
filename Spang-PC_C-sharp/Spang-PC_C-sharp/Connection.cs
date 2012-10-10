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
        private const int ACK_BIT = 0x10;
        private const int SHUTDOWN_BIT = 0x20;

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
            this.protocols.Add(Protocol.OrderedReliable, new OrderedReliableProtocol(this));
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
                    continue;

                if (IsShutdownMessage(recived))
                {
                    Console.WriteLine("The remote endpoint shutdown the connection!");
                    //10053 symbolizes a remote host shutdown.
                    throw new SocketException(10053);

                }

                Protocol protocol;
                if (protocolFromID((recived[0]), out protocol))
                {
                    recived = this.protocols[protocol].processRecivedMessage(recived);
                    if (recived != null)
                        return recived;
                }
            }
        }

        private bool IsShutdownMessage(byte[] recived)
        {
            return recived.Length == 1 &&
                   (recived[0] & SHUTDOWN_BIT) == SHUTDOWN_BIT; 
        }

        private bool protocolFromID(int id, out Protocol prot)
        {
            foreach (var protocol in this.protocols.Keys)
            {
                if ((((int)protocol) & id) == (int)protocol)
                {
                    prot = protocol;
                    return true;
                }
            }
            prot = Protocol.Unordered;
            return false;
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
                    Thread.Sleep(10);
                    updateAndSend();
                }
                catch (Exception)
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
                this.connection.SendInternal(packer.GetPackedData());
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
            volatile int sendAccNum;
            volatile int lastRecivedMessageAckNum;
            private List<int> missingMessages = new List<int>();

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
                timer.message = packer.GetPackedData();
                timer.stopWatch.Start();

                connection.reliableTimer.addResender(timer);
            }

            protected override byte[] processMessage(UnPacker unPacker)
            {
                int flags = unPacker.UnpackByte();
                int accnum = unPacker.UnpackInteger();


                if ((flags & ACK_BIT) == ACK_BIT)
                {
                    connection.reliableTimer.removeResender(accnum);
                    return null;
                }
                else
                {
                    sendAckMessage(accnum);
                    return processRecivedMessage(unPacker, accnum);
                }
            }

            private byte[] processRecivedMessage(UnPacker unPacker, int accnum)
            {
                if (accnum > this.lastRecivedMessageAckNum)
                {
                    addMissingMessages(accnum);
                    this.lastRecivedMessageAckNum = accnum;

                    return unPacker.UnpackByteArray(unPacker.remaining());
                }
                else
                {
                    if (this.missingMessages.Contains(accnum))
                    {
                        this.missingMessages.Remove(accnum);
                        return unPacker.UnpackByteArray(unPacker.remaining());
                    }

                    return null;
                }
            }

            private void addMissingMessages(int accnum)
            {
                for (int i = this.lastRecivedMessageAckNum; i < this.lastRecivedMessageAckNum - accnum; i++)
                {
                    Console.WriteLine("Adding missing messages!");
                    this.missingMessages.Add(i);
                }
            }

            private void sendAckMessage(int accnum)
            {
                Packer packer = new Packer(5);
                packer.Pack((byte)(((int)Protocol.Reliable) | ACK_BIT));
                packer.Pack(accnum);
                this.connection.SendInternal(packer.GetPackedData());
            }

            public ReliableProtocol(Connection connection) : base(connection) { }
        }

        private class OrderedReliableProtocol : ProtocolManager
        {
            volatile int sendAccNum;
            volatile int lastOrderedMessage = -1;
            private SortedList<int, byte[]> outOfOrderMessages = new SortedList<int, byte[]>();

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
                timer.message = packer.GetPackedData();
                timer.stopWatch.Start();

                connection.reliableTimer.addResender(timer);
            }

            protected override byte[] processMessage(UnPacker unPacker)
            {
                int flags = unPacker.UnpackByte();
                int accnum = unPacker.UnpackInteger();

                if ((flags & ACK_BIT) == ACK_BIT)
                {
                    connection.reliableTimer.removeResender(accnum);
                    return null;
                }
                else
                {
                    sendAckMessage(accnum);
                    return processRecivedMessage(unPacker, accnum);
                }
            }

            private byte[] processRecivedMessage(UnPacker unPacker, int accnum)
            {
                if (this.lastOrderedMessage + 1 == accnum)
                {
                    this.lastOrderedMessage++;
                    if (this.outOfOrderMessages.Count == 0)
                        return unPacker.UnpackByteArray(unPacker.remaining());
                    
                    return this.buildOrderedMessages(unPacker);
                }
                else if (this.lastOrderedMessage >= accnum)
                {
                    return null;
                } 
                else
                {
                    if (!this.outOfOrderMessages.ContainsKey(accnum))
                    {
                        Console.WriteLine("Message out of order! {0}", accnum);
                        this.outOfOrderMessages.Add(accnum, unPacker.UnpackByteArray(unPacker.remaining()));
                    }
                    return null;
                }
            }

            private byte[] buildOrderedMessages(UnPacker unPacker)
            {
                List<int> keysToRemove = new List<int>();
                Packer packer = new Packer(unPacker.remaining());
                packer.Pack(unPacker.UnpackByteArray(unPacker.remaining()));
  
                foreach (var item in this.outOfOrderMessages.Keys)
                {
                    if (this.lastOrderedMessage + 1 == item)
                    {
                        keysToRemove.Add(item);
                        this.lastOrderedMessage++;
                        packer.Pack(this.outOfOrderMessages[item]);
                    }
                    else
                    {
                        break;
                    }
                }

                keysToRemove.ForEach((x) => this.outOfOrderMessages.Remove(x));

                return packer.GetPackedData();
            }

            private void sendAckMessage(int accnum)
            {
                Packer packer = new Packer(5);
                packer.Pack((byte)(((int)Protocol.OrderedReliable) | ACK_BIT));
                packer.Pack(accnum);
                this.connection.SendInternal(packer.GetPackedData());
            }

            
            public OrderedReliableProtocol(Connection connection) : base(connection) { }
        }

    }
}