package me.vable.android.helloworld.activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.ActionBarActivity
import android.util.Log
import android.view.View
import android.widget.TextView
import com.android.volley.VolleyError
import me.vable.android.helloworld.MyVolley
import me.vable.android.helloworld.ReqJson
import me.vable.android.helloworld.WeatherApp
import org.json.JSONObject

public class SyncResult extends ActionBarActivity {
    def list = []

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        String msg = application.weatherData

        // Create the text view
        TextView textView = new TextView(this);
        textView.setId(View.generateViewId())
        textView.setTextSize(20);
        textView.setText(msg);

        list.add(textView)      //动态创建的widget 保存下来为了后面能获得

        // Set the text view as the activity layout
        setContentView(textView);

        textView.onClickListener = {
            Log.i("AndroidRuntime","wtf, touch me?")
            //it.setText('1234cd')
            //Log.i("AndroidRuntime",it.getText())
/*
    //[try bbbb]  directly way using JsonObjectRequest
            JsonObjectRequest myReq = new JsonObjectRequest(Request.Method.GET,
                    "http://wthrcdn.etouch.cn/weather_mini?citykey=101010100",
                    null,   // [citykey:101010100] as JSONObject,
                    createMyReqSuccessListener(it),
                    createMyReqErrorListener(it)
            );

            //20sec timeout, retry 1 time,
            myReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));

            MyVolley.addToRequestQueue(myReq);
*/
            def rj = new ReqJson()
            rj.url="http://wthrcdn.etouch.cn/weather_mini?citykey=101010100"
//            rj.url="http://wthrcdn.etouch.cn/weatherXXXX_mini?citykey=101010100"  // test serverError/Auth Error in MyVolley
//            rj.url="http://wthrcdn.etouch.cn1/weather_mini?citykey=101010100"     //test NO_NETWORK error in MyVolley
            rj.successFunc = this.&demoSuccessFunction
            //rj.responseErrorFunc = this.&demoErrorHandler     //test errorhandler, @optional , unless you wanna handle sth

            MyVolley.asyncCall(WeatherApp.getInstance(),rj)
        }
    }

        public void demoSuccessFunction(JSONObject response){
            def view  = this.list[0]
            view.setText(response.data.toString())
        }

        public void demoErrorHandler(VolleyError error){
            def view  = this.list[0]
            view.setText(error.getMessage());
        }
/*
        //[try bbbb]  directly way using JsonObjectRequest

        protected Response.Listener<JSONObject> createMyReqSuccessListener(View v) {
            return new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        v.setText(response.data.toString());
                        Log.i("AndroidRuntime",response.toString())
                    } catch (JSONException e) {
                        v.setText("Parse error");
                    }
                }
            };
        }
        private Response.ErrorListener createMyReqErrorListener(View v) {
            return new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.i("AndroidRuntime",response.toString())
                    v.setText(error.getMessage());

                    //handle timeout
                    if (error.networkResponse == null) {
                        if (error.getClass().equals(TimeoutError.class)) {
                            // Show timeout error message
                            application.showL("Oops. Timeout error!")
                        }
                    }
                }
            };
        }
*/

}