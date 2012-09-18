package spang.mobile;

import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends Activity {
	private static final int PORT = 1337;
	private static final String ADDR = "129.16.184.28";

	private Socket socket;
	private PrintWriter writer;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
     // connectToServer();
    }
    private void connectToServer() {
    	InetAddress addr;
		try {
			addr = InetAddress.getByName(ADDR);
	    	socket = new Socket(addr, PORT);
	    	writer = new PrintWriter(socket.getOutputStream());
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
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
    	try {
			DatagramPacket packet = new DatagramPacket(data, data.length,InetAddress.getByName(ADDR), PORT);
			new DatagramSocket().send(packet);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	
    	//writer.println(message);
    	//writer.flush();
    }
}
