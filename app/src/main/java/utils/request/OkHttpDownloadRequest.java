package utils.request;

import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import utils.HttpUtils;
import utils.ResultCallback;

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

    @Override
    public void invokeAsyn(final ResultCallback callback) {
        prepareInvoked(callback);
        Call call = mOkHttpClient.newCall(mRequest);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                mHttpsUtils.sendFailResultCallback(request, e, callback);
            }

            @Override
            public void onResponse(Response response) throws IOException {
                try {
                    String filePath = saveFile(response, callback);
                    HttpUtils.getInstance().sendSuccessResultCallback(filePath, callback);
                } catch (Exception e) {
                    e.printStackTrace();
                    HttpUtils.getInstance().sendFailResultCallback(response.request(), e, callback);
                }
            }
        });
        super.invokeAsyn(callback);
    }

    private String getFileName(String path) {
        int separatorIndex = path.lastIndexOf("/");
        return separatorIndex < 0 ? path : path.substring(separatorIndex + 1, path.length());
    }

    @Override
    public <T> T invoke(Class<T> clazz) throws IOException {
        Call call = mOkHttpClient.newCall(mRequest);
        Response response = call.execute();
        return (T) saveFile(response, null);
    }

    private String saveFile(Response response, final ResultCallback callback) {
        InputStream is = null;
        byte[] buf = new byte[2048];
        int len = 0;
        FileOutputStream fos = null;
        try {
            is = response.body().byteStream();
            final long total = response.body().contentLength();
            long sum = 0;
            File dir = new File(mDestFileDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File file = new File(dir, mDestFileName);
            fos = new FileOutputStream(file);
            while ((len = is.read(buf)) != -1) {
                sum += len;
                fos.write(buf, 0, len);
                if (callback != null) {
                    final long finalSum = sum;
                    mHttpsUtils.getDelivery().post(new Runnable() {
                        @Override
                        public void run() {
                            callback.inProgress(finalSum * 1.0f / total);
                        }
                    });
                }
            }

            fos.flush();
            return file.getAbsolutePath();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
