package me.vable.android.helloworld.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.arasthel.swissknife.SwissKnife
import com.arasthel.swissknife.annotations.OnClick
import me.vable.android.helloworld.R

//import groovy.transform.CompileStatic

/**
 * @CompileStatic to improve performance of Groovy
 */
//@CompileStatic
class MainActivity extends Activity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // This must be called for injection of views and callbacks to take place
        SwissKnife.inject(this);

        if(application.isFirstRun()){
            application.show("first time run, great!")
        }


        // This must be called for saved state restoring
        //SwissKnife.restoreState(this, savedInstanceState);


/*
    //注释掉的是没用swissknife的写法
        def btn = findViewById(R.id.weatherBtn)
        btn.onClickListener = {
            application.weatherData = "haha"
            getFuckWeatherData()
        }
*/
    }

    /*
    def getFuckWeatherData() {
        def intent = new Intent(this,SyncResult)

        startActivity(intent);
    }
    */
    @OnClick(R.id.weatherBtn)
    def onBtnClicked(){          //any function name works
        application.weatherData = "Touch me,   I will show you some weather"
        def intent = new Intent(this,SyncResult)
        startActivity(intent)
    }

}
