package spang.mobile;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends Activity {
	private static final int PORT = 1337;
	private static final String ADDR = "192.168.33.221";

	private Socket socket;
	private PrintWriter writer;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
          	
        connectToServer();

    	byte[] data = new byte[1000];
		DatagramPacket packet;
		try {
			packet = new DatagramPacket(data, socket.getLocalPort());
			new DatagramSocket().receive(packet);
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String str = new String(data);
		
    }
    
    public String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface
                    .getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf
                        .getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (Exception ex) {
        	throw new RuntimeException();
        }
        return "";
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
