package utils.request;

import android.text.TextUtils;

import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;

import java.util.Map;

/**
 * Created by Slope on 2015/11/24.
 */
public class OkHttpGetRequest extends OkHttpRequest {

    protected OkHttpGetRequest(String url, Object tag, Map<String, String> params, Map<String, String> headers) {
        super(url, tag, params, headers);
    }

    @Override
    protected Request buildRequest() {
        if (TextUtils.isEmpty(mUrl)) {
            throw new IllegalArgumentException("the url can not be empty");
        }

        mUrl = appendParams(mUrl, mParams);
        Request.Builder builder = new Request.Builder();
        appendHeader(builder, mHeaders);
        builder.url(mUrl).tag(mTag);
        return builder.build();
    }

    private String appendParams(String url, Map<String, String> params) {
        StringBuffer buffer = new StringBuffer();
        buffer.append(url +"?");
        if (params != null && !params.isEmpty()) {
            for (String key : params.keySet()) {
                buffer.append(key).append("=").append(params.get(key)).append("&");
            }
        }

        buffer = buffer.deleteCharAt(buffer.length() - 1);
        return buffer.toString();
    }

    @Override
    protected RequestBody buildRequestBody() {
        return null;
    }
}
