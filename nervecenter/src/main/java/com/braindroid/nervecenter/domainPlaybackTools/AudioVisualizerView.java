package com.braindroid.nervecenter.domainPlaybackTools;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.media.audiofx.Visualizer;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AudioVisualizerView extends View {

    private static final int LINE_WIDTH = 2; // width of visualizer lines
    private static final int LINE_SCALE = 100; // scales visualizer lines
    private List<Float> amplitudes; // amplitudes for line lengths
    private int width; // width of this View
    private int height; // height of this View
    private Paint linePaint; // specifies line drawing characteristics

    public AudioVisualizerView(Context context) {
        super(context);
    }

    public AudioVisualizerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public AudioVisualizerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    // called when the dimensions of the View change
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        width = w; // new width of this View
        height = h; // new height of this View
        amplitudes = new ArrayList<>(width / LINE_WIDTH);
    }

    // add the given amplitude to the amplitudes ArrayList
    public void addAmplitude(float amplitude) {
        amplitudes.add(amplitude); // add newest to the amplitudes ArrayList

        // if the power lines completely fill the VisualizerView
        if (amplitudes.size() * LINE_WIDTH >= width) {
            amplitudes.remove(0); // remove oldest power value
        }
    }

    public void clear() {
        amplitudes.clear();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        setWillNotDraw(false);

        linePaint = new Paint(); // create Paint for lines
        linePaint.setColor(0x766A9E);
        linePaint.setStrokeWidth(LINE_WIDTH); // set stroke width
    }

    // draw the visualizer with scaled lines representing the amplitudes
    @Override
    public void onDraw(Canvas canvas) {
        if(isInEditMode()) {
            drawPreview(canvas);
            return;
        }

        int middle = height / 2; // get the middle of the View
        float curX = 0; // start curX at zero

        // for each item in the amplitudes ArrayList
        for (float power : amplitudes) {
            float scaledHeight = power / LINE_SCALE; // scale the power
            curX += LINE_WIDTH; // increase X by LINE_WIDTH

            // draw a line representing this item in the amplitudes ArrayList
            canvas.drawLine(curX, middle + scaledHeight / 2, curX, middle
                    - scaledHeight / 2, linePaint);
        }
    }

    final Random rand = new Random();
    private void drawPreview(Canvas canvas) {
        int middle = height / 2; // get the middle of the View
        float curX = 0; // start curX at zero
        int toDraw = width / LINE_WIDTH;
        float[] powers = new float[toDraw];
        for (int i = 0; i < toDraw; i++) {
            powers[i] = rand.nextFloat() * (rand.nextInt(20000) + 1);
        }

        // for each item in the amplitudes ArrayList

        for (int i = 0; i < powers.length; i++) {
            float scaledHeight = powers[i] / LINE_SCALE; // scale the power
            curX += LINE_WIDTH; // increase X by LINE_WIDTH

            // draw a line representing this item in the amplitudes ArrayList
            canvas.drawLine(curX, middle + scaledHeight / 2, curX, middle
                    - scaledHeight / 2, linePaint);
        }
    }

//    private void test() {
//        MediaPlayer p;
//        Visualizer visualizer = new Visualizer(p.getAudioSessionId());
//        visualizer.setDataCaptureListener(
//                new Visualizer.OnDataCaptureListener() {
//                    public void onWaveFormDataCapture(Visualizer visualizer,
//                                                      byte[] bytes, int samplingRate) {
//                        mVisualizerView.updateVisualizer(bytes);
//                    }
//
//                    public void onFftDataCapture(Visualizer visualizer,
//                                                 byte[] bytes, int samplingRate) {
//                    }
//                },
//                Visualizer.getMaxCaptureRate() / 2, true, false
//        );
//
//
//    }
}
