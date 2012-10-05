package spang.mobile;

import utils.LogCatLogger;
import utils.Logger;
import android.app.Activity;
import android.content.Intent;
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

		
		Intent intent2 = new Intent(MainActivity.this, SensorProcessor.class);
		MainActivity.this.startService(intent2);	
		
		Intent intent = new Intent(this, NetworkedActivity.class);
		this.startActivity(intent);
	}
	
	
		
	@Override 
	protected void onDestroy() {
		super.onDestroy();
		
		Intent intent = new Intent(this, NetworkService.class);
		this.stopService(intent);
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
}
