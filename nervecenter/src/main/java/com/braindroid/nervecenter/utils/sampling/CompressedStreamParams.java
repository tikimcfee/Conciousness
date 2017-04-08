package com.braindroid.nervecenter.utils.sampling;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class CompressedStreamParams extends StreamParams {

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({STREAM_MODE_MAX_ONLY, STREAM_MODE_MIN_ONLY, STREAM_MODE_MIN_MAX, STREAM_MODE_AVERAGE})
    public @interface StreamMode {}
    public static final int STREAM_MODE_MAX_ONLY = 0;
    public static final int STREAM_MODE_MIN_ONLY = 1;
    public static final int STREAM_MODE_MIN_MAX = 2;
    public static final int STREAM_MODE_AVERAGE = 3;

    public @StreamMode int streamMode = STREAM_MODE_AVERAGE;
    public int samplesToSkip = 1;
    public float scaleIncrement = 1;

    public CompressedStreamParams(short[] sampleSet, int scaledViewportWidth,
                                  int sliceStart, int sliceEnd,
                                  float centerPosition) {
        super(sampleSet, scaledViewportWidth, sliceStart, sliceEnd, centerPosition);
    }




}
