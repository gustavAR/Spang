package spang.mobile;

import keyboard.KeyboardtestActivity;
import utils.LogCatLogger;
import utils.Logger;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Logger.setLogger(new LogCatLogger());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;		
	}

	public void sendData(View view){

		EditText text = (EditText)this.findViewById(R.id.editText1);
		final String ip = text.getText().toString();

		EditText number = (EditText)this.findViewById(R.id.editText2);
		final int port = Integer.parseInt(number.getText().toString());

		Thread thread = new Thread(new Runnable() {

			public void run() {	
				Intent intent = new Intent(MainActivity.this, NetworkService.class);
				intent.putExtra("HOST", ip);
				intent.putExtra("PORT", port);

				MainActivity.this.stopService(intent);
				MainActivity.this.startService(intent);				
			}
		});

		thread.start();

		Intent intent = new Intent(this, ComputerActivity.class);

		this.startActivity(intent);
	}



	@Override 
	protected void onDestroy() {
		super.onDestroy();

		Intent intent = new Intent(this, NetworkService.class);
		this.stopService(intent);
	}

	public void showKeyboard(View view){
		Intent intent = new Intent(this, KeyboardtestActivity.class);
		this.startActivity(intent);
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

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
		case R.id.menu_settings: 
			Intent intent = new Intent(this, PrefsActivity.class);
			startActivity(intent);
		}
		return true;
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
		// TODO Auto-generated method stub
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
