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
package spang.mobile;

import logging.LogCatLogger;
import logging.Logger;
import spang.android.network.NetworkService;
import spang.events.Action1;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

/**
 * The entry point of the application.
 * 
 * @author Lukas Kurtyan & Gustav Alm Rosenblad & Joakim Johansson & Pontus Pall
 *
 */
public class MainActivity extends Activity {
	
	//A service that can connect over the network.
	private NetworkService service;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		//Starts a network service.
		Intent intent = new Intent(MainActivity.this, NetworkService.class);
		this.startService(intent);	
		
		//Makes the application log to logcat.  
		Logger.setLogger(new LogCatLogger());
	}
	

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;		
	}
	
	/*
	 * The connection used when binding and unbinding the networkservice. 
	 */
    private ServiceConnection connection = new ServiceConnection() {	
		public void onServiceDisconnected(ComponentName name) {
			service = null;
		}
		
		public void onServiceConnected(ComponentName name, IBinder service) {
			//Binds the service to the MainActivity.
			MainActivity.this.service = ((NetworkService.NetworkBinder)service).getService();
		}
	};
	

	/**
	 * {@inheritDoc}
	 */
	@Override 
	protected void onStart() {
		super.onStart();
		//Binds to the network service.
		Intent intent = new Intent(this, NetworkService.class);		
		this.bindService(intent, connection, Context.BIND_WAIVE_PRIORITY);	
	}
	

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onStop() {
		super.onStop();
		//Unbinds the network service.
		this.unbindService(connection);
	}


	/**
	 * {@inheritDoc}
	 */
	@Override 
	protected void onDestroy() {
		super.onDestroy();
		//Stops the network service.
		Intent intent = new Intent(this, NetworkService.class);
		this.stopService(intent);
	}
	

	public void connect(View view){

		//Gets the ip.
		EditText text = (EditText)this.findViewById(R.id.editText1);
		final String ip = text.getText().toString();

		//Gets the port.
		EditText number = (EditText)this.findViewById(R.id.editText2);
		final int port = Integer.parseInt(number.getText().toString());
		
		//Notify users that we are making a connection attempt.
		Toast.makeText(this, "Connecting...!", Toast.LENGTH_SHORT).show();
		
		//Connects the NetworkService to the remote endpoint specified by ip and port. 
		service.connectAsync(ip, port, new Action1<Boolean>() {	
			//Callback when the connection is complete.
			public void onAction(Boolean success) {
				if(success) {
					//Notify users of success
					Toast.makeText(MainActivity.this, "Connected!",Toast.LENGTH_SHORT).show();
					//Start computer activity.
					Intent intent = new Intent(MainActivity.this, ComputerActivity.class);
					MainActivity.this.startActivity(intent);				
				} else {
					//Notify users of failure
					Toast.makeText(MainActivity.this, "Failed to connect!",Toast.LENGTH_SHORT).show();							
				}
			}
		});			
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
		}
	}
}
