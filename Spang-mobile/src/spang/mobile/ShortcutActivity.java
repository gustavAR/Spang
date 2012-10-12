package spang.mobile;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class ShortcutActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shortcut);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_shortcut, menu);
        return true;
    }
}
