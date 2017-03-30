package com.braindroid.nervecenter.visualization.rendering;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.braindroid.nervecenter.R;
import com.braindroid.nervecenter.utils.ViewFinder;

import org.rajawali3d.renderer.ISurfaceRenderer;
import org.rajawali3d.view.IDisplay;
import org.rajawali3d.view.ISurface;

public class WaveformRenderedView extends FrameLayout implements IDisplay {

    private ISurface rajaSurface;
    private ISurfaceRenderer surfaceRenderer;

    public WaveformRenderedView create(Context context, ViewGroup viewGroup) {
        WaveformRenderedView waveformRenderedView = (WaveformRenderedView)LayoutInflater
                .from(context).inflate(R.layout.waveform_rendered_view, viewGroup, false);

        return waveformRenderedView;
    }

    public WaveformRenderedView(Context context) {
        this(context, null);
    }

    public WaveformRenderedView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WaveformRenderedView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    private void init() {
        rajaSurface = ViewFinder.in(this, R.id.waveform_rendered_view_textureView);
    }


    @Override
    public ISurfaceRenderer createRenderer() {

        return null;
    }
}
