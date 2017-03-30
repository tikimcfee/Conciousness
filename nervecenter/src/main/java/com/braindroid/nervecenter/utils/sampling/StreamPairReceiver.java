package com.braindroid.nervecenter.utils.sampling;

public interface StreamPairReceiver {
    void onExtremePair(int position1, short min1, short max1, int position2, short min2, short max2);
}
