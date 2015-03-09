package me.vable.android.helloworld;
import android.app.Activity;
import android.os.Bundle
import com.arasthel.swissknife.SwissKnife
import groovy.transform.CompileStatic;

/**
 * @CompileStatic to improve performance of Groovy
 */
@CompileStatic
class MainActivity extends Activity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // This must be called for injection of views and callbacks to take place
        SwissKnife.inject(this);
        // This must be called for saved state restoring
        SwissKnife.restoreState(this, savedInstanceState);

    }
}
