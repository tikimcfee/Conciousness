package com.braindroid.nervecenter.visualization;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

public class WaveformSurfaceView extends View
        implements SurfaceHolder.Callback,
        WaveformCanvas.CanvasSupplier {

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
        waveformCanvas = new WaveformCanvas(this);
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

    boolean testing = false;
    public void startTest() {
        testing = true;
        invalidate();
    }

    private Canvas myCanvas = null;
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        myCanvas = canvas;

        if(testing) {
            waveformCanvas.foo(canvas);
        } else {
            canvas.drawColor(Color.parseColor("#009999"));
            waveformCanvas.updateCanvas(canvas);
        }
    }

    //region Canvas supplier
    @Override
    public Canvas acquireCanvas() {
        return myCanvas;
    }

    @Override
    public void postCanvas(Canvas canvas) {
        if(canvas == null) {
            testing = false;
        } else {
            invalidate();
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
        invalidate();
    }

    private void drawWaveform(Canvas canvas) {
        // Clear the screen each time because SurfaceView won't do this for us.

    }
}
