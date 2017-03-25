package com.braindroid.conciousness;

import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;

public class ViewFinder {

    public static <T> T in(ViewGroup viewGroup, int viewId) {
        return (T)viewGroup.findViewById(viewId);
    }

    public static <T> T in(View view, int viewId) {
        return (T)view.findViewById(viewId);
    }
    public static <T> T in(AppCompatActivity activity, int viewId) {
        return (T)activity.findViewById(viewId);
    }

}
