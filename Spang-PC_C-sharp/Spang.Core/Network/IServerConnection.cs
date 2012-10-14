﻿/*  Copyright 2012 Joakim Johansson, Lukas Kurtyan, Gustav Alm Rosenblad and Pontus Pall
 *  
 *  This file is part of Spang.

    Spang is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Spang is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Spang.  If not, see <http://www.gnu.org/licenses/>.
 */
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading;

namespace Spang.Core.Network
{
    interface IServerConnection : IConnection
    {
        /// <summary>
        /// The id of the connection.
        /// </summary>
        int ID { get; }

        void StartAsyncRecive();
        void StopAsyncRecive();
    }

    class ServerConnection : IServerConnection
    {
        private readonly IConnection connection;
        private readonly Server server;
        private UdpWorker uworker;
        

        public ServerConnection(IConnection connection, Server server, int id)
        {
            this.server = server;
            this.connection = connection;
            this.ID = id;
        }

        public int ID
        {
            get;
            private set;
        }

        public void StartAsyncRecive()
        {
            if (uworker != null)
                return; //We are already receiving.

            //New worker objects are created and started.

            this.uworker = new UdpWorker(this);
            uworker.Recive += this.OnRecived;
            uworker.Timeout += this.OnTimeout;

            new Thread(uworker.DoWork).Start();
        }

        public void StopAsyncRecive()
        {
            if (this.uworker != null)
            {
                //Stops the worker threads.
                this.uworker.Recive -= this.OnRecived;
                this.uworker.Timeout -= this.OnTimeout;
                this.uworker.StopWorking();
                this.uworker = null;
            }
        }

        private void OnRecived(byte[] bytes)
        {
            server.OnRecived(this.ID, bytes);
        }

        private void OnTimeout()
        {
            server.OnDissconnected(this, DisconnectCause.Unexpected);
        }

        #region IConnection Delegation

        public void Close()
        {
            this.connection.Close();
        }

        public void Send(byte[] data)
        {
            this.connection.Send(data);
        }

        public void Send(byte[] data, Protocol protocol)
        {
            this.connection.Send(data, protocol);
        }

        public byte[] Receive()
        {
            return this.connection.Receive();
        }

        public bool Connected
        {
            get { return this.connection.Connected; }
        }

        public System.Net.IPEndPoint RemoteEndPoint
        {
            get { return this.connection.RemoteEndPoint; }
        }

        public System.Net.IPEndPoint LocalEndPoint
        {
            get { return this.connection.LocalEndPoint;  }
        }

        public int ReciveTimeout
        {
            get { return this.connection.ReciveTimeout; }
            set { this.connection.ReciveTimeout = value; }
        }

        public int SendTimeout
        {
            get { return this.connection.SendTimeout; }
            set { this.connection.SendTimeout = value; }
        }

        #endregion
    }
}