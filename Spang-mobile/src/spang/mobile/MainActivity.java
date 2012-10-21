package spang.mobile;

import spang.android.network.NetworkService;
import spang.events.Action1;
import utils.LogCatLogger;
import utils.Logger;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	private NetworkService service;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Intent intent = new Intent(MainActivity.this, NetworkService.class);
		this.startService(intent);	

		Logger.setLogger(new LogCatLogger());
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;		
	}
	
    private ServiceConnection connection = new ServiceConnection() {
		
		public void onServiceDisconnected(ComponentName name) {
			service = null;
		}
		
		public void onServiceConnected(ComponentName name, IBinder service) {
			MainActivity.this.service = ((NetworkService.NetworkBinder)service).getService();
		}
	};
	
	@Override 
	protected void onStart() {
		super.onStart();
		Intent intent = new Intent(this, NetworkService.class);		
		this.bindService(intent, connection, Context.BIND_WAIVE_PRIORITY);	
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		this.unbindService(connection);
	}

	@Override 
	protected void onDestroy() {
		super.onDestroy();

		Intent intent = new Intent(this, NetworkService.class);
		this.stopService(intent);
	}
	

	public void sendData(View view){

		EditText text = (EditText)this.findViewById(R.id.editText1);
		final String ip = text.getText().toString();

		EditText number = (EditText)this.findViewById(R.id.editText2);
		final int port = Integer.parseInt(number.getText().toString());
		
		//Notify users that we are making a connection attempt.
		Toast.makeText(this, "Connecting...!", Toast.LENGTH_SHORT).show();
		service.connectAsync(ip, port, new Action1<Boolean>() {
			
			public void onAction(Boolean success) {
				if(success) {
					//Notify users of success
					Toast.makeText(MainActivity.this, "Connected!",Toast.LENGTH_SHORT).show();	
					Intent intent = new Intent(MainActivity.this, ComputerActivity.class);
					MainActivity.this.startActivity(intent);				
				} else {
					//Notify users of failure
					Toast.makeText(MainActivity.this, "Failed to connect!",Toast.LENGTH_SHORT).show();							
				}
			}
		});			
	}
	
	public void captureScreen(View view){
		
		
	}

	/**
	 * This tells the phone to scan a QR-code with zxing
	 * and return the data to this activity.
	 * If zxing bar code scanner is not installed,
	 * the user will be directed to the proper site on
	 * the marketplace.
	 * @param view
	 */
	public void scanQRC(View view){
		try {
			Intent intent = new Intent(
					"com.google.zxing.client.android.SCAN");
			intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
			startActivityForResult(intent, 0);//This will throw an exception if zxing is not installed
		} catch (Exception e) {

			Uri marketUri = Uri
					.parse("market://details?id=com.google.zxing.client.android");
			Intent marketIntent = new Intent(Intent.ACTION_VIEW,
					marketUri);
			startActivity(marketIntent);
		}
	}	

	/**
	 * This is called when we get a result from another activity.
	 * When the data from the QR-code is returned. It is passed
	 * into the IP- and port-textfield.
	 * 
	 * This method assumes that it'll be passed a string
	 * consisting of the IP-adress and the port, separated by "/".
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 0) {
			if (resultCode == android.app.Activity.RESULT_OK) {
				String contents = data.getStringExtra("SCAN_RESULT");
				int slashIndex = contents.indexOf("/");
				if (slashIndex == -1){
					return;
				}
				String iPAdress = contents.substring(0, slashIndex);
				String portNumber = contents.substring(slashIndex + 1, contents.length());
				
				EditText iPField = (EditText)this.findViewById(R.id.editText1);
				iPField.setText(iPAdress);
				
				EditText portField = (EditText)this.findViewById(R.id.editText2);
				portField.setText(portNumber);
			}
			if(resultCode == android.app.Activity.RESULT_CANCELED){
				//handle cancel
			}
		}
	}
}
