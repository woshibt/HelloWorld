package me.vable.android.helloworld.activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.ActionBarActivity
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.TextView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import org.json.JSONException
import org.json.JSONObject

public class SyncResult extends ActionBarActivity {

    @Override
    boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event)
    }

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

        // Set the text view as the activity layout
        setContentView(textView);
        //textView.onTouchListener = this.onVClicked(textView)

        //RequestQueue queue = Volley.newRequestQueue(this);

        textView.onClickListener = {
            Log.i("AndroidRuntime","wft, is it touched already?")
            it.setText('1234cd')
            Log.i("AndroidRuntime",it.getText())

            JsonObjectRequest myReq = new JsonObjectRequest(Request.Method.GET,
                    "http://wthrcdn.etouch.cn/weather_mini?citykey=101010100",
                    null,   // [citykey:101010100] as JSONObject,
                    createMyReqSuccessListener(it),
                    createMyReqErrorListener(it)
            );
            //WeatherApp.getInstance().addToRequestQueue(myReq);
            application.addToRequestQueue(myReq);
        }
    }

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
                    v.setText(error.getMessage());
                }
            };
        }


}