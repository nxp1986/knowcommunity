package OkhttpUtils;

import com.google.gson.internal.$Gson$Types;
import com.squareup.okhttp.Request;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * 带泛型的结果回调
 * Created by Slope on 2015/11/24.
 */
public abstract class ResultCallback<T> {
    /**
     * 当前的类型
     */
    public Type mType;

    public ResultCallback() {
        mType = getSuperclassTypeParameter(getClass());
    }

    /**
     * 获取父类的参数类型
     * @param subClass 父类的参数
     * @return
     */
    private Type getSuperclassTypeParameter(Class<?> subClass) {
        Type superclass = subClass.getGenericSuperclass();
        if (superclass instanceof Class) {
            throw new RuntimeException("Missing type parameter");
        }
        ParameterizedType parameterized = (ParameterizedType) superclass;
        return  $Gson$Types.canonicalize(parameterized.getActualTypeArguments()[0]);
    }

    /**
     * 网络访问之前
     * @param request
     */
    public void onBefor(Request request) {}

    /**
     * 网络访问之后
     */
    public void onAfter(){}

    /**
     * 网络访问中
     * @param progress
     */
    public void inProgress(float progress){}

    /**
     * 当发生错误的时候 或抛出异常的时候
     * @param request 请求
     * @param e 异常欣喜
     */
    public abstract void onError(Request request, Exception e);

    /**
     * 网络访问的响应
     * @param response 响应的参数
     */
    public abstract void onResponse(T response);

    /**
     * 默认的结果回调
     */
    public static final ResultCallback<String> DEFAULT_RESULT_CALLBACK = new ResultCallback<String>() {
        @Override
        public void onError(Request request, Exception e) {

        }

        @Override
        public void onResponse(String response) {

        }
    };

}
