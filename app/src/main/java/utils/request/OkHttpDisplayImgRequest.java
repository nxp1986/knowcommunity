package utils.request;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.RunnableFuture;

import utils.ImageUtils;
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
        final Call call = mOkHttpClient.newCall(mRequest);
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
                    //通过流对象获取到的imageSize
                    ImageUtils.ImageSize imageSize = ImageUtils.getImageSize(is);
                    //通过imageView获取到imageSize
                    ImageUtils.ImageSize imageViewSize = ImageUtils.getImageViewSize(mImageView);
                    int inSampleSieze = ImageUtils.calculateInSampleSize(imageSize, imageViewSize);
                    try {
                        is.reset();
                    } catch (IOException e) {
                        response = getInputStream();
                        is = response.body().byteStream();
                    }

                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = false;
                    options.inSampleSize = inSampleSieze;
                    final Bitmap bitmap = BitmapFactory.decodeStream(is);
                    mHttpsUtils.getDelivery().post(new Runnable() {
                        @Override
                        public void run() {
                            mImageView.setImageBitmap(bitmap);
                        }
                    });
                    mHttpsUtils.sendSuccessResultCallback(mRequest, callback);
                } catch (IOException e) {
                    setErrorRes();
                    mHttpsUtils.sendFailResultCallback(mRequest, e, callback);
                } finally {
                    if (is != null) {
                        try {
                            is.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
    }

    private Response getInputStream() throws IOException {
        Call call = mOkHttpClient.newCall(mRequest);
        return call.execute();
    }

    private void setErrorRes() {
        mHttpsUtils.getDelivery().post(new Runnable() {
            @Override
            public void run() {
                mImageView.setImageResource(mErrorResId);
            }
        });
    }

    @Override
    public <T> T invoke(Class<T> clazz) throws IOException {
        return null;
    }
}
