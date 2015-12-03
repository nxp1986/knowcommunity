package com.slope.knowcommunity.base;

import android.app.Application;
import android.content.Context;

import com.facebook.drawee.backends.pipeline.Fresco;

/**
 * @author Slope
 * @desc
 * @createData 2015/12/3 9:53
 */
public class BaseKCApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Fresco.initialize(getContext());
    }


    public Context getContext() {
        return getApplicationContext();
    }
}
