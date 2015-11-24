package utils.request;

import android.util.Pair;

import com.squareup.okhttp.MediaType;

import java.io.File;
import java.util.Map;

/**
 * Created by Slope on 2015/11/24.
 */
public class OkHttpUploadRequest extends OkHttpPostRequest {
    private Pair<String, File>[] mFiles;
    public OkHttpUploadRequest(String url, Object tag, Map<String, String> params, Map<String, String> headers, Pair<String, File>[] files) {
        super(url, tag, params, headers, null, null, null, null);
        this.mFiles = files;
    }
}
