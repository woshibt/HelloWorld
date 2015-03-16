package me.vable.android.helloworld

import android.annotation.TargetApi
import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.graphics.Point
import android.os.Build
import android.text.TextUtils
import android.view.Display
import android.view.WindowManager
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.VolleyLog
import com.android.volley.toolbox.Volley

class WeatherApp extends Application {

    String weatherData = ''

    SharedPreferences mPref;    //user preferences
    public static final String TAG = "AndroidRuntime"
    /**
     * Global request queue for Volley
     * Ideally you should have one centralized place for your Queue,
     * and the best place to initialize queue is in your Application class.
     */
    private RequestQueue mRequestQueue;

    private static WeatherApp instance; //A singleton instance of the application class for easy access in other places
    private static Context sContext;    //Keeps a reference of the application context, in case "not inside an Activity but need the context"
    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        instance.initializeInstance()   // initialize the singleton
    }

    @Override
    public void onTerminate() {
        // Do your application wise Termination task
        super.onTerminate();
    }
    /**
     * @return WeatherApp singleton instance
     */
    public static synchronized WeatherApp getInstance() {
        return instance;
    }

    /**
     * do your app’s version wise specialized task
     * or may be something that you need to be done before the creation of your first activity
     * e.g. screenConfiguration() method to determine is a  Tab ? Phone?
     */

    private void initializeInstance() {
        // initiate the context
        sContext = getApplicationContext();
        // set application wise preference
        mPref = this.getApplicationContext().getSharedPreferences("pref_key", MODE_PRIVATE);

        // Do your application wise initialization task
        screenConfiguration();
    }
    /*
     * use " WeatherApplication.sContext " anywhere to get it,   even not in an activity
     */
    public static Context getContext() {
        return sContext;
    }
    /**
     *  determining the size of ScreenWidth and ScreenHeight
     * and it also helpful if you like to know the device is a [ tab or phone ]
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)       //最后一个参数prevents the compilation error where minSdkVersion < 13
    public void screenConfiguration() {
        Configuration config = getResources().getConfiguration();
        boolean isTab = (config.screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;


        Point size = new Point();
        WindowManager wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();

        int deviceScreenWidth;
        int deviceScreenHeight;

        try {
            display.getSize(size);
            deviceScreenWidth = size.x;
            deviceScreenHeight = size.y;
        } catch (NoSuchMethodError e) {
            deviceScreenWidth = display.getWidth();
            deviceScreenHeight = display.getHeight();
        }
    }

    /**
     *   isFirstRun()    if the app runs for the first time. suppose,
     *   you get to show the user the EULA (End User License Agreement) page for the first time.
     */
    public boolean isFirstRun() {
        return mPref.getBoolean("is_first_run", true);  // return true if the app is running for the first time
    }
    /**
     *  setRunned()   after firstRun , set a flag
     */
    public void setRunned() {
        // after a successful run, call this method to set first run false
        SharedPreferences.Editor edit = mPref.edit();
        edit.putBoolean("is_first_run", false);
        edit.commit();
    }

    /**
     * @return The Volley Request queue, the queue will be created if it is null
     */
    public RequestQueue getRequestQueue() {
        // lazy initialize the request queue, the queue instance will be
        // created when it is accessed for the first time
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }
        return mRequestQueue;
    }

    /**
     * Adds the specified request to the global queue, if tag is specified
     * then it is used else Default TAG is used.
     *
     * @param req
     * @param tag ------- to differentiate our customized queued request
     */
    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);

        VolleyLog.d("Adding request to queue: %s", req.getUrl());

        getRequestQueue().add(req);
    }

    /**
     * Adds the specified request to the global queue using the Default TAG.
     *
     * @param req
     * @param tag
     */
    public <T> void addToRequestQueue(Request<T> req) {
        // set the default tag if tag is empty
        req.setTag(TAG);

        getRequestQueue().add(req);
    }

    /**
     * Cancels all pending requests by the specified TAG, it is important
     * to specify a TAG so that the pending/ongoing requests can be cancelled.
     *
     * @param tag
     */
    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }

    /**
     * To Simplify Toast (float msg) defination
     * @param msg
     */

    public static void show(String msg){
        Toast.makeText(getInstance(), msg, Toast.LENGTH_SHORT).show();
    }

    public static void showL(String msg){
        Toast.makeText(getInstance(), msg, Toast.LENGTH_LONG).show();
    }

    public static void show(int msg){
        Toast.makeText(getInstance(), msg, Toast.LENGTH_SHORT).show();
    }

    public static void showL(int msg){
        Toast.makeText(getInstance(), msg, Toast.LENGTH_LONG).show();
    }
}