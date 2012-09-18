package spang.mobile;

import java.net.InetAddress;
import java.net.UnknownHostException;

import network.Client;
import network.IConnection;
import network.NetworkException;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends Activity {
	private static final int PORT = 1337;
	private static final String ADDR = "192.168.33.102";
	private IConnection connection;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Client client = new Client();
       
        try {
			this.connection= client.connectTo(InetAddress.getByName(ADDR), PORT);
		} catch (UnknownHostException e) {
			throw new NetworkException(e);
		}
        
        new Thread(new Runnable() {
			
			public void run() {
				reading();
			}
		}).start();
    }
    public void reading(){
    	while(true){
    		String string = new String(this.connection.reciveUDP());
    		Log.i("Hej", string);
    	}
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    public void sendData(View view){
    	EditText text = (EditText)this.findViewById(R.id.editText1); 
    	String message = text.getText().toString();
    	byte[] data = message.getBytes();
    	connection.sendUDP(data);
    	
    	
    	//writer.println(message);
    	//writer.flush();
    }
    
    public void goToMouse() {
    	System.out.print("Klick!");
    }
}
