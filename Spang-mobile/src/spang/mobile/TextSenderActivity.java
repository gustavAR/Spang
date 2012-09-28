package spang.mobile;

import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import network.Client;
import network.IConnection;
import network.exceptions.NetworkException;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;

public class TextSenderActivity extends Activity {


	private static final int PORT = 1337;
	private String adress;
	private IConnection connection;
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_sender);
        
        Intent intent = getIntent();
        this.adress = intent.getStringExtra("connection");

		Client client = new Client();
		try {
			client.connect(InetAddress.getByName(adress), PORT);
			this.connection= client.getConnection();
		} catch (UnknownHostException e) {
			throw new NetworkException(e);
		}
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_text_sender, menu);
        return true;
    }
    
    public void sendText(View view) {
    	EditText text = (EditText)this.findViewById(R.id.editText1);
    	String toSend = text.getText().toString();
    	
    	ByteBuffer buffer = ByteBuffer.allocate(toSend.length() + 5).order(ByteOrder.LITTLE_ENDIAN);
    	buffer.put((byte)11);
    	
    	buffer.putInt(toSend.length());
    	try {
			buffer.put(toSend.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	this.connection.sendUDP(buffer.array());
    }
    
}
