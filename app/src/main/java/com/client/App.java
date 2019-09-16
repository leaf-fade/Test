package com.client;

import android.app.Application;
import com.client.skin_core.SkinManager;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        SkinManager.init(this);
    }
}
