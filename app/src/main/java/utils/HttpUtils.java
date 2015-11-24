package utils;

import android.os.Build;
import android.os.Handler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Modifier;

import javax.net.ssl.KeyManager;
import javax.net.ssl.TrustManager;

/**
 * 网络链接的工具类
 * Created by Slope on 2015/11/24.
 */
public class HttpUtils {

    private static HttpUtils mInstance;

    private OkHttpClient mOkHttpClient;
    private Handler mDelivery;
    private Gson mGson;

    private HttpUtils() {
        mOkHttpClient = new OkHttpClient();
        mDelivery = new Handler();
        int sdkVersion = Build.VERSION.SDK_INT;
        if (sdkVersion >= 23) {
            GsonBuilder gsonBuilder = new GsonBuilder().excludeFieldsWithModifiers(
                    Modifier.FINAL,
                    Modifier.TRANSIENT,
                    Modifier.STATIC);
            mGson = gsonBuilder.create();
        } else {
            mGson = new Gson();
        }
    }

    /**
     * 使用单例模式生成一个httpUtils对象
     * 同时这个单例是线程同步的
     * @return
     */
    public static HttpUtils getInstance() {
        if (mInstance == null) {
            synchronized (HttpUtils.class) {
                if (mInstance == null) {
                    mInstance = new HttpUtils();
                }
            }
        }
        return mInstance;
    }

    public Handler getmDelivery() {
        return mDelivery;
    }

    public OkHttpClient getmOkHttpClient() {
        return mOkHttpClient;
    }

    public void execute(final Request request, ResultCallback callback) {
        if (callback == null)
            callback = ResultCallback.DEFAULT_RESULT_CALLBACK;
        final ResultCallback resultCallback = callback;
        resultCallback.onBefor(request);
        mOkHttpClient.newCall(request).enqueue(new Callback() {
            //当网络访问失败的时候调用
            @Override
            public void onFailure(Request request, IOException e) {
                sendFailResultCallback(request, e, resultCallback);
            }

            @Override
            public void onResponse(final Response response) {
                if (response.code() >= 400 && response.code() <= 599) {
                    try {
                        sendFailResultCallback(request, new RuntimeException(response.body().toString()), resultCallback);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return;
                }
                try {
                    String string = response.body().toString();
                    if (resultCallback.mType == String.class) {
                        sendSuccessResultCallback(string, resultCallback);
                    } else {
                        mGson.fromJson(string, resultCallback.mType);
                        sendSuccessResultCallback(string, resultCallback);
                    }
                } catch (JsonParseException e) {//Json解析的错误
                    sendFailResultCallback(response.request(), e, resultCallback);
                } catch (Exception e) {
                    sendFailResultCallback(response.request(), e, resultCallback);
                }
            }

        });
    }

    /**
     * @param request 请求的资源
     * @param clazz   返回的对象
     * @param <T>     泛型
     * @return json 字符串
     * @throws IOException
     */
    public <T> T execute(Request request, Class<T> clazz) throws IOException {
        Call call = mOkHttpClient.newCall(request);
        Response execute = call.execute();
        String string = execute.body().toString();
        return mGson.fromJson(string, clazz);
    }

    /**
     * 当网络访问执行成功的时候调用
     * @param object 网络返回的数据
     * @param callback 结果回调
     */
    private void sendSuccessResultCallback(final Object object, final ResultCallback callback) {
        if (callback == null) {
            return;
        }
        mDelivery.post(new Runnable() {
            @Override
            public void run() {
                callback.onResponse(object);
                callback.onAfter();
            }
        });
    }

    /**
     * 当网络访问失败的时候调用
     * @param request
     * @param e
     * @param callback
     */
    private void sendFailResultCallback(final Request request, final Exception e, final ResultCallback callback) {
        if (callback == null) {
            return;
        }

        mDelivery.post(new Runnable() {
            @Override
            public void run() {
                callback.onError(request, e);
                callback.onAfter();
            }
        });
    }

    public void cancelTag(Object tag){
        mOkHttpClient.cancel(tag);
    }

    /**
     * 设置证书
     * @param certificates
     */
    public void setCertificates(InputStream... certificates) {
        setCertificates(certificates, null, null);
    }

    private void setCertificates(InputStream[] certificates, InputStream bksFile, String password) {
        TrustManager[] trustManagers =  prepareTrustManager(certificates);
        KeyManager[] keyManagers = prepareKeyManager(bksFile, password);
    }

    private KeyManager[] prepareKeyManager(InputStream bksFile, String password) {

        return null;
    }

    private TrustManager[] prepareTrustManager(InputStream[] certificates) {

        return new TrustManager[0];
    }
}
