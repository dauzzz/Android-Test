package com.example.myutils.utils;

import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Surface;

import java.io.IOException;

public abstract class Decoder {

    private MediaCodec mediaCodec;


    public void setDecoder() throws IOException{
        mediaCodec = MediaCodec.createDecoderByType(MediaFormat.MIMETYPE_VIDEO_AVC);
        mediaCodec.setCallback(setCodecCallback());
        MediaFormat mediaFormat = MediaFormat.createVideoFormat(MediaFormat.MIMETYPE_VIDEO_AVC, 640, 480);
        mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE,1216000);
        mediaFormat.setInteger(MediaFormat.KEY_FRAME_RATE,30);
        mediaFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface);
        mediaFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL,15);
        Log.i("create MediaFormat",mediaFormat.toString());
        mediaCodec.configure(mediaFormat,setDecodedSurface(),null,0);
    }

    public void startDecoderCodec(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                mediaCodec.start();
            }
        }).start();
    }

    protected abstract MediaCodec.Callback setCodecCallback();

    protected abstract Surface setDecodedSurface();

    public void releaseAll() {
        mediaCodec.release();
        setDecodedSurface().release();
    }
}
