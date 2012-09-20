package spang.mobile;

import java.net.InetAddress;
import java.net.UnknownHostException;

import network.Client;
import network.IConnection;
import network.NetworkException;
import network.NotImplementedException;
import android.app.Activity;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;

public class MouseActivity extends Activity {

	private static final int PORT = 1337;
	private String adress;
	private IConnection connection;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();

        this.adress = intent.getStringExtra("connection");
        
		Client client = new Client();

		try {
			this.connection= client.connectTo(InetAddress.getByName(adress), PORT);
		} catch (UnknownHostException e) {
			throw new NetworkException(e);
		}
        
        MouseView mView = new MouseView(this, null, this.connection);
       
        setContentView(mView);
        mView.setFocusableInTouchMode(true);
        if(!mView.requestFocus())
        	throw new NotImplementedException();
          
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_mouse, menu);
        return true;
    }
    
  
    
}
