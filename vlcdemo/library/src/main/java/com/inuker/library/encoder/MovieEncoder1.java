package com.inuker.library.encoder;

import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;

import com.inuker.library.RGBProgram;
import com.inuker.library.utils.LogUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Created by liwentian on 2017/10/31.
 */

public class MovieEncoder1 extends BaseMovieEncoder {

    private volatile RGBProgram mRGBProgram;
    private volatile ByteBuffer mYUVBuffer;

    public MovieEncoder1(Context context, int width, int height) {
        super(context, width, height);
    }

    @Override
    public void onPrepareEncoder() {
        LogUtils.v(String.format("onPrepareEncoder width = %d, height = %d", mWidth, mHeight));
        mRGBProgram = new RGBProgram(mContext, mWidth, mHeight);
        mYUVBuffer = ByteBuffer.allocateDirect(mWidth * mHeight * 4)
                .order(ByteOrder.nativeOrder());
    }

    @Override
    public void onFrameAvailable(Object object, long timestamp) {
        byte[] data = (byte[]) object;

        if (mYUVBuffer == null) {
            return;
        }

//        LogUtils.v(String.format("onFrameAvailable: data = %d, buffer = %d", data.length, mYUVBuffer.capacity()));

        synchronized (mYUVBuffer) {
            mYUVBuffer.position(0);
            int len = Math.min(mYUVBuffer.capacity(), data.length);
            mYUVBuffer.put(data, 0, len);
        }
        mHandler.sendMessage(mHandler.obtainMessage(MSG_FRAME_AVAILABLE,
                (int) (timestamp >> 32), (int) timestamp));
    }

    @Override
    public void onFrameAvailable() {
        mRGBProgram.useProgram();

        synchronized (mYUVBuffer) {
            mRGBProgram.setUniforms(mYUVBuffer.array());
        }

        mRGBProgram.draw();
    }
}
