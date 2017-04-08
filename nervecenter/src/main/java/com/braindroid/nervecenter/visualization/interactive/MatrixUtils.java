package com.braindroid.nervecenter.visualization.interactive;

import android.graphics.Matrix;
import android.view.View;

public class MatrixUtils {

    public static int getScaledWidth(Matrix matrix, View view) {
        final float[] vals = new float[9];
        matrix.getValues(vals);
        return Math.round(vals[Matrix.MSCALE_X] * view.getWidth());
    }

    public static float getScaledX(Matrix matrix) {
        final float[] vals = new float[9];
        matrix.getValues(vals);
        return vals[Matrix.MSCALE_X];
    }

    public static int getScaledWidth(Matrix matrix, int width) {
        return Math.round(getScaledX(matrix) * width);
    }

    public static void setScaleX(Matrix matrix, float width) {
        final float[] vals = new float[9];
        matrix.getValues(vals);
        vals[Matrix.MSCALE_X] = width;
        matrix.setValues(vals);
    }

}
