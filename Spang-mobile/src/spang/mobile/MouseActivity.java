package spang.mobile;

import network.NotImplementedException;
import android.app.Activity;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;

public class MouseActivity extends Activity {

	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        MouseView mView = new MouseView(this, null, intent.getStringExtra("connection"));
       
        setContentView(mView);
        mView.setFocusableInTouchMode(true);
        if(!mView.requestFocus())
        	throw new NotImplementedException();
          
       Log.i("Hej", "YAAAY!");
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_mouse, menu);
        return true;
    }
    
  
    
}
