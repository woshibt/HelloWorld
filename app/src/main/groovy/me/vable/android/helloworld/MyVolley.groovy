package me.vable.android.helloworld

import android.app.Activity
import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.text.TextUtils
import android.util.Log
import com.android.volley.*
import com.android.volley.toolbox.*
import groovy.json.JsonSlurper
import me.vable.android.helloworld.models.BitmapLruCache
import me.vable.android.helloworld.models.PersistentCookieStore
import org.apache.http.cookie.Cookie
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.impl.client.HttpClients
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager
import org.json.JSONException
import org.json.JSONObject

import java.util.concurrent.ConcurrentHashMap

/**
 * A class with clean APIs to write RESTful HTTP requests,
 * A customized "static" class "with NO instance" to handle the singleton [queue] & the singleton [imageLoader] in the whole Application
 * ---Author: Jing
 * Referecne:
 *    Variable init:
 *      https://github.com/ogrebgr/android_volley_examples/blob/master/src/com/github/volley_examples/app/MyVolley.java
 *    JsonRequest:
 *      https://github.com/ogrebgr/android_volley_examples/blob/master/src/com/github/volley_examples/Act_JsonRequest.java
 *    Header / Cookie / error:
 *      http://arnab.ch/blog/2013/08/asynchronous-http-requests-in-android-using-volley/
 *
 *
 * [API samples]
 *  1. Add a standard request to the Volley queue to interact with server
 *      MyVolley.addToRequestQueue(Request<T> req)              //any type of request works
 *
 *  2. Add a "tagged" request to the Volley queue to interact with server
 *      MyVolley.addToRequestQueue(Request<T> req, String tag)  //any type of request works, tag as you wish
 *
 *  3. cancel all ongoing/pending requests
 *          //one reason when you need to do this is if user rotates his device while a request is ongoing
 *              you need to cancel that because the Activity is going be restarted.
 *      MyVolley.cancelPendingRequests(Object tag)              //tag string,
 *                                                              //if null, dangerous !!!! cancel all "MyVolley"requests
 *
 *  4. manually set a Cookie
 *      def newCookie = new Cookie(name, value)
 *      ..... set other attributes
 *      MyVolley.setCookie(newCookie)
 *
 *  5. get all cookies
 *      MyVolley.getCookies()       //return a hashmap <name,cookie>
 *
 *  6. clean all cookies
 *      MyVolley.removeAllCookies()
 *
 *  7. get a cookie by name
 *      MyVolley.getCookieByName(name)  //prefix is handled internally
 *
 *  4. get ImageLoader
 *      MyVolley.getImageLoader()                               //get the LurCache/requestQueue based imageLoader which use  1/8th of the available memory for this memory cache
 *
 *         //how to use imageLoader
           public void getImage() {
                 String imageUrl = "http://"+host+":8080/web/image.jsp";
                 NetworkImageView view = (NetworkImageView) findViewById(R.id.network_image_view);
                 view.setDefaultImageResId(android.R.drawable.ic_menu_rotate);
                 view.setErrorImageResId(android.R.drawable.ic_delete);
                 view.setImageUrl(imageUrl, MyVolley.getImageLoader());
           }
 */
class MyVolley{
    private static final String TAG = "MyVolley";

    /**
     * Global request queue for Volley
     * Ideally you should have one centralized place for your Queue,
     * and the best place to initialize queue is in your Application class.
     */
    private static CloseableHttpClient httpClient;   // http client instance
    private static PoolingHttpClientConnectionManager connectionManager //PoolingHttpClientConnectionManager as singleton
    private static PersistentCookieStore myCookieStore     //PersistentCookieStore as singleton, cookies serialized in SharedPreferences
    private static RequestQueue mRequestQueue;      //singleton request queue in whole application
    private static ImageLoader mImageLoader;        //singleton imageLoader in whole application

    public MyVolley() {}   //no instance, just a static Volley requestQueue incluced

