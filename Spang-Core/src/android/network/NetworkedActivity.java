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
package android.network;

import network.DCCause;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.widget.Toast;
import events.Action;
import events.Action1;

/**
 * Class that interfaces against the Network Service.
 * @author Lukas Kuryan.
 *
 */
public abstract class NetworkedActivity extends Activity {

	private NetworkService network;	

	/**
	 * Gets the active NetworkService.
	 * NOTE: This will return null before onNetworkServiceConnected is called!
	 * @return a NetworkService.
	 */
	public NetworkService getNetworkService() {	
		return this.network;
	}

	@Override
	protected void onStart() {
		//Bind the service so we can use it.
		Intent intent = new Intent(this, NetworkService.class);
		this.bindService(intent, connection, Context.BIND_WAIVE_PRIORITY);	
		super.onStart();
	}

	@Override
	protected void onStop() {
		this.removeListeners();
		//Unbind the service so it can be closed later.
		this.unbindService(connection);
		super.onStop();
	}

	private void removeListeners() {
		if(network != null) {
			network.removeConnectedListener(networkConnection);
			network.removeDisconnectedListener(networkDisconnected);
			network.removeRevicedListener(networkRecived);
		}
	}

	private void addListeners() {
		network.addConnectedListener(networkConnection);
		network.addDisconnectedListener(networkDisconnected);
		network.addRevicedListener(networkRecived);	
	}

	private Action networkConnection = new Action() {

		public void onAction() {
			NetworkedActivity.this.onConnected();
		}
	};

	private Action1<Object> networkRecived = new Action1<Object>() {

		public void onAction(Object eventArgs) {
			NetworkedActivity.this.onMessageRecived(eventArgs);
		}
	};

	private Action1<DCCause> networkDisconnected = new Action1<DCCause>() {

		public void onAction(DCCause eventArgs) {
			NetworkedActivity.this.onDisconnected(eventArgs);
		}
	};

	//Connection used to bind the network service.
	private ServiceConnection connection = new ServiceConnection() {

		public void onServiceDisconnected(ComponentName name) {
			network = null;
			NetworkedActivity.this.onNetworkServiceConnected();
		}

		public void onServiceConnected(ComponentName name, IBinder service) {
			network = ((NetworkService.NetworkBinder)service).getService();
			NetworkedActivity.this.onNetworkServiceConnected();

			addListeners();
		}
	};

	protected abstract void onNetworkServiceConnected();
	protected abstract void onNetworkSerivceDissconnected();
	protected abstract void onMessageRecived(Object message);


	protected void onConnected() {
		//Dunno what to do here atm :O
	}

	protected void onDisconnected(DCCause cause) {	
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("We disconnected! Cause: " + cause);
		builder.setTitle("Disconnected");

		builder.setPositiveButton("Reconnect?", new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				NetworkedActivity.this.network.reconnectAsync(5, 1000, new Action1<Boolean>() {	
					public void onAction(Boolean success) {
						if(success) {
							Toast.makeText(NetworkedActivity.this, "Connected!", Toast.LENGTH_SHORT).show();
						} else {
							builder.setMessage("Failed to connect!");	
							builder.show();
						}
					}
				});

				Toast.makeText(NetworkedActivity.this, "Trying to reconnect...", Toast.LENGTH_SHORT).show();
			}
		});

		builder.show();	
	}


}