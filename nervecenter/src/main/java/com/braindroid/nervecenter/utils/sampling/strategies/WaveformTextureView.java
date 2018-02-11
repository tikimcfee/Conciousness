package com.braindroid.nervecenter.utils.sampling.strategies;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.util.AttributeSet;
import android.util.Log;
import android.view.TextureView;

import com.braindroid.nervecenter.visualization.WaveformCanvas;
import com.braindroid.nervecenter.visualization.interactive.ZoomableTextureView;

import timber.log.Timber;

public class WaveformTextureView extends ZoomableTextureView
        implements TextureView.SurfaceTextureListener,
        WaveformCanvas.CanvasSupplier {

    private WaveformCanvas waveformCanvas;
    private final Canvas EMPTY_CANVAS = new Canvas();

    public WaveformTextureView(Context context) {
        super(context);
        initView();
    }

    public WaveformTextureView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public WaveformTextureView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView();
    }

    private void initView() {
        setVerticalScaleEnabled(false);
        setHorizontalScaleEnabled(true);

        waveformCanvas = new WaveformCanvas(this);
    }

    public void RUN_TEST() {
        waveformCanvas.RUN_TEST();
    }

    @Override
    public void onNewTransform(Matrix transform) {
        super.onNewTransform(transform);
        waveformCanvas.RUN_TEST_PAN_ZOOM(transform);
    }

    //region Public Audio API

    private boolean hasData = false;
    public void setAudioDetails(short[] audioData, int sampleRate, int channels) {
        hasData = true;
        waveformCanvas.clearCaches();
        waveformCanvas.setAudioData(audioData, sampleRate, channels);
    }


    public void refresh() {
        // Update the display.

        Canvas canvas = lockCanvas();
        if (canvas != null) {
            drawWaveform(canvas);
            unlockCanvasAndPost(canvas);
        }
    }

    private void drawWaveform(Canvas canvas) {
        // Clear the screen each time because SurfaceView won't do this for us.
        canvas.drawColor(Color.parseColor("#009999"));
        waveformCanvas.updateCanvas(canvas);
    }

    //endregion

    //region SurfaceTexture Listener

    private int originalWidth = 0;
    private int originalHeight = 0;

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        originalWidth = width;
        originalHeight = height;
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        Log.v("WTV-C", "sizeChanged w=" + width + " h=" + height);
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

    //endregion

    @Override
    public Canvas acquireCanvas() {
        Canvas canvas = lockCanvas();
        if(canvas == null) {
            Timber.e("Failed to lock canvas; returning fallback canvas!");
            return EMPTY_CANVAS;
        }
        return canvas;
    }

    @Override
    public void postCanvas(Canvas canvas) {
        if(canvas == EMPTY_CANVAS) {
            Timber.w("Empty canvas was posted; dropping post entirely");
            return;
        }
        unlockCanvasAndPost(canvas);
    }
}
