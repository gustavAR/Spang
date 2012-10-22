package network;
import logging.ILogger;
import logging.Logger;
import network.exceptions.NetworkException;
import network.exceptions.RemoteCrashException;
import network.exceptions.RemoteShutdownException;
import network.exceptions.TimeoutException;

import org.junit.Before;
import org.junit.Test;

import spang.events.Action1;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class UdpWorkerTests {

	UdpWorker worker;
	IConnection mockedConnection;
	
	
	@Before
	public void setup() {
		mockedConnection = mock(IConnection.class);
		worker = new UdpWorker(mockedConnection);
		Logger.setLogger(mock(ILogger.class));
	}
	
	@Test
	public void testIfRecivedEventIsTriggeredWhenDataIsRecived() throws InterruptedException {
		final boolean[] flag = {false}; //zzzz this java
		worker.addRecivedAction(new Action1<byte[]>() {
			public void onAction(byte[] obj) {
				flag[0] = true;	
			}
		});		
		when(mockedConnection.recive()).thenReturn(new byte[0]);

		//Start the worker on a diffrent thread as per the design.
		Thread thread = new Thread(worker);
		thread.start();
		
		//Sleep to give the workerthread some time do do work.
		Thread.sleep(100);
		
		assertTrue(flag[0]);
		
		//Make sure the thread exits.
		thread.interrupt();
	}
	
	@Test
	public void testIfConnectionTimeoutTriggersCorrectEvent() throws InterruptedException {
		this.testIfReadFailedInvokesWithCorrectDCCause(new TimeoutException(), 
												DCCause.Timeout);		
	}
	
	@Test
	public void testIfRemoteCrashTriggersCorrectEvent() throws InterruptedException {
		this.testIfReadFailedInvokesWithCorrectDCCause(new RemoteCrashException(), 
												DCCause.RemoteNetworkCrash);		
	}
	
	@Test
	public void testIfRemoteShutdownTriggersCorrectEvent() throws InterruptedException {
		this.testIfReadFailedInvokesWithCorrectDCCause(new RemoteShutdownException(), 
												DCCause.EndpointShutdown);		
	}
	
	@Test
	public void testIfNetworkExceptionTriggersCorrectEvent() throws InterruptedException {
		this.testIfReadFailedInvokesWithCorrectDCCause(new NetworkException(), 
				DCCause.LocalNetworkCrash);		
	}
	
	public void testIfReadFailedInvokesWithCorrectDCCause(NetworkException exception, final DCCause expected) 
			throws InterruptedException {
		final boolean[] flag = {false}; //zzzz this java
		worker.addReciveFailedListener(new Action1<DCCause>() {
			@Override
			public void onAction(DCCause obj) {
				if(obj == expected) {
					flag[0] = true;
				}
			}
		});
		
		when(this.mockedConnection.recive()).thenThrow(exception);
		
		//Start the worker on a diffrent thread as per the design.
		Thread thread = new Thread(worker);
		thread.start();
		
		//Sleep to give the workerthread some time do do work.
		Thread.sleep(100);
		
		assertTrue(flag[0]);
		
		//Make sure the thread exits.
		thread.interrupt();
	}
}