package com.braindroid.nervecenter.utils.sampling;

public interface StreamReceiver {
    void onExtreme(int position, short min, short max);
}
