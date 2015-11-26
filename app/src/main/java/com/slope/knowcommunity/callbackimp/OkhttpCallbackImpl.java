package com.slope.knowcommunity.callbackimp;

import com.squareup.okhttp.Request;

import OkhttpUtils.ResultCallback;

/**
 * @author Slope
 * @desc
 * @createData 2015/11/26 15:07
 */
public abstract class OkhttpCallbackImpl<T> extends ResultCallback<T> {

    @Override
    public void onBefor(Request request) {
        super.onBefor(request);
    }

    @Override
    public void onAfter() {
        super.onAfter();
    }
}
