package spang.mobile;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Menu;

/**
 * Class that interfaces against the Network Service.
 * @author Lukas Kuryan.
 *
 */
public class NetworkedActivity extends Activity {
	
	private NetworkService network;	

	/**
	 * Gets the active NetworkService.
	 * @return a NetworkService.
	 */
	public NetworkService getNetworkService() {
		return this.network;
	}
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_networked);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_networked, menu);
        return true;
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
		}
		
		public void onServiceConnected(ComponentName name, IBinder service) {
			network = ((NetworkService.NetworkBinder)service).getService();
					
			//TODO remove testing code!.
			NetworkView view = new NetworkView(NetworkedActivity.this, network);
			setContentView(view);
			view.setFocusableInTouchMode(true);
			view.requestFocus();
		}
	};
    
}
