package com.braindroid.nervecenter.utils.sampling;

public class StreamResults {

    public final float[] finalPointsMin;
    public final float[] finalPointsMax;

    public StreamResults(float[] finalPointsMin, float[] finalPointsMax) {
        this.finalPointsMin = finalPointsMin;
        this.finalPointsMax = finalPointsMax;
    }
}
