package spang.mobile;

import network.IConnection;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

public class NetworkServiceAdapter implements  ServiceConnection {
	private NetworkService serivce;
	private boolean isBound;
			
	public void bindService(Context context) {
		if(!isBound) {		
			Intent intent = new Intent(context, NetworkService.class);			
			context.bindService(intent, this, Context.BIND_AUTO_CREATE);
		}
	}
	
	public void unbindService(Context context) {
		if(this.isBound) {
			context.unbindService(this);			
		}
	}
	
	public void onServiceConnected(ComponentName name, IBinder service) {
		NetworkService.NetworkBinder binder = (NetworkService.NetworkBinder)service;
		this.serivce = binder.getService();		
		
		this.isBound = true;
	}
	public void onServiceDisconnected(ComponentName name) {
		this.isBound = false;		
	}	
	
	
	/**
	 *@see NetowrkSerive.connectTo(String,int)
	 */
	public void connectTo(String host, int port) {
		if(this.isBound) {
			this.serivce.connectTo(host, port);
		}
	}
	
	/**
	 * @see NetworkService.sendUDP(byte[])
	 */
	public void sendUDP(byte[] message) {
		if(this.isBound) {
			this.serivce.sendUDP(message);
		}
	}
	
	/**
	 * see NetworkService.sendTCP(byte[])
	 */
	public void sendTCP(byte[] message) {
		if(this.isBound) {
			this.serivce.sendTCP(message);
		}
	}

	public void startService(Context context) {
		Intent intent = new Intent(context, NetworkService.class);	
		context.startService(intent);
	}
	
	public void stopService(Context context) {
		Intent intent = new Intent(context, NetworkService.class);	
		context.stopService(intent);
	}

	public IConnection getConnection() {
		return this.serivce.getConnection();
	}

	public boolean isConnected() {
		return this.serivce.isConnected();
	}
}