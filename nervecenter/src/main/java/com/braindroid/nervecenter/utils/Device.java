package com.braindroid.nervecenter.utils;

import android.content.Context;
import android.util.TypedValue;

public class Device {

    public static float dipToPx(int dip, Context context) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (float)dip, context.getResources().getDisplayMetrics());
    }

    public static float dipToPx(float dip, Context context) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, context.getResources().getDisplayMetrics());
    }

}
