package com.braindroid.nervecenter.visualization.interactive;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.TextureView;

public class ZoomableTextureView extends TextureView implements ZoomState {

    private static final String SUPERSTATE_KEY = "superState";
    private static final String MIN_SCALE_KEY = "minScale";
    private static final String MAX_SCALE_KEY = "maxScale";

    private ZoomOnTouchListeners onTouch;

    public ZoomableTextureView(Context context) {
        super(context);
        initView();
    }

    public ZoomableTextureView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public ZoomableTextureView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView();
    }

    private void initView() {
        onTouch = new ZoomOnTouchListeners(this);
        setOnTouchListener(onTouch);
    }

    //region Public Scaling API

    @Override
    public float getMaxScale() {
        return onTouch.getMaxScale();
    }

    @Override
    public float getMinScale() {
        return onTouch.getMinScale();
    }

    @Override
    public void setMaxScale(float maxScale) {
        onTouch.setMaxScale(maxScale);
    }

    @Override
    public void setMinScale(float minScale) {
        onTouch.setMinScale(minScale);
    }

    @Override
    public void setHorizontalScaleEnabled(boolean enabled) {
        onTouch.setHorizontalScaleEnabled(enabled);
    }

    @Override
    public void setVerticalScaleEnabled(boolean enabled) {
        onTouch.setVerticalScaleEnabled(enabled);
    }

    //endregion

    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(SUPERSTATE_KEY, super.onSaveInstanceState());
        bundle.putFloat(MIN_SCALE_KEY, onTouch.getMinScale());
        bundle.putFloat(MAX_SCALE_KEY, onTouch.getMaxScale());
        return bundle;

    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            onTouch.setMinScale(bundle.getInt(MIN_SCALE_KEY));
            onTouch.setMaxScale(bundle.getInt(MAX_SCALE_KEY));
            state = bundle.getParcelable(SUPERSTATE_KEY);
        }
        super.onRestoreInstanceState(state);
    }

}