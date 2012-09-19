package spang.mobile;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import network.IConnection;
import network.INetworkListener;
import network.Network;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Xml.Encoding;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MainActivity extends Activity {
	private static final int PORT = 1337;
	private static final String ADDR = "192.168.33.221";
	private ArrayAdapter<String> adapter;
	private ArrayList<String> serverList;

	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        serverList = new ArrayList<String>();
        adapter = new ArrayAdapter<String>(this, R.id.listView1, serverList);
        
        Network.ListenToBroadcasts(9673, new INetworkListener() {
			
			public void listen(byte[] data) {
				try {
					serverList.add(new String(data,"UTF-8"));
					adapter.notifyDataSetChanged();
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    public void sendData(View view){
    	ListView listView = (ListView) this.findViewById(R.id.listView1);
    	String selected = (String) listView.getSelectedItem();
    	
    	Intent intent = new Intent(this, MouseActivity.class);
    	intent.putExtra("connection", selected);
    	this.startActivity(intent);
    }
}
