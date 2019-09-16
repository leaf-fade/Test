package com.client.skin_core.util;

import android.util.Log;

public class L {
    private static final String TAG = "Skin-Core";
    private final static Boolean debug = true;

    public static void i(String msg) {
        if(debug){
            Log.i(TAG, msg);
        }
    }

    public static void e(String msg) {
        if(debug){
            Log.e(TAG, msg);
        }
    }

    public static void d(String msg) {
        if(debug){
            Log.d(TAG, msg);
        }
    }
}
