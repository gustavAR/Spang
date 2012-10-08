package spang.mobile;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

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
    	//Unbind the service so it can be closed later.
    	this.unbindService(connection);
    	super.onStop();
    }
    
    //Connection used to bind the network service.
    private ServiceConnection connection = new ServiceConnection() {
		
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
}
