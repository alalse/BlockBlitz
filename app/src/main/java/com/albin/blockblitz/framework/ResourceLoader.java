package com.albin.blockblitz.framework;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

//Class used to get access to resources outside of activities and fragments
public class ResourceLoader {
    private static Resources res;

    public static void setResources(Resources resources) { res = resources; }

    public static Bitmap getBitmap(int i) { return BitmapFactory.decodeResource(res, i); }

    public static String getString(int i) { return res.getString(i); }

    public static String getString(int i, String placeholder) { return res.getString(i, placeholder); }
}