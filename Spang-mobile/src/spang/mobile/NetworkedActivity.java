package spang.mobile;

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
<<<<<<< HEAD

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
=======
import android.widget.Toast;

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
>>>>>>> origin/master

    @Override
    protected void onStart() {
     //Bind the service so we can use it.
     Intent intent = new Intent(this, NetworkService.class);
     this.bindService(intent, connection, Context.BIND_WAIVE_PRIORITY);	
     super.onStart();
    }
    
    @Override
    protected void onStop() {
     //Unbind the service so it can be closed later.
     this.unbindService(connection);
     super.onStop();
    }
    
    //Connection used to bind the network service.
    private ServiceConnection connection = new ServiceConnection() {
<<<<<<< HEAD

public void onServiceDisconnected(ComponentName name) {
network = null;
NetworkedActivity.this.onNetworkServiceConnected();
}

public void onServiceConnected(ComponentName name, IBinder service) {
network = ((NetworkService.NetworkBinder)service).getService();
NetworkedActivity.this.onNetworkServiceConnected();
}
};

protected abstract void onNetworkServiceConnected();
protected abstract void onNetworkSerivceDissconnected();
=======
		
		public void onServiceDisconnected(ComponentName name) {
			network = null;
			NetworkedActivity.this.onNetworkServiceConnected();
		}
		
		public void onServiceConnected(ComponentName name, IBinder service) {
			network = ((NetworkService.NetworkBinder)service).getService();
			NetworkedActivity.this.onNetworkServiceConnected();
			
			//TODO implement multithreading problems. That is multithreading with the UI thread doing UI stuff.
			/*network.addConnectedListener(new EventHandler<IClient, Boolean>() {
				
				public void onAction(IClient sender, Boolean eventArgs) {
					NetworkedActivity.this.onConnected();
				}
			});
			network.addDisconnectedListener(new EventHandler<IClient, DCCause>() {
				public void onAction(IClient sender, DCCause eventArgs) {
					NetworkedActivity.this.onDisconnected(eventArgs);
				}
			});
			network.addRevicedListener(new EventHandler<IClient, byte[]>() {
				public void onAction(IClient sender, byte[] eventArgs) {
					NetworkedActivity.this.onMessageRecived(eventArgs);
				}
			});*/
			
			
		}
	};
	
	protected abstract void onNetworkServiceConnected();
	protected abstract void onNetworkSerivceDissconnected();
	protected abstract void onMessageRecived(byte[] message);
	
	
	protected void onConnected() {
		//Dunno what to do here atm :O
	}
	
	protected  void onDisconnected(DCCause cause) {		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("We disconnection! Cause: " + cause);
		builder.setTitle("Disconnected");
		
		builder.setPositiveButton("Reconnect?", new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				NetworkedActivity.this.network.reconnect(5, 1000);
				Toast.makeText(NetworkedActivity.this, "Trying to reconnect...", Toast.LENGTH_SHORT).show();
			}
		});
		
		builder.show();		
	}
	
	
>>>>>>> origin/master
}