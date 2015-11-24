package utils.request;

import android.widget.ImageView;

import java.util.Map;

/**
 * Created by Slope on 2015/11/24.
 */
public class OkHttpDisplayImgRequest extends OkHttpGetRequest {
    private ImageView mImageView;
    private int mErrorResId;

    public OkHttpDisplayImgRequest(String url, Object tag, Map<String, String> params, Map<String, String> headers, ImageView imageView, int errorResId) {
        super(url, tag, params, headers);
        this.mImageView = imageView;
        this.mErrorResId = errorResId;
    }
}
