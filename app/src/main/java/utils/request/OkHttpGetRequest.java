package utils.request;

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
        return null;
    }

    @Override
    protected RequestBody buildRequestBody() {
        return null;
    }
}
