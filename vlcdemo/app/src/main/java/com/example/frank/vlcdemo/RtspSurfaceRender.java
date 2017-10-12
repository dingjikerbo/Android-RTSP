package com.example.frank.vlcdemo;

import android.graphics.Bitmap;
import android.opengl.GLSurfaceView;

import com.inuker.library.BitmapUtils;
import com.inuker.library.RGBProgram;
import com.inuker.library.TaskUtils;
import com.inuker.library.utils.ImageUtils;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_DEPTH_BUFFER_BIT;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;

/**
 * Created by liwentian on 2017/10/12.
 */

public class RtspSurfaceRender implements GLSurfaceView.Renderer, RtspHelper.RtspCallback {

    private ByteBuffer mBuffer;

    private GLSurfaceView mGLSurfaceView;

    private RGBProgram mProgram;

    private String mRtspUrl;

    private volatile boolean mCapturePending;
    private CaptureCallback mCaptureCallback;

    public RtspSurfaceRender(GLSurfaceView glSurfaceView) {
        mGLSurfaceView = glSurfaceView;
    }

    public void setRtspUrl(String url) {
        mRtspUrl = url;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        mProgram = new RGBProgram(mGLSurfaceView.getContext(), width, height);
        mBuffer = ByteBuffer.allocateDirect(width * height * 4).order(ByteOrder.nativeOrder());
        RtspHelper.getInstance().createPlayer(mRtspUrl, width, height, this);
    }

    public void onSurfaceDestoryed() {
        RtspHelper.getInstance().releasePlayer();
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glClearColor(1f, 1f, 1f, 1f);

        mProgram.useProgram();
        mProgram.setUniforms(mBuffer.array());
        mProgram.draw();
    }

    @Override
    public void onPreviewFrame(ByteBuffer buffer, int width, int height) {
        mBuffer.rewind();

        buffer.rewind();
        mBuffer.put(buffer);

        if (mCapturePending) {
            mCapturePending = false;
            onCapture(width, height);
        }

        mGLSurfaceView.requestRender();
    }

    private void onCapture(int width, int height) {
        final Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        mBuffer.rewind();
        bmp.copyPixelsFromBuffer(mBuffer);
        TaskUtils.execute(new Runnable() {
            @Override
            public void run() {
                File file = ImageUtils.getNewImageFile();
                BitmapUtils.saveBitmap(bmp, file);
                BitmapUtils.recycle(bmp);
                mCaptureCallback.onCapture(file);
                mCaptureCallback = null;
            }
        });
    }

    public interface CaptureCallback {
        void onCapture(File path);
    }

    public void capture(CaptureCallback callback) {
        mCaptureCallback = callback;
        mCapturePending = true;
    }
}
