package com.braindroid.nervecenter.visualization;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.MotionEvent;
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
    public void RUN_TEST() {
        waveformCanvas.RUN_TEST();
    }

    private boolean hasData = false;
    public void setAudioDetails(short[] audioData, int sampleRate, int channels) {
        hasData = true;
        waveformCanvas.clearCaches();
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

    private void drawWaveform(Canvas canvas) {
        // Clear the screen each time because SurfaceView won't do this for us.
        canvas.drawColor(Color.parseColor("#009999"));
        waveformCanvas.updateCanvas(canvas);
    }

    boolean trackingFinger = false;

    float startingXPosition = 0;
    float currentScrollPosition = 0;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_DOWN) {
            trackingFinger = true;
            startingXPosition = event.getX();
        } else if(event.getAction() == MotionEvent.ACTION_UP) {
            trackingFinger = false;
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {

            float thisMovementPosition = event.getX();

            /*
            the translation value is where you started, minus where you are now - that's the difference.
            x1 == 10, x2 == 5 -> [5 units translated]

            we invert that to say
                if you moved +5 units, we need to move our canvas 5 units TO THE LEFT
                if you moved -5 units, we need to move our canvas 5 units TO THE RIGHT
            */
            float translation = (startingXPosition - thisMovementPosition) * -1;

            /*
            we have our translation amount. add that amount to the current position.
             */
            currentScrollPosition += translation;

            /* if we end up with a scroll position >= 0; we've reached the farthest left, so just reset */
            currentScrollPosition = currentScrollPosition >= 0 ? 0 : currentScrollPosition;

//            waveformCanvas.TEST_DRAW_TRANSLATION((int)currentScrollPosition);
        }

        return true;
    }
}
