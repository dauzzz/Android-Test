package com.example.myutils.utils;

import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.os.Build;
import android.util.Log;
import android.view.Surface;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;

public class Encoder extends Thread {

    private MediaCodec mediaCodec;
    private Surface surface;

    private final BlockingQueue<ByteBuffer> sharedQ;

    private int i = 0;

    public Encoder(BlockingQueue<ByteBuffer> sharedQ) {
        this.sharedQ = sharedQ;
    }

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
        mediaCodec.setCallback(new MediaCodec.Callback() {
            @Override
            public void onInputBufferAvailable(@NonNull MediaCodec codec, int index) {

            }

            @Override
            public void onOutputBufferAvailable(@NonNull MediaCodec codec, int index, @NonNull MediaCodec.BufferInfo info) {
                Log.i("Async output", "Output Buffer Thread : "+ Thread.currentThread());

                    try {
                        sharedQ.put(Objects.requireNonNull(codec.getOutputBuffer(index)));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Log.i("sharedQ input","encode "+sharedQ.size());
                    i++;
                    //sharedQ.notify();
                    codec.releaseOutputBuffer(index, false);

            }

            @Override
            public void onError(@NonNull MediaCodec codec, @NonNull MediaCodec.CodecException e) {

            }

            @Override
            public void onOutputFormatChanged(@NonNull MediaCodec codec, @NonNull MediaFormat format) {

            }
        });
        MediaFormat mediaFormat = MediaFormat.createVideoFormat(MediaFormat.MIMETYPE_VIDEO_AVC, 640, 480);
        mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE,4000000);
        mediaFormat.setInteger(MediaFormat.KEY_FRAME_RATE,15);
        mediaFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface);
        mediaFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL,5);
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
        mediaCodec.stop();
        mediaCodec.release();
        surface.release();
    }


}
