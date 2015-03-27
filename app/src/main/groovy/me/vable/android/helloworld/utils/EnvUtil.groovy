package me.vable.android.helloworld.utils

import groovy.transform.CompileStatic
import me.vable.android.helloworld.BuildConfig;

@CompileStatic
class EnvUtil {
    public static final boolean DEBUG = BuildConfig.IS_DEV_ENV;
    public static final String devServer = BuildConfig.DEV_SERVER
    public static final String productServer = BuildConfig.PRODUCT_SERVER

    //get server address:port
    public static String getHost(){
        if (DEBUG) {
            return devServer
        }
        return productServer
    }
}