package activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.Window;

import com.facebook.drawee.view.SimpleDraweeView;
import com.slope.knowcommunity.R;

import java.io.File;

/**
 * 欢迎页面
 * Created by Slope on 2015/11/24.
 */
public class SplashActivity extends Activity {

    private SimpleDraweeView mFrescoSplashImageView;

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.splash_activity);
        mFrescoSplashImageView = (SimpleDraweeView) findViewById(R.id.fresco_splash);

        initSplashView();
    }

    private void initSplashView() {
        File dir = getFilesDir();

    }
}
