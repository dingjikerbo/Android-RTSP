package com.inuker.library;

import android.content.Context;

/**
 * Created by liwentian on 2017/10/12.
 */

public class MyContext {

    private static Context sContext;

    public static Context getContext() {
        return sContext;
    }

    public static void setContext(Context context) {
        sContext = context;
    }
}
