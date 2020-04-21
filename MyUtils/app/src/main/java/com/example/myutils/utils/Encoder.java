package com.example.myutils.utils;

import android.graphics.Bitmap;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.os.Build;
import android.util.Log;
import android.view.Surface;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Queue;

public abstract class Encoder extends Thread {

    private MediaCodec mediaCodec;
    private Surface surface;

    @Override
    public void run(){
        try{
            setEncoder();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setEncoder() throws IOException {
        Log.i("set encode", "Encode Thread : "+ Thread.currentThread());
        mediaCodec = MediaCodec.createEncoderByType(MediaFormat.MIMETYPE_VIDEO_AVC);
        mediaCodec.setCallback(setCodecCallback());
        MediaFormat mediaFormat = MediaFormat.createVideoFormat(MediaFormat.MIMETYPE_VIDEO_AVC, 1280, 960);
        mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE,1280000);
        mediaFormat.setInteger(MediaFormat.KEY_FRAME_RATE,30);
        mediaFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface);
        mediaFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL,1);
        mediaCodec.configure(mediaFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            surface = mediaCodec.createInputSurface();
            Log.i("encoderSurface",surface.toString());
        }
        mediaCodec.start();
    }
    public Surface getSurface(){
        return surface;
    }


    public void releaseAll() {
        mediaCodec.release();
        surface.release();
    }

    protected abstract MediaCodec.Callback setCodecCallback();
}
