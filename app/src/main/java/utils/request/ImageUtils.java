package utils.request;

import android.graphics.BitmapFactory;

import java.io.InputStream;

/**
 * classes : utils.request.ImageUtils
 * @create : Created by Slope on 2015/11/25
 * @description : 图片的工具类
 */
public class ImageUtils {

    /**
     * 根据inputStream获取图片的宽度和高度
     * @param inputStream 传入的流对象
     * @return 返回图片的高度和宽度的对象
     */
    public static ImageSize getImageSize(InputStream inputStream) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(inputStream, null, options);
        return new ImageSize(options.outWidth, options.outHeight);
    }

    /**
     * 根据源图片和目标图片的大小计算出他们之间的
     * @param srcSize
     * @param targetSize
     * @return
     */
    public static int calculateInSampleSize(ImageSize srcSize, ImageSize targetSize) {
        int height = srcSize.height;
        int width = srcSize.width;
        int inSampleSize = 1;
        int reqHeight = targetSize.height;
        int reqWidth = targetSize.width;

        if (height > reqHeight && width > reqWidth) {
            int widthRatio = Math.round((float) width / (float) reqWidth);
            int heightRatio = Math.round((float) height / (float) reqHeight);
            inSampleSize = Math.max(widthRatio, heightRatio);
        }
        return inSampleSize;
    }

    public static class ImageSize {
        int width;
        int height;

        public ImageSize() {
        }

        public ImageSize(int width, int height) {
            this.height = height;
            this.width = width;
        }
    }
}