    /**
     * init called by Application.initializeInstance()
     * @param context   Application Context
     */
    static void init(Context context) {

        // init singleton PersistentCookieStore
        myCookieStore =  new PersistentCookieStore(context)

        //init PoolingHttpClientConnectionManager to enable [concurrent requests]
        connectionManager = new PoolingHttpClientConnectionManager()
        connectionManager.setMaxTotal(200)      //max 200 connects/per  in the pool  [default is 20]
        connectionManager.setDefaultMaxPerRoute(20) //connects per Route(URL)        [default is 2]

        //init singleton requestQueue         set up a CloseableHttpClient using the new httpComponent 4.4 (updated in /lib folder)
        httpClient = HttpClients.custom()
                .setConnectionManager(connectionManager)
                .setDefaultCookieStore(myCookieStore)
                .build();
        mRequestQueue = Volley.newRequestQueue(context, new HttpClientStack(httpClient));

        //init mImageLoader
        int memClass = ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE))
                .getMemoryClass();
        // Use 1/8th of the available memory for this memory cache.
        int cacheSize = 1024 * 1024 * memClass / 8;
        mImageLoader = new ImageLoader(mRequestQueue, new BitmapLruCache(cacheSize));
    }

    /**
     * @return PersistentCookieStore        the singleton one
     */
    public static PersistentCookieStore getCookieStore() {
        return myCookieStore;
    }

    /**
     * @return The Volley Request queue, the queue will be created if it is null
     */
    public static RequestQueue getRequestQueue() {
        if (mRequestQueue != null) {
            return mRequestQueue;
        }else{
            throw new IllegalStateException("RequestQueue not initialized");
        }
    }

    /**
     * Adds the specified request to the global queue, if tag is specified
     * then it is used else Default TAG is used.
     *
     * @param req
     * @param tag ------- to differentiate our customized queued request
     */
    public static <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);

        VolleyLog.d("AndroidRuntime:Adding request to queue: %s", req.getUrl());

        getRequestQueue().add(req);
    }

    /**
     * Adds the specified request to the global queue using the Default TAG.
     *
     * @param req
     */
    public static <T> void addToRequestQueue(Request<T> req) {
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
    public static void cancelPendingRequests(String tag) {
        String theTag = TextUtils.isEmpty(tag) ? TAG : tag;
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(theTag);
            mRequestQueue.c
        }
    }

    /**
     * get a cookie by its name
     */
    public static Cookie getCookieByName(String name){
        return myCookieStore.getCookieByName(name)
    }
    /**
     * get all cookies
     * @return a hashmap (cookieName: cookie)
     */
    public static ConcurrentHashMap<String, Cookie> getCookies() {
        return myCookieStore.getCookiesHashMap()
    }

    /**
     * to set a cookie to PersistentCookieStore
     */
    public static void setCookie(Cookie cookie) {
        myCookieStore.addCookie(cookie);
    }
    /**
     * remove all cookies
     */
    public static void removeAllCookies(Cookie cookie) {
        myCookieStore.clear()
    }

    /**
     * Get a freindly error msg by analyzing error response & strings defined in resourses
     * @param error
     * @param applicatoinContext
     * @return    a nice msg
     */
    private static String volleyErrorHelper(VolleyError error,Context applicatoinContext){
        Log.i("MyVolley","volleyErrorHelper......")
        Log.i("MyVolley",error.toString())

        def r = applicatoinContext.getResources()
        if (error instanceof TimeoutError) {
            return r.getString(R.string.generic_timeout);
        }
        else if ((error instanceof ServerError) || (error instanceof AuthFailureError)) {    //server error
            NetworkResponse response = error.networkResponse;
            Log.e("MyVolley","ServerError or AuthFailureError, check your request url&params or if server works")
            if (response != null) {
                Log.e("MyVolley","----statusCode:"+response.statusCode)
                switch (response.statusCode) {
                    case 400:
                        return r.getString(R.string.generic_requestWrongFormat_400);      // request format error, server couldn't handle it
                    case 401:
                        return r.getString(R.string.generic_not_authroized_401);      // certificate not authorized
                    case 403:
                        return r.getString(R.string.generic_access_forbidden_403);      // server access forbidden
                    case 404:
                        return r.getString(R.string.generic_resources_not_found_404);      // resources not found
                    case 500:
                        return r.getString(R.string.generic_server_internel_error_500);      // server internel error
                    case 422:
                        try {
                            // server might return error like this { "error": "Some error occured" }
                            // Use "Gson" to parse the result
                            HashMap<String, String> result = new JsonSlurper().parse(response.data)
                            if (result != null && result.containsKey("error")) {
                                return result.get("error");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        Log.e("MyVolley",error.getMessage())
                        return error.getMessage();      // invalid request

                    default:
                        return r.getString(R.string.generic_server_down);
                }
            }

        }else if ((error instanceof NetworkError) || (error instanceof NoConnectionError)){  //network error or hostname error
            return r.getString(R.string.no_internet);
        }
        return r.getString(R.string.generic_error);

    }


    /**
     *  handle a json request (get/post) in a simpler way
     *  @param appInstance       in activity, just  YouApplication.getInstance()
     *  @param reqJson           new a ReqJson according to API
     *
     */
    public static void asyncCall(Application appInstance ,ReqJson reqJson){
        Log.i("MyVolley","asyncCall......")

        JSONObject params = reqJson.params?(new JSONObject(reqJson.params)):(new JSONObject())

        JsonObjectRequest myReq = new JsonObjectRequest(Request.Method.GET,
                reqJson.url,
                (JSONObject)params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("MyVolley",response.toString())
                        try {
                            reqJson.successFunc(response);                      //call defined success function
                        } catch (JSONException e) {
                            appInstance.show("Server Data Parse error");    //Toast to screen
                            Log.e("MyVolley",e.toString())
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("MyVolley",error.toString())
                        if(error.networkResponse) {
                            byte[] htmlBodyBytes = error.networkResponse.data;  //error in details
                            Log.e("MyVolley", new String(htmlBodyBytes), error);
                        }

                        if(reqJson.responseErrorFunc != null){
                            reqJson.responseErrorFunc(error)                    //call defined error function
                        }else{
                            appInstance.showL(volleyErrorHelper(error,appInstance.getContext()))              //default error/timeout handling
                        }
                    }
                }
        ){
            //add extraHeaders
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return reqJson.extraHeaders;
            }
        };


        if(reqJson.useRetry){
            myReq.setRetryPolicy(reqJson.retryPolicy);   //20sec timeout, retry 1 time,
        };

        addToRequestQueue(myReq,reqJson.tag);

    }


    /**
     * Returns instance of ImageLoader initialized with {@see FakeImageCache} which effectively means
     * that no memory caching is used. This is useful for images that you know that will be show
     * only once.
     *
     * @return ImageLoader
     */
    public static ImageLoader getImageLoader() {
        if (mImageLoader != null) {
            return mImageLoader;
        } else {
            throw new IllegalStateException("ImageLoader not initialized");
        }
    }

    /**
     * use imageLoader(with LruCache) to load an image into a view
     * @param viewResourceId
     * @param imgUrl
     * @param activity       in Activity, use "this" ;  in Fragment, use "getActivity()"
     */
    public static void loadImg(int viewResourceId, String imgUrl,Activity activity) {
        loadImg(viewResourceId, imgUrl,activity,null,null)
    }
    public static void loadImg(int viewResourceId, String imgUrl,Activity activity, defaultImageResId,errorImageResId) {
        NetworkImageView imgView = (NetworkImageView)(activity.findViewById(viewResourceId))
        Log.i("MyVolley loadImg",imgView.toString())

        int defaultImg = defaultImageResId?:R.drawable.default_image
        imgView.setDefaultImageResId(defaultImg);
        int failImg = errorImageResId?:R.drawable.failed_image
        imgView.setErrorImageResId(failImg);
        imgView.setImageUrl(imgUrl,imageLoader);
    }
}