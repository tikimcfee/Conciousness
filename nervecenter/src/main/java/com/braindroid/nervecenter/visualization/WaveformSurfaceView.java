package com.braindroid.nervecenter.visualization;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class WaveformSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

    private WaveformCanvas waveformCanvas;

    //region Constructor / init

    public WaveformSurfaceView(Context context) {
        this(context, null);
    }

    public WaveformSurfaceView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WaveformSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

    private void init() {
        waveformCanvas = new WaveformCanvas(new WaveformCanvas.CanvasSupplier() {
            @Override
            public Canvas acquireCanvas() {
                return getHolder().lockCanvas();
            }

            @Override
            public void postCanvas(Canvas canvas) {
                getHolder().unlockCanvasAndPost(canvas);
            }
        });
        getHolder().addCallback(this);
    }

    //endregion

    //region Base holder callback

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        repaintOnCallback(holder);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        repaintOnCallback(holder);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    private void repaintOnCallback(SurfaceHolder holder) {
        Canvas canvas = holder.lockCanvas();
        if(canvas == null) {
            return;
        }
        canvas.drawColor(Color.parseColor("#009999"));
        holder.unlockCanvasAndPost(canvas);

        if(hasData) {
            refresh();
        }
    }


    //endregion

    private boolean hasData = false;
    public void setAudioDetails(short[] audioData, int sampleRate, int channels) {
        hasData = true;
        waveformCanvas.setAudioData(audioData, sampleRate, channels);
    }

    public void refresh() {
        // Update the display.
        Canvas canvas = getHolder().lockCanvas();
        if (canvas != null) {
            drawWaveform(canvas);
            getHolder().unlockCanvasAndPost(canvas);
        }
    }

    public void RUN_TEST() {
        waveformCanvas.RUN_TEST();
    }

    private void drawWaveform(Canvas canvas) {
        // Clear the screen each time because SurfaceView won't do this for us.
        canvas.drawColor(Color.parseColor("#009999"));

//        float width = getWidth();
//        float height = getHeight();
//        float centerY = height / 2;

        waveformCanvas.updateCanvas(canvas);
    }
}
