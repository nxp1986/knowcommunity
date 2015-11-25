package utils.request;

import android.widget.ImageView;

import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import utils.ResultCallback;

/**
 * 加载图片的异步任务
 * Created by Slope on 2015/11/24.
 */
public class OkHttpDisplayImgRequest extends OkHttpGetRequest {
    private ImageView mImageView;
    private int mErrorResId;

    public OkHttpDisplayImgRequest(String url, Object tag, Map<String, String> params, Map<String, String> headers, ImageView imageView, int errorResId) {
        super(url, tag, params, headers);
        this.mImageView = imageView;
        this.mErrorResId = errorResId;
    }

    @Override
    public void invokeAsyn(final ResultCallback callback) {
        prepareInvoked(callback);
        Call call = mOkHttpClient.newCall(mRequest);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                setErrorRes();
                mHttpsUtils.sendFailResultCallback(request, e, callback);
            }

            @Override
            public void onResponse(Response response) throws IOException {
                InputStream is = null;
                try {
                    is = response.body().byteStream();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void setErrorRes() {

    }

    @Override
    public <T> T invoke(Class<T> clazz) throws IOException {
        return null;
    }
}
