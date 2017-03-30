package com.braindroid.nervecenter.utils.sampling;

public class StreamParams {
    public final short[] sampleSet;
    public final int scaledViewportWidth;
    public final int sliceStart;
    public final int sliceEnd;

    public final float centerPosition;

    public StreamParams(short[] sampleSet, int scaledViewportWidth,
                        int sliceStart, int sliceEnd,
                        float centerPosition) {
        this.sampleSet = sampleSet;
        this.scaledViewportWidth = scaledViewportWidth;
        this.sliceStart = sliceStart;
        this.sliceEnd = sliceEnd;
        this.centerPosition = centerPosition;
    }


}
