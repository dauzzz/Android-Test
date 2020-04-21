package com.example.myutils.utils;

import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.util.Log;
import android.view.Surface;

import java.io.IOException;
import java.util.Queue;

public abstract class Decoder extends Thread {

    private MediaCodec mediaCodec;
    public final Queue sharedQ;

    protected Decoder(Queue sharedQ) {
        this.sharedQ = sharedQ;
    }

    @Override
    public void run(){
        try {
            setDecoder();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setDecoder() throws IOException{
        Log.i("set decode", "Decode Thread : "+ Thread.currentThread());
        mediaCodec = MediaCodec.createDecoderByType(MediaFormat.MIMETYPE_VIDEO_AVC);
        mediaCodec.setCallback(setCodecCallback());
        MediaFormat mediaFormat = MediaFormat.createVideoFormat(MediaFormat.MIMETYPE_VIDEO_AVC, 1280, 960);
        mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE,1280000);
        mediaFormat.setInteger(MediaFormat.KEY_FRAME_RATE,30);
        mediaFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Flexible);
        mediaFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL,1);
        Log.i("create MediaFormat",mediaFormat.toString());
        mediaCodec.configure(mediaFormat,setDecodedSurface(),null,0);
        mediaCodec.start();
    }


    protected abstract MediaCodec.Callback setCodecCallback();

    protected abstract Surface setDecodedSurface();

    public void releaseAll() {
        mediaCodec.release();
        setDecodedSurface().release();
    }
}
