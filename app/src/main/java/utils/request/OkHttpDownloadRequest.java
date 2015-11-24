package utils.request;

import java.util.Map;

/**
 * Created by Slope on 2015/11/24.
 */
public class OkHttpDownloadRequest extends OkHttpGetRequest {
    private String mDestFileName;
    private String mDestFileDir;

    public OkHttpDownloadRequest(String url, Object tag, Map<String, String> params, Map<String, String> headers, String destFileName, String destFileDir) {
        super(url, tag, params, headers);
        this.mDestFileDir = destFileDir;
        this.mDestFileName = destFileName;
    }
}
