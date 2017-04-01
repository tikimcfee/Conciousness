package com.braindroid.nervecenter.visualization.interactive;

public interface ZoomState {

    float getMaxScale();
    void setMaxScale(float maxScale);

    float getMinScale();
    void setMinScale(float minScale);

    void setHorizontalScaleEnabled(boolean enabled);
    void setVerticalScaleEnabled(boolean enabled);

}
