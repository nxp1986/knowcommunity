package utils.request;

import android.util.Pair;

import com.squareup.okhttp.Headers;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.RequestBody;

import java.io.File;
import java.net.FileNameMap;
import java.net.URLConnection;
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

    @Override
    protected void validParams() {
        if (mParams == null && mFiles == null) {
            throw new IllegalArgumentException("params and files can't both null in upload request");
        }
    }

    @Override
    protected RequestBody buildRequestBody() {
        MultipartBuilder builder = new MultipartBuilder().type(MultipartBuilder.FORM);
        addParams(builder, mParams);
        if (mFiles != null) {
            RequestBody requestBody = null;
            for (int i = 0; i < mFiles.length; i++) {
                Pair<String, File> filePair = mFiles[i];
                String fileKeyName = filePair.first;
                File file = filePair.second;
                String fileName = file.getName();
                requestBody = RequestBody.create(MediaType.parse(guessMimeType(fileKeyName)), file);
                builder.addPart(Headers.of("Content-Disposition", "form-data; name=\""
                        + fileKeyName + "\"; fileName=\""
                        + fileName + "\""),
                        requestBody);
            }
        }
        return builder.build();
    }

    private String guessMimeType(String fileKeyName) {
        FileNameMap fileNameMap = URLConnection.getFileNameMap();
        String contentTypeFor = fileNameMap.getContentTypeFor(fileKeyName);
        if (contentTypeFor == null) {
            contentTypeFor = "application/octet-stream";
        }
        return contentTypeFor;
    }


    private void addParams(MultipartBuilder builder, Map<String, String> params) {

    }
}
