package utils;

import android.graphics.BitmapFactory;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.InputStream;
import java.lang.reflect.Field;

/**
 * classes : utils.ImageUtils
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

    public static ImageSize getImageViewSize(View view) {
        ImageSize imageSize = new ImageSize();
        imageSize.width = getExpectWigth(view);
        imageSize.height = getExpectHeight(view);
        return imageSize;
    }

    /**
     * 获取到期望的View的高度
     * @param view
     * @return
     */
    private static int getExpectHeight(View view) {
        int height = 0;
        if (view == null) return 0;
        ViewGroup.LayoutParams params = view.getLayoutParams();

        if (params != null && params.height != ViewGroup.LayoutParams.WRAP_CONTENT) {
            height = view.getHeight();
        }
        if (height <= 0 && params != null) {
            height = params.height;
        }
        if (height <= 0) {
            height = getimageViewFieldValue(view, "mMaxHeight");
        }

        if (height <= 0) {
            DisplayMetrics displayMetrics = view.getContext().getResources().getDisplayMetrics();
            height = displayMetrics.heightPixels;
        }

        return height;
    }

    /**
     * 获取期望的view的宽度
     * @param view
     * @return
     */
    private static int getExpectWigth(View view) {
        int wight = 0;
        if (view == null) return 0;
        ViewGroup.LayoutParams params = view.getLayoutParams();
        if (params != null && params.width != ViewGroup.LayoutParams.WRAP_CONTENT) {
            wight = view.getWidth();
        }
        if (wight <= 0 && params != null) {
            wight = params.width;
        }
        if (wight <= 0) {
            wight = getimageViewFieldValue(view, "mMaxWight");
        }
        if (wight <= 0) {
            wight = view.getContext().getResources().getDisplayMetrics().widthPixels;
        }
        return wight;
    }

    /**
     * 通过反射的方式获取imageView中的某一个属性值
     * @param object view的类型
     * @param fieldName 字段名称
     * @return
     */
    private static int getimageViewFieldValue(Object object, String fieldName) {
        int value = 0;
        try {
            Field field = ImageView.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            int fieldValue = field.getInt(object);
            if (fieldValue > 0 && fieldValue < Integer.MAX_VALUE) {
                value = fieldValue;
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return value;
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
