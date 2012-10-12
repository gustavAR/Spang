package spang.mobile.achtung;

import com.example.achtung.R;
import com.example.achtung.R.layout;
import com.example.achtung.R.menu;

import android.network.NetworkedActivity;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import android.support.v4.app.NavUtils;

public class AchtungActivity extends NetworkedActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_achtung);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_achtung, menu);
        return true;
    }

    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    public void onReadyClicked(View view){
    	if(this.getNetworkService() != null) {
    		String message = this.getMessage();
    		if(message != null) {
    			this.getNetworkService().send(message);
    		}
    	}
    }

	private String getMessage() {
		EditText text = (EditText)findViewById(R.id.editText1);
		String name = text.getText().toString();
		
		if(name == "") return name;
		else return "READY:" + name;
	}

	@Override
	protected void onMessageRecived(Object arg0) {
		if(arg0 instanceof String) {
			String response = (String)arg0;
			if(response == "OK_NAME") {
				Toast.makeText(this, "Name ok!", Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(this, "Invalid name choose another!", Toast.LENGTH_SHORT).show();
			}
		}
	}

	@Override
	protected void onNetworkSerivceDissconnected() {
		//Somethign do cna be fine lol bur bur kek.
		
	}

	@Override
	protected void onNetworkServiceConnected() {
		String message = this.getMessage();
		//Used on reconnects! :O
		if(message != "") {
			this.getNetworkService().send(message);					
		}
	}

}
