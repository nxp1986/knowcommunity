package com.slope.knowcommunity.base;

import android.app.Application;
import android.content.Context;

import com.facebook.drawee.backends.pipeline.Fresco;


/**
 * 基本的AppLication对象，通过这个对象可以获取全局的一些数据
 * Created by Slope on 2015/11/24.
 */
public class BaseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Fresco.initialize(getContext());
    }


    public Context getContext() {
        return getApplicationContext();
    }
}
