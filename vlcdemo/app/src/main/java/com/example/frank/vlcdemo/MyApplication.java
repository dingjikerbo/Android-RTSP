package com.example.frank.vlcdemo;

import android.app.Application;
import android.content.Context;

import com.inuker.library.MyContext;

/**
 * Created by liwentian on 2017/10/12.
 */

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        MyContext.setContext(this);
    }

    public static Context getContext() {
        return MyContext.getContext();
    }
}
