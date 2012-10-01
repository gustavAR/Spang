package spang.mobile;

import network.IConnection;
import network.exceptions.NotImplementedException;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;

import network.IConnection;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

public class MouseActivity extends Activity {
	private IConnection connection;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_mouse, menu);
		return true;
	}

	@Override 
	protected void onStart() {
		Intent intent = new Intent(this, NetworkService.class);
		this.bindService(intent, sconnection, Context.BIND_AUTO_CREATE);
		super.onStart();
	}
	
	@Override 
	protected void onStop() {
		Intent intent = new Intent(this, NetworkService.class);
		this.unbindService(sconnection);
		super.onStop();
	}
	
	
	private ServiceConnection sconnection = new ServiceConnection() {
		
		public void onServiceDisconnected(ComponentName name) {
			
		}
		
		public void onServiceConnected(ComponentName name, IBinder service) {
			
			NetworkService mservice = ((NetworkService.NetworkBinder)service).getService();
			
			MouseActivity.this.connection = mservice.getConnection();
			MouseView mView = new MouseView(MouseActivity.this, null, connection);
			setContentView(mView);
			mView.setFocusableInTouchMode(true);
			if(!mView.requestFocus())
				throw new NotImplementedException();
			
		}
	};


}
