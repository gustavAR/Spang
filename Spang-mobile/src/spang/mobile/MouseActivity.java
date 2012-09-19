package spang.mobile;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;

public class MouseActivity extends Activity {

	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new MouseView(this, null));
        
       
        
        System.out.println("YAAAY!");
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_mouse, menu);
        return true;
    }
    
  
    
}
