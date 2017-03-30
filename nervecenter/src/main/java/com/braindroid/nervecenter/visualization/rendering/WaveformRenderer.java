package com.braindroid.nervecenter.visualization.rendering;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.MotionEvent;

import org.rajawali3d.Object3D;
import org.rajawali3d.lights.ALight;
import org.rajawali3d.lights.DirectionalLight;
import org.rajawali3d.materials.textures.Texture;
import org.rajawali3d.math.Matrix4;
import org.rajawali3d.primitives.Line3D;
import org.rajawali3d.renderer.Renderer;
import org.rajawali3d.util.ObjectColorPicker;
import org.rajawali3d.util.OnObjectPickedListener;

import javax.microedition.khronos.opengles.GL10;

public class WaveformRenderer extends Renderer implements OnObjectPickedListener {

    private int[] viewport;

    private Matrix4 viewMatrix;
    private Matrix4 projectionMatrix;

    private ObjectColorPicker mPicker;

    public WaveformRenderer(Context context) {
        super(context);

    }

    @Override
    protected void initScene() {
        getCurrentCamera().setPosition(0, 0, 10);
        getCurrentCamera().setLookAt(0, 0, 0);

        viewport = new int[] { 0, 0, getViewportWidth(), getViewportHeight() };
        viewMatrix = getCurrentCamera().getViewMatrix();
        projectionMatrix = getCurrentCamera().getProjectionMatrix();

        mPicker = new ObjectColorPicker(this);
        mPicker.setOnObjectPickedListener(this);

        Texture texture = new Texture("Waveform");



    }

    @Override
    public void onRenderSurfaceSizeChanged(GL10 gl, int width, int height) {
        super.onRenderSurfaceSizeChanged(gl, width, height);
        viewport[2] = getViewportWidth();
        viewport[3] = getViewportHeight();
        viewMatrix = getCurrentCamera().getViewMatrix();
        projectionMatrix = getCurrentCamera().getProjectionMatrix();
    }

    @Override
    public void onOffsetsChanged(float xOffset, float yOffset, float xOffsetStep, float yOffsetStep, int xPixelOffset, int yPixelOffset) {

    }

    @Override
    public void onTouchEvent(MotionEvent event) {

    }

    //region Object Picker

    @Override
    public void onObjectPicked(@NonNull Object3D object) {

    }

    @Override
    public void onNoObjectPicked() {

    }

    //endregion
}
