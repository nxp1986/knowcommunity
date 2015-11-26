package OkhttpUtils.request;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.RequestBody;

import java.io.IOException;

import okio.BufferedSink;

/**
 * Created by Slope on 2015/11/24.
 */
public class CountingRequestBody extends RequestBody {
    private RequestBody mRequestBody;
    private Listener mListener;

    public CountingRequestBody(RequestBody requestBody, Listener listener) {
        this.mListener = listener;
        this.mRequestBody = requestBody;
    }

    @Override
    public MediaType contentType() {
        return null;
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {

    }

    public static interface Listener {
        public void onRequestProgress(long bytesWritten, long contentLength);
    }

}
