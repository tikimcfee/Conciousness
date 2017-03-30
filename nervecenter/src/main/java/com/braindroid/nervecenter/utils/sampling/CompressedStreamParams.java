package com.braindroid.nervecenter.utils.sampling;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class CompressedStreamParams extends StreamParams {

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({STREAM_MODE_MAX_ONLY, STREAM_MODE_MINS_ONLY})
    public @interface StreamMode {}
    public static final int STREAM_MODE_MAX_ONLY = 0;
    public static final int STREAM_MODE_MINS_ONLY = 1;
    public static final int STREAM_MODE_AVERAGE = 2;

    public @StreamMode int streamMode = STREAM_MODE_AVERAGE;
    public int samplesToSkip = 4;

    public CompressedStreamParams(short[] sampleSet, int scaledViewportWidth,
                                  int sliceStart, int sliceEnd,
                                  float centerPosition) {
        super(sampleSet, scaledViewportWidth, sliceStart, sliceEnd, centerPosition);
    }




}
