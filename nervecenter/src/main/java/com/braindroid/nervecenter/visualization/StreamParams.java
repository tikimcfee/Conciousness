package com.braindroid.nervecenter.visualization;

import android.graphics.Path;

public class StreamParams {
    public final short[] sampleSet;
    public final int scaledViewportWidth;
    public final int sliceStart;
    public final int sliceEnd;

    public final Path minPath;
    public final Path maxPath;

    public final float centerPosition;

    public StreamParams(short[] sampleSet, int scaledViewportWidth,
                        int sliceStart, int sliceEnd,
                        Path minPath, Path maxPath,
                        float centerPosition) {
        this.sampleSet = sampleSet;
        this.scaledViewportWidth = scaledViewportWidth;
        this.sliceStart = sliceStart;
        this.sliceEnd = sliceEnd;
        this.minPath = minPath;
        this.maxPath = maxPath;
        this.centerPosition = centerPosition;
    }


}
