/*  Copyright 2012 Joakim Johansson, Lukas Kurtyan, Gustav Alm Rosenblad and Pontus Pall
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
package network;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

import logging.Logger;


import network.exceptions.InvalidEndpointException;
import network.exceptions.NetworkException;
import serialization.ISerializer;
import serialization.SerializeManager;
import spang.events.Action1;
import spang.events.EventHandler;
import spang.events.EventHandlerDelegate;
import utils.Packer;
import utils.UnPacker;

public class Client implements IClient {

	//Default interval in which to send heart-beat callback to a server.
	private static final int DEF_HEARTBEAT_INTERVAL = 1000;

	//Default time it takes for the connection to time out.
	private static final int DEF_TIMEOUT = 5000;

	//Last connection connected to or null if we never connected.
	private InetSocketAddress lastConnectedAddress;

	//Connector used to connect with.
	private final IConnector connector;

	//The connection used to send and receive messages.
	private IConnection connection;

	//Worker used to receive udp messages asynchronously.
	private UdpWorker udpWorker;	

	//Event handler that raises the connected event.
	private EventHandlerDelegate<IClient, Boolean> connectionEvent;

	//Event handler that raises the received event.
	private EventHandlerDelegate<IClient, Object> recivedEvent;

	//Event handler that raises the disconnected event.
	private EventHandlerDelegate<IClient, DCCause> disconnectedEvent;

	//Stores the connectionTimeout.
	private int connectionTimeout;

	//Stores the interval that heart-beats will be sent in.
	private int heartBeatInterval;

	//Stores the time the last message was sent.
	private long lastMessageSent;

	//Serializes messages to byte arrays.
	private final SerializeManager serializeManager;


	/**
	 * Creates a new client.
	 */
	public Client(IConnector connector, SerializeManager serializeManager) {
		this.connector = connector;
		this.serializeManager = serializeManager;
		this.connectionEvent = new EventHandlerDelegate<IClient, Boolean>();
		this.recivedEvent = new EventHandlerDelegate<IClient, Object>();
		this.disconnectedEvent = new EventHandlerDelegate<IClient, DCCause>();
		this.connectionTimeout = DEF_TIMEOUT;
		this.heartBeatInterval = DEF_HEARTBEAT_INTERVAL;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isConnected() {
		return this.connection != null &&
				this.connection.isConnected();
	}


	/**
	 * {@inheritDoc}
	 */
	public void registerSerializer(ISerializer serializer) {
		this.serializeManager.registerSerilizer(serializer);
	}	

	/**
	 * {@inheritDoc}
	 */
	public int getConnectionTimeout() {
		return this.connectionTimeout;
	}


	/**
	 * {@inheritDoc}
	 */
	public void setConnectionTimeout(int value) {
		this.connectionTimeout = value;
		if(this.connection != null)
			this.connection.setTimeout(value);

	}

	public int getHearthBeatInterval() {
		return this.heartBeatInterval;		
	}

	public void setHeartBeatInterval(int value) {
		this.heartBeatInterval = value;
	}

	/**
	 * {@inheritDoc}
	 */
	public void connect(InetSocketAddress address) {
		this.connect(address, this.connectionTimeout, false);
	}

	/**
	 * {@inheritDoc}
	 */
	public void connect(InetAddress address, int port) {
		this.connect(new InetSocketAddress(address, port));
	}

	/**
	 * {@inheritDoc}
	 */
	public void connect(String host, int port) {
		try {
			InetAddress address = InetAddress.getByName(host);
			this.connect(address, port);
		} catch (UnknownHostException e) {
			throw new InvalidEndpointException("Invalid host name");			
		} catch (IllegalArgumentException e) {
			throw new InvalidEndpointException("Invalid port (range is 1-0xFFFE");
		}
	}

	private void connect(InetSocketAddress address, int timeout, boolean reconnecting) {
		//If we are connected it makes no since to connect again.
		if(this.connection != null)
			throw new NetworkException("We are already connected. Can't connect to a new connection.");

		this.connection = this.connector.connect(address, timeout);
		this.lastConnectedAddress = address;
		this.onConnected(reconnecting);
	}

	private void onConnected(boolean reconnecting) {
		this.connection.setTimeout(this.connectionTimeout);				
		this.connectionEvent.invoke(this, reconnecting);	

		//Starts a receiver thread to start receiving new messages.
		this.startReciving();
	}

	/**
	 * {@inheritDoc}
	 */
	public void reconnect(int retries, int timeout) {
		if(this.lastConnectedAddress == null)
			throw new IllegalArgumentException("Never been connected to anything so cannot reconnect");
		else if (this.connection != null)
			return;
		else
			this.reconnectInternal(retries, timeout, this.lastConnectedAddress);
	}

	private void reconnectInternal(int retries, int timeout, InetSocketAddress endpoint) {
		for (int i = 0; i < retries; i++) {
			try {
				this.connect(endpoint, timeout, true);
				return;
			} catch(NetworkException e) {
				Logger.logInfo("Failed to reconnect " + i + " retrying...");
			}		
		}

		//If we failed to reconnect
		if(this.connection == null)
			throw new NetworkException("Failed to reconnect!");

	}

	/**
	 * {@inheritDoc}
	 */
	public void disconnect() {
		if(this.connection == null) {
			return;
		}

		this.onDisconnect(DCCause.LocalShutdown);
	}

	private void onDisconnect(DCCause cause) {
		this.stopReciving();
		this.close();
		this.connection = null;

		this.disconnectedEvent.invoke(this, cause);
	}

	private void startReciving() {
		this.udpWorker = new UdpWorker(this.connection);

		this.udpWorker.addRecivedAction(new Action1<byte[]>() {

			public void onAction(byte[] obj) {
				onRecived(obj);
			}
		});

		this.udpWorker.addReciveFailedListener(new Action1<DCCause>() {
			public void onAction(DCCause obj) {
				onDisconnect(obj);
			}
		});

		new Thread(udpWorker).start();
	}

	private void stopReciving() {
		if(this.udpWorker == null)
			return;

		this.udpWorker.clearEventListeners();
		this.udpWorker.StopWorking();

		this.udpWorker = null;
	}

	private void onRecived(byte[] message) {
		if(isHeartbeat(message)) { 
			if(shouldSendHeartbeatCallback())
				this.sendHeartBeatResponse();
		} else {
			UnPacker unpacker = new UnPacker(message);
			while(unpacker.remaining() > 0) {
				Object deserializedMessage = this.serializeManager.deserialize(unpacker);
				this.recivedEvent.invoke(this, deserializedMessage);	
			}
		}	
	}

	private boolean shouldSendHeartbeatCallback() {
		return System.currentTimeMillis() > this.lastMessageSent + this.heartBeatInterval;
	}

	private void sendHeartBeatResponse() {
		//Since it is not important that every hearthbeat  makes it we 
		//may send it using the unordered fast protocol.
		this.connection.send(new byte[0], Protocol.Unordered);
	}

	private boolean isHeartbeat(byte[] message) {
		return message.length == 0;
	}



	/**
	 * {@inheritDoc}
	 */
	public void send(Object toSend) {
		this.send(toSend, Protocol.Unordered);
	}

	/**
	 * {@inheritDoc}
	 */
	public void send(Object toSend, Protocol protocol) {
		try {
			byte[] serializedMessage = this.serializeMessage(toSend);				
			this.connection.send(serializedMessage, protocol);
			this.lastMessageSent = System.currentTimeMillis();
		} catch(NetworkException e) {
			Logger.logException(e);
			this.onDisconnect(DCCause.LocalNetworkCrash);
		}
	}

	private byte[] serializeMessage(Object toSend) {
		Packer packer = new Packer();
		this.serializeManager.serialize(packer, toSend);
		return packer.getPackedData();		
	}

	/**
	 * {@inheritDoc}
	 */
	public void addConnectedListener(EventHandler<IClient, Boolean> listener) {
		this.connectionEvent.addListener(listener);
	}

	/**
	 * {@inheritDoc}
	 */
	public void removeConnectedListener(EventHandler<IClient, Boolean> listener) {
		this.connectionEvent.removeListener(listener);
	}

	/**
	 * {@inheritDoc}
	 */
	public void addDisconnectedListener(
			EventHandler<IClient, DCCause> listener) {
		this.disconnectedEvent.addListener(listener);
	}

	/**
	 * {@inheritDoc}
	 */
	public void removeDisconnectedListener(
			EventHandler<IClient, DCCause> listener) {
		this.disconnectedEvent.removeListener(listener);
	}

	/**
	 * {@inheritDoc}
	 */
	public void addRevicedListener(EventHandler<IClient, Object> listener) {
		this.recivedEvent.addListener(listener);
	}

	/**
	 * {@inheritDoc}
	 */
	public void removeRevicedListener(EventHandler<IClient, Object> listener) {
		this.recivedEvent.removeListener(listener);
	}

	/**
	 * {@inheritDoc}
	 */
	public void close() {
		this.connection.close();
	}
}