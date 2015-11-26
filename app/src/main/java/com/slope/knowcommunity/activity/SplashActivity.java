package com.slope.knowcommunity.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;

import com.slope.knowcommunity.R;
import com.slope.knowcommunity.bean.SplashSo;
import com.slope.knowcommunity.callbackimp.OkhttpCallbackImpl;
import com.slope.knowcommunity.common.AnimationConstant;
import com.slope.knowcommunity.common.Constant;
import com.squareup.okhttp.Request;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import OkhttpUtils.request.OkHttpRequest;

public class SplashActivity extends Activity {
    private ImageView mFrescoSplashImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.splash_activity);
        mFrescoSplashImageView = (ImageView) findViewById(R.id.fresco_splash);

        initSplashView();
    }

    private void initSplashView() {
        new OkHttpRequest.Builder().url(Constant.BASEURL + Constant.START).get(new OkhttpCallbackImpl<SplashSo>() {
            @Override
            public void onError(Request request, Exception e) {

            }

            @Override
            public void onResponse(SplashSo response) {
                String img = response.getImg();
                new OkHttpRequest.Builder().url(img).imageView(mFrescoSplashImageView).displayImage(null);
            }
        });

        ScaleAnimation scaleAnimation = new ScaleAnimation(1.0f, 1.2f, 1.0f, 1.2f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation.setDuration(AnimationConstant.START_SPALSH_DURATION);
        scaleAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                startActivity();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mFrescoSplashImageView.startAnimation(scaleAnimation);
    }

    private void startActivity() {
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in,
                android.R.anim.fade_out);
        finish();
    }
}
