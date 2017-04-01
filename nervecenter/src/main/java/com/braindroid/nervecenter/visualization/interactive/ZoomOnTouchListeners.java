package com.braindroid.nervecenter.visualization.interactive;

import android.graphics.Matrix;
import android.graphics.PointF;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.TextureView;
import android.view.View;

class ZoomOnTouchListeners implements View.OnTouchListener, ZoomState {

    private float minScale = 1f;
    private float maxScale = 5f;
    private float saveScale = 1f;

    private boolean horizontalScaleEnabled = false;
    private boolean verticalScaleEnabled = false;

    private static final int NONE = 0;
    private static final int DRAG = 1;
    private static final int ZOOM = 2;
    private int mode = NONE;

    private final Matrix textureTransformMatrix = new Matrix();
    private final float[] matrixValueBuffer = new float[9];
    private ScaleGestureDetector mScaleDetector;

    private final PointF last = new PointF();
    private final PointF start = new PointF();
    private float right, bottom;

    private final TextureView textureView;

    public ZoomOnTouchListeners(ZoomableTextureView zoomableTextureView) {
        textureView = zoomableTextureView;
        mScaleDetector = new ScaleGestureDetector(zoomableTextureView.getContext(), new ScaleListener());
    }

    //region Zoom State

    @Override
    public void setMinScale(float scale) {
        if (scale < 1.0f || scale > maxScale)
            throw new RuntimeException("minScale can't be lower than 1 or larger than maxScale(" + maxScale + ")");
        else minScale = scale;
    }

    @Override
    public void setMaxScale(float scale) {
        if (scale < 1.0f || scale < minScale)
            throw new RuntimeException("maxScale can't be lower than 1 or minScale(" + minScale + ")");
        else minScale = scale;
    }

    @Override
    public float getMinScale() {
        return minScale;
    }

    @Override
    public float getMaxScale() {
        return maxScale;
    }

    @Override
    public void setHorizontalScaleEnabled(boolean enabled) {
        this.horizontalScaleEnabled = enabled;
    }

    @Override
    public void setVerticalScaleEnabled(boolean enabled) {
        this.verticalScaleEnabled = enabled;
    }

    //endregion

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {

        mScaleDetector.onTouchEvent(motionEvent);

        textureTransformMatrix.getValues(matrixValueBuffer);
        float x = matrixValueBuffer[Matrix.MTRANS_X];
        float y = matrixValueBuffer[Matrix.MTRANS_Y];
        PointF curr = new PointF(motionEvent.getX(), motionEvent.getY());

        switch (motionEvent.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                last.set(motionEvent.getX(), motionEvent.getY());
                start.set(last);
                mode = DRAG;
                break;
            case MotionEvent.ACTION_UP:
                mode = NONE;
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                last.set(motionEvent.getX(), motionEvent.getY());
                start.set(last);
                mode = ZOOM;
                break;
            case MotionEvent.ACTION_MOVE:
                if (mode == ZOOM || (mode == DRAG && saveScale > minScale)) {
                    float deltaX = curr.x - last.x;// x difference
                    float deltaY = curr.y - last.y;// y difference
                    if (y + deltaY > 0)
                        deltaY = -y;
                    else if (y + deltaY < -bottom)
                        deltaY = -(y + bottom);

                    if (x + deltaX > 0)
                        deltaX = -x;
                    else if (x + deltaX < -right)
                        deltaX = -(x + right);
                    textureTransformMatrix.postTranslate(deltaX, deltaY);
                    last.set(curr.x, curr.y);
                }
                break;
            case MotionEvent.ACTION_POINTER_UP:
                mode = NONE;
                break;
        }
        textureView.setTransform(textureTransformMatrix);
        textureView.invalidate();
        return true;
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {

        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            mode = ZOOM;
            return true;
        }

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            float newScaleFactor = detector.getScaleFactor();
            float lastScaleFactor = saveScale;
            saveScale *= newScaleFactor;
            if (saveScale > maxScale) {
                saveScale = maxScale;
                newScaleFactor = maxScale / lastScaleFactor;
            } else if (saveScale < minScale) {
                saveScale = minScale;
                newScaleFactor = minScale / lastScaleFactor;
            }

            float scaleX = newScaleFactor, scaleY = newScaleFactor;
            final int width = textureView.getWidth();
            final int height = textureView.getHeight();

            if(!horizontalScaleEnabled) {
                scaleX = 1;
            } else {
                right = width * saveScale - width;
            }

            if(!verticalScaleEnabled) {
                scaleY = 1;
            } else {
                bottom = height * saveScale - height;
            }

            final float x = matrixValueBuffer[Matrix.MTRANS_X];
            final float y = matrixValueBuffer[Matrix.MTRANS_Y];

            textureTransformMatrix.postScale(scaleX, scaleY, detector.getFocusX(), detector.getFocusY());
            textureTransformMatrix.getValues(matrixValueBuffer);

            if (0 <= width || 0 <= height) {
                if (newScaleFactor < 1) {
                    if (0 < width) {
                        if (y < -bottom)
                            textureTransformMatrix.postTranslate(0, -(y + bottom));
                        else if (y > 0)
                            textureTransformMatrix.postTranslate(0, -y);
                    } else {
                        if (x < -right)
                            textureTransformMatrix.postTranslate(-(x + right), 0);
                        else if (x > 0)
                            textureTransformMatrix.postTranslate(-x, 0);
                    }
                }
            } else {
                if (newScaleFactor < 1) {
                    if (x < -right)
                        textureTransformMatrix.postTranslate(-(x + right), 0);
                    else if (x > 0)
                        textureTransformMatrix.postTranslate(-x, 0);
                    if (y < -bottom)
                        textureTransformMatrix.postTranslate(0, -(y + bottom));
                    else if (y > 0)
                        textureTransformMatrix.postTranslate(0, -y);
                }
            }
            return true;
        }
    }
}
