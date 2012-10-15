package spang.mobile;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class ShortcutPrefsActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shortcut_prefs);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_shortcut_prefs, menu);
        return true;
    }
}
