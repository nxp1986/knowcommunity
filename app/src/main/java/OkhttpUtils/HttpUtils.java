package OkhttpUtils;

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
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

/**
 * 网络链接的工具类
 * Created by Slope on 2015/11/24.
 */
public class HttpUtils {
    /**
     * HtppUtils的实例对象
     */
    private static HttpUtils mInstance;
    /**
     * OkHttpClient对象
     */
    private OkHttpClient mOkHttpClient;
    /**
     * Handler对象
     */
    private Handler mDelivery;
    /**
     * Gson解析对象
     */
    private Gson mGson;

    private HttpUtils() {
        mOkHttpClient = new OkHttpClient();
        mDelivery = new Handler();
        int sdkVersion = Build.VERSION.SDK_INT;
        //当sdk的版本在23以上的时候需要忽悠下面的几个字段  包括 final transient static
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

    public Handler getDelivery() {
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
                    String string = response.body().string();
                    if (resultCallback.mType == String.class) {
                        sendSuccessResultCallback(string, resultCallback);
                    } else {
                        Object object = mGson.fromJson(string, resultCallback.mType);
                        sendSuccessResultCallback(object, resultCallback);
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
        String string = execute.body().string();
        return mGson.fromJson(string, clazz);
    }

    /**
     * 当网络访问执行成功的时候调用
     * @param object   网络返回的数据
     * @param callback 结果回调
     */
    public void sendSuccessResultCallback(final Object object, final ResultCallback callback) {
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
    public void sendFailResultCallback(final Request request, final Exception e, final ResultCallback callback) {
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

    public void cancelTag(Object tag) {
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
        try {
            TrustManager[] trustManagers = prepareTrustManager(certificates);
            KeyManager[] keyManagers = prepareKeyManager(bksFile, password);
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(keyManagers, new TrustManager[]{new MyTrustManager(chooseTrustManager(trustManagers))}, new SecureRandom());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }
    }

    private KeyManager[] prepareKeyManager(InputStream bksFile, String password) {
        try {
            if (bksFile == null || password == null) return null;
            KeyStore clientKeyStore = KeyStore.getInstance("BKS");
            clientKeyStore.load(bksFile, password.toCharArray());
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            keyManagerFactory.init(clientKeyStore, password.toCharArray());
            return keyManagerFactory.getKeyManagers();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (UnrecoverableKeyException e) {
            e.printStackTrace();
        }
        return null;
    }

    private TrustManager[] prepareTrustManager(InputStream[] certificates) {
        if (certificates == null || certificates.length < 0) return null;
        try {
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null);
            int index = 0;
            for (InputStream certificate:certificates){
                String certificateAlias = Integer.toString(index++);
                keyStore.setCertificateEntry(certificateAlias, certificateFactory.generateCertificate(certificate));
                try {
                    if (certificate != null) {
                        certificate.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            TrustManagerFactory trustManagerFactory = null;
            trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(keyStore);
            TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
            return trustManagers;
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new TrustManager[0];
    }

    private X509TrustManager chooseTrustManager(TrustManager[] trustManagers) {
        for (TrustManager trustManager : trustManagers) {
            if (trustManager instanceof X509TrustManager) {
                return (X509TrustManager) trustManager;
            }
        }
        return null;
    }

    private class MyTrustManager implements X509TrustManager {

        private X509TrustManager defaultTrustManager;
        private X509TrustManager localTrustManager;

        public MyTrustManager(X509TrustManager localTrustManager) throws NoSuchAlgorithmException, KeyStoreException {
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init((KeyStore) null);
            defaultTrustManager = chooseTrustManager(trustManagerFactory.getTrustManagers());
            this.localTrustManager = localTrustManager;
        }


        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            //检查客户端证书时调用的方法
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            //检查服务器端证书调用的方法
            try {
                defaultTrustManager.checkServerTrusted(chain, authType);
            } catch (CertificateException e) {
                localTrustManager.checkServerTrusted(chain,authType);
            }
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }
}
