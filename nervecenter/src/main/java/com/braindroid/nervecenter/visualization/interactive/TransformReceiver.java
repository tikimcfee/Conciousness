package com.braindroid.nervecenter.visualization.interactive;

import android.graphics.Matrix;

public interface TransformReceiver {

    void onNewTransform(Matrix transform);

    int receiverHeight();
    int receiverWidth();

}
