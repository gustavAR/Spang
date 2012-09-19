package spang.mobile;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    public void sendData(View view){
    	EditText text = (EditText)this.findViewById(R.id.editText1);
    	String ip = text.getText().toString();
    	
    	Intent intent = new Intent(this, MouseActivity.class);
    	intent.putExtra("connection", ip);
    	this.startActivity(intent);
    }
}
