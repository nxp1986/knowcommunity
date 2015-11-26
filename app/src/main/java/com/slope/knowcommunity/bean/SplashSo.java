package com.slope.knowcommunity.bean;

/**
 * @author Slope
 * @desc
 * @createData 2015/11/26 15:06
 */
public class SplashSo {
    private Object text;
    private String img;

    public Object getText() {
        return text;
    }

    public void setText(Object text) {
        this.text = text;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    @Override
    public String toString() {
        return "SplashSo{" +
                "text='" + text + '\'' +
                ", img='" + img + '\'' +
                '}';
    }
}
