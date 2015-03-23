package me.vable.android.helloworld

import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request

/**
 *  A request param class customized for MyVolley.groovy usage
 *  ---- params
 *  -----tag
 *  ---- various headers
 *  ---- timeout retry strategy
 *  ---- headers
 *  ---- cookies
 *    reference: http://arnab.ch/blog/2013/08/asynchronous-http-requests-in-android-using-volley/
 *
 *  [Usage Sample Code]
 *
 *  //=========MUST DO=========
 *      ReqJson rj = new ReqJson()
 *      rj.tag = "activity name"
 *      rj.url = "server url"
 *      rj.params.put(key,value)                //@ignore if no params ,  repeat it if multiple values
 *      rj.successFunc = this.&yourSuccessFunc  // function defined seperately.
 *                                              //& MUST be used as this is a reference of function
 *                                              //      def yourSuccessFunc(response){.....}
 *
 * //=========optional==========
 *      rj.method = Request.Method.POST         //@ignore this if GET
 *
 *      rj.responseErrorFunc = this.&responseErrorFunc          //if set, & MUST be used as this is a reference of function
 *                                              //if not set, default available in MyVolley.groovy
 *
 *      rj.timeoutFunc = this.&timeoutFunc      //if set, & MUST be used as this is a reference of function
 *                                              //if not set, default available in MyVolley.groovy
 *
 *                                              //@ignore suggested
 *      rj.useRetry                             //default true,  NO retry upon timeout if set false
 *      rj.retryPolicy                          //@ignore unless you have a clear understanding of DefaultRetryPolicy (google it)
 *
 *      rj.extraHeaders.put(key,value)               //@ignore or repeat if there are multiple values
 *                                              // -----extra headers for : User-Agent, Accept-Language, etc.
 *
 *      rj.cookieList                           //@ignore if you don't have specific cookie to put in REQUEST
 */

class ReqJson{
    int method = Request.Method.GET         //or Request.Method.POST

    String url = ""

    HashMap<String, String> params = [:]            //new JSONObject(params) in JsonObjectRequest by MyVolley.groovy

    def successFunc = null     //function for successful callback,  [Response] will be passed to it

    def responseErrorFunc  = null      //function for error callback, [VolleyError] will be passed to it

    // seems not useful
    //def timeoutFunc  = null    //function for timeout callback

    /**
     *  request tag
     *  @best practice  use your activity name to be the "tag"
     *  @useful Scenario     to cancel all requests of a type,
     *          e.g., when device rotates the activity will restart, so perhaps you need to cancel all related ongoing requests
     *  if not set, default one will be "MyVolley" by MyVolley class settings
     */
    String tag = ""

    /**
     * Retry strategy definition
     */
    Boolean useRetry = true                 //false to give up upon timeout
    DefaultRetryPolicy  retryPolicy = new DefaultRetryPolicy(20 * 1000, 1, 1.0f)  //retry once at most after 20 secs, second timeout set as same

    /**
     * Setting Request Headers (HTTP headers)
     *      e.g. for authorization
     *      ----Volley Request class provides a method called getHeaders() which you need to override to add your custom headers if necessary.
     */

    HashMap<String, String> extraHeaders = [:]

    /**
     *  cookies to set in the requests
     */
    List<HttpCookie> cookieList = []

    public ReqJson(){}  //construction
}