package com.example.frank.vlcdemo;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import org.videolan.libvlc.IVLCVout;
import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaPlayer;

import java.util.ArrayList;

public class MainActivity extends Activity implements SurfaceHolder.Callback {

    private static final String URL = "rtsp://192.168.10.61:8554/test.mkv";

    private SurfaceView mSurfaceView;

    private LibVLC mVlc;

    private MediaPlayer mMediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSurfaceView = findViewById(R.id.surface);
        mSurfaceView.getHolder().addCallback(this);
    }

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

            // Set up video output
            final IVLCVout vout = mMediaPlayer.getVLCVout();
            vout.setVideoView(mSurfaceView);
            vout.attachViews();

            vout.setWindowSize(1920, 1080);

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
