package spang.mobile;

import network.exceptions.HostException;
import network.exceptions.NetworkException;
import utils.LogCatLogger;
import utils.Logger;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity {

	private Messenger serviceMessenger = null;
	private Messenger connectMessenger = new Messenger(new ServiceConnectHandler());
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Logger.setLogger(new LogCatLogger());
		if(Integer.parseInt(Build.VERSION.SDK) <= Build.VERSION_CODES.CUPCAKE)
			findViewById(R.id.button1).setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					sendData(v);
				}
			});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	public void sendData(View view){
		EditText text = (EditText)this.findViewById(R.id.editText1);
		String ip = text.getText().toString();

		EditText number = (EditText)this.findViewById(R.id.editText2);
		int port = Integer.parseInt(number.getText().toString());

		//				Intent intent = new Intent(this, MouseActivity.class);
		//				intent.putExtra("connection", ip);
		//				this.startActivity(intent);

		Intent intent = new Intent(this, NetworkService.class);
		intent.putExtra(NetworkService.CONNECTION_ADDRESS, ip);
		intent.putExtra(NetworkService.CONNECTION_PORT, port);
		bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
		
	}

	public void sendText(View view){
		EditText text = (EditText)this.findViewById(R.id.editText1);
		String ip = text.getText().toString();

		Intent intent = new Intent(this, TextSenderActivity.class);
		intent.putExtra("connection", ip);
		this.startActivity(intent);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
		case R.id.menu_settings: 
			Intent intent = new Intent(this, PrefsActivity.class);
			startActivity(intent);
		}
		return true;
	}
	private ServiceConnection serviceConnection = new ServiceConnection() {

		public void onServiceDisconnected(ComponentName name) {
			// TODO Auto-generated method stub

		}

		public void onServiceConnected(ComponentName name, IBinder service) {
			serviceMessenger = new Messenger(service);
			Message message = Message.obtain(null, NetworkService.CALLBACK_MESSAGE,
					this.hashCode());
			message.replyTo = MainActivity.this.connectMessenger;
			try {
				serviceMessenger.send(message);
			} catch (RemoteException e) {
				//IDK what to do here
			}
		}
	};

	/**
	 * Handler of incoming messages from service.
	 */
	class ServiceConnectHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case NetworkService.CONNECTION_FAIL_ADDRESS:
				Toast.makeText(MainActivity.this, "The address was invalid", Toast.LENGTH_SHORT).show();
				break;
			case NetworkService.CONNECTION_FAIL_TIMEOUT:
				Toast.makeText(MainActivity.this, "Connection timed out", Toast.LENGTH_SHORT).show();
				break;
			case NetworkService.CONNECTION_SUCCESS:
				Toast.makeText(MainActivity.this, "Connection successful", Toast.LENGTH_SHORT).show();
				break;
			default:
				super.handleMessage(msg);
			}
		}
	}
}
