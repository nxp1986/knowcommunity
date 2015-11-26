package OkhttpUtils.request;

import android.util.Pair;
import android.widget.ImageView;

import com.squareup.okhttp.Headers;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;

import java.io.File;
import java.io.IOException;
import java.util.IdentityHashMap;
import java.util.Map;

import OkhttpUtils.HttpUtils;
import OkhttpUtils.ResultCallback;

/**
 * OkHttp的请求
 * Created by Slope on 2015/11/24.
 */
public abstract class OkHttpRequest {

    protected HttpUtils mHttpsUtils = HttpUtils.getInstance();
    protected OkHttpClient mOkHttpClient;

    protected RequestBody mRequestBody;
    protected Request mRequest;

    protected String mUrl;
    protected Object mTag;
    protected Map<String, String> mParams;
    protected Map<String, String> mHeaders;

    protected OkHttpRequest(String url, Object tag, Map<String, String> params, Map<String, String> headers) {
        mOkHttpClient = mHttpsUtils.getmOkHttpClient();
        this.mUrl = url;
        this.mTag = tag;
        this.mParams = params;
        this.mHeaders = headers;
    }

    protected abstract Request buildRequest();

    protected abstract RequestBody buildRequestBody();

    protected void prepareInvoked(ResultCallback callback) {
        mRequest = buildRequest();
        mRequestBody = buildRequestBody();
        mRequestBody = wrapRequestBody(mRequestBody, callback);
    }

    public void invokeAsyn(ResultCallback callback) {
        prepareInvoked(callback);
        mHttpsUtils.execute(mRequest, callback);
    }

    protected RequestBody wrapRequestBody(RequestBody requestBody, ResultCallback callback) {
        return mRequestBody;
    }

    public <T> T invoke(Class<T> clazz) throws IOException {
        mRequestBody = buildRequestBody();
        Request request = buildRequest();
        return mHttpsUtils.execute(request, clazz);
    }

    protected void appendHeader(Request.Builder builder, Map<String, String> headers) {
        if (builder == null) {
            throw new IllegalArgumentException("builder can not empty");
        }

        Headers.Builder headerBuilder = new Headers.Builder();

        if (headers == null || headers.isEmpty()) return;

        for (String key : headers.keySet()) {
            headerBuilder.add(key, headers.get(key));
        }

        builder.headers(headerBuilder.build());
    }

    public void cancel() {
        if (mTag != null) {
            mHttpsUtils.cancelTag(mTag);
        }
    }

    public static class Builder {
        private String url;
        private Object tag;
        private Map<String, String> headers;
        private Map<String, String> params;
        private Pair<String, File>[] files;
        private MediaType mediaType;

        private String destFileDir;
        private String destFileName;

        private ImageView imageView;
        private int errorResId = -1;

        //for post
        private String content;
        private byte[] bytes;
        private File file;

        public Builder url(String url) {
            this.url = url;
            return this;
        }

        public Builder tag(Object tag) {
            this.tag = tag;
            return this;
        }

        public Builder params(Map<String, String> params) {
            this.params = params;
            return this;
        }

        public Builder addParams(String key, String val) {
            if (this.params == null) {
                params = new IdentityHashMap<>();
            }
            params.put(key, val);
            return this;
        }

        public Builder headers(Map<String, String> headers) {
            this.headers = headers;
            return this;
        }

        public Builder addHeader(String key, String val) {
            if (this.headers == null) {
                headers = new IdentityHashMap<>();
            }
            headers.put(key, val);
            return this;
        }


        public Builder files(Pair<String, File>... files) {
            this.files = files;
            return this;
        }

        public Builder destFileName(String destFileName) {
            this.destFileName = destFileName;
            return this;
        }

        public Builder destFileDir(String destFileDir) {
            this.destFileDir = destFileDir;
            return this;
        }


        public Builder imageView(ImageView imageView) {
            this.imageView = imageView;
            return this;
        }

        public Builder errResId(int errorResId) {
            this.errorResId = errorResId;
            return this;
        }

        public Builder content(String content) {
            this.content = content;
            return this;
        }

        public Builder mediaType(MediaType mediaType) {
            this.mediaType = mediaType;
            return this;
        }

        public <T> T get(Class<T> clazz) throws IOException
        {
            OkHttpRequest request = new OkHttpGetRequest(url, tag, params, headers);
            return request.invoke(clazz);
        }

        public OkHttpRequest get(ResultCallback callback)
        {
            OkHttpRequest request = new OkHttpGetRequest(url, tag, params, headers);
            request.invokeAsyn(callback);
            return request;
        }

        public <T> T post(Class<T> clazz) throws IOException
        {
            OkHttpRequest request = new OkHttpPostRequest(url, tag, params, headers, mediaType, content, bytes, file);
            return request.invoke(clazz);
        }

        public OkHttpRequest post(ResultCallback callback)
        {
            OkHttpRequest request = new OkHttpPostRequest(url, tag, params, headers, mediaType, content, bytes, file);
            request.invokeAsyn(callback);
            return request;
        }

        public OkHttpRequest upload(ResultCallback callback)
        {
            OkHttpRequest request = new OkHttpUploadRequest(url, tag, params, headers, files);
            request.invokeAsyn(callback);
            return request;
        }

        public <T> T upload(Class<T> clazz) throws IOException
        {
            OkHttpRequest request = new OkHttpUploadRequest(url, tag, params, headers, files);
            return request.invoke(clazz);
        }


        public OkHttpRequest download(ResultCallback callback)
        {
            OkHttpRequest request = new OkHttpDownloadRequest(url, tag, params, headers, destFileName, destFileDir);
            request.invokeAsyn(callback);
            return request;
        }

        public String download() throws IOException
        {
            OkHttpRequest request = new OkHttpDownloadRequest(url, tag, params, headers, destFileName, destFileDir);
            return request.invoke(String.class);
        }

        public void displayImage(ResultCallback callback)
        {
            OkHttpRequest request = new OkHttpDisplayImgRequest(url, tag, params, headers, imageView, errorResId);
            request.invokeAsyn(callback);
        }

    }
}
