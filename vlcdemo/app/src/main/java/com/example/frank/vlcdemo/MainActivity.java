package com.example.frank.vlcdemo;

import android.app.Activity;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.ImageView;

import org.videolan.libvlc.IVLCVout;
import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaPlayer;
import org.videolan.libvlc.MediaPlayCallback;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;

public class MainActivity extends Activity implements SurfaceHolder.Callback {

//    private static final String URL = "rtsp://192.168.1.100:8554/test.mkv";
    public static final String URL = "rtsp://admin:admin123@10.31.11.79:554/cam/realmonitor?channel=1@subtype=0";

    private SurfaceView mSurfaceView;

    private LibVLC mVlc;

    private MediaPlayer mMediaPlayer;

    private ImageView mImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSurfaceView = findViewById(R.id.surface);
        mSurfaceView.getHolder().addCallback(this);

        mImage = findViewById(R.id.image);
    }

    private Bitmap bufferToBitmap(ByteBuffer buffer) {
        final Bitmap bmp = Bitmap.createBitmap(960, 540, Bitmap.Config.ARGB_8888);
        buffer.rewind();
        bmp.copyPixelsFromBuffer(buffer);

//        Matrix matrix = new Matrix();
//        matrix.postScale(360f / bmp.getWidth(), 640f / bmp.getHeight());
//        matrix.postRotate(180);
//        Bitmap dstbmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, false);
//
//        BitmapUtils.recycle(bmp);

        return bmp;
    }

    private Bitmap mBitmap;

    private ByteBuffer mBuffer = ByteBuffer.allocateDirect(960 * 540 * 4).order(ByteOrder.nativeOrder());

    private void createPlayer() {
        releasePlayer();

        try {
            ArrayList<String> options = new ArrayList<String>();
            options.add("--aout=opensles");
            options.add("--audio-time-stretch"); // time stretching
            options.add("-vvv"); // verbosity
            mVlc = new LibVLC(this, options);


            // Create media player
            mMediaPlayer = new MediaPlayer(mVlc);
            mMediaPlayer.setVideoFormat("RGBA", 960, 540, 960 * 4);
//            mMediaPlayer.setScale(0.75f);

            mMediaPlayer.setVideoCallback(mBuffer, new MediaPlayCallback() {
                @Override
                public void onDisplay(ByteBuffer buffer) {
                    Log.v("bush", "called here");

                    buffer.rewind();

                    if (mBitmap != null) {
                        mBitmap.recycle();
                    }
                    mBitmap = bufferToBitmap(buffer);
                    mImage.post(new Runnable() {
                        @Override
                        public void run() {
                            Log.v("bush", String.format("bitmap size = %d,%d", mBitmap.getWidth(), mBitmap.getHeight()));
                            mImage.setImageBitmap(mBitmap);
                        }
                    });

                }
            });


            // Set up video output
//            final IVLCVout vout = mMediaPlayer.getVLCVout();
//            vout.setVideoView(mSurfaceView);
//            vout.attachViews();

//            vout.setWindowSize(1920, 1080);

            Media m = new Media(mVlc, Uri.parse(URL));


            int cache = 1000;
            m.addOption(":network-caching=" + cache);
            m.addOption(":file-caching=" + cache);
            m.addOption(":live-cacheing=" + cache);
            m.addOption(":sout-mux-caching=" + cache);
            m.addOption(":codec=mediacodec,iomx,all");
            mMediaPlayer.setMedia(m);
            mMediaPlayer.play();
        } catch (Throwable e) {
        }
    }

    private void releasePlayer() {
        if (mVlc == null) {
            return;
        }

        mMediaPlayer.stop();
        final IVLCVout vout = mMediaPlayer.getVLCVout();
        vout.detachViews();

        mVlc.release();
        mVlc = null;
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        createPlayer();
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        releasePlayer();
    }
}
