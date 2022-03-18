package com.example.appdev;
public class JobItem {
    private int mImageResource;
    private String mText1;
    private String mText2;

    public JobItem(int mImageResource, String text1, String text2) {
        this.mImageResource = mImageResource;
        this.mText1 = text1;
        this.mText2 = text2;
    }

    public int getmImageResource() {
        return mImageResource;
    }

    public String getmText1() {
        return mText1;
    }

    public String getmText2() {
        return mText2;
    }
}
