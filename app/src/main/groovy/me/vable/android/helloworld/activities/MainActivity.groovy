package me.vable.android.helloworld.activities

import android.app.Activity
import android.app.FragmentTransaction
import android.content.Intent
import android.os.Bundle
import com.arasthel.swissknife.SwissKnife
import com.arasthel.swissknife.annotations.OnClick
import me.vable.android.helloworld.Fragment_webImg
import me.vable.android.helloworld.MyVolley
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
        SwissKnife.restoreState(this, savedInstanceState);

        if(savedInstanceState == null) {    //避免重复创建fragment
            //imageLoader test--- 动态添加一个建好的image fragment
            Fragment_webImg frag = new Fragment_webImg()
            //错误写法     android.R.id.content 是当前activity的 root container
            //getFragmentManager().beginTransaction().add(android.R.id.content,frag,"testImage").commit()
            FragmentTransaction ft = getFragmentManager().beginTransaction()
            ft.add(R.id.jingContainer, frag, "testImage")
            ft.addToBackStack(null)     //支持 （手机）back 按钮回退
            ft.commit()
        }

/*
    //注释掉的是没用swissknife的传统写法
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

    /**
     * to load a realtime weather data and display in a textview in SyncResult.groovy
     * -------weatherData is a test of shareing variable in Application, meaningless
     */

    @OnClick(R.id.weatherBtn)
    def onBtnClicked(){          //any function name works
        application.weatherData = "Touch me,   I will show you some weather"
        def intent = new Intent(this,SyncResult)
        startActivity(intent)
    }

    /**
     * load a web image to the NetworkImageView
     */
    @OnClick(R.id.imgBtn)
    def toLoadAnImage(){
        /*
            //下面这一大堆被封装进MyVolley了

        NetworkImageView imgView = (NetworkImageView)findViewById(R.id.theWebImg)
        Log.i("AndroidRuntime",imgView.toString())

        imgView.setDefaultImageResId(R.drawable.default_image);
        imgView.setErrorImageResId(R.drawable.failed_image);
        imgView.setImageUrl("http://img.my.csdn.net/uploads/201404/13/1397393290_5765.jpeg",
                MyVolley.getImageLoader());
        */
        MyVolley.loadImg(R.id.theWebImg,"http://img.my.csdn.net/uploads/201404/13/1397393290_5765.jpeg",this)

    }

}
