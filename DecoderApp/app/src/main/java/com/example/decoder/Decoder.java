package com.example.decoder;

import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.util.Log;
import android.view.Surface;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.BlockingQueue;

public class Decoder extends Thread {

    private MediaCodec mediaCodec;
    public final BlockingQueue<byte[]> sharedQ;
    private Surface decodedSurface;

    private int in = 0;

    protected Decoder(BlockingQueue<byte[]> sharedQ, Surface decodedSurface) {
        this.sharedQ = sharedQ;
        this.decodedSurface = decodedSurface;
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
        /*mediaCodec.setCallback(new MediaCodec.Callback() {
            @Override
            public void onInputBufferAvailable(@NonNull MediaCodec codec, int index) {
                Log.i("Async input", "Input Buffer Thread : "+ Thread.currentThread());
                final ByteBuffer byteBuffer = codec.getInputBuffer(index);
                assert byteBuffer != null;
                byteBuffer.clear();

                    try {
                        Log.i("sharedQOutput",sharedQ.size()+"");

                            byteBuffer.put((ByteBuffer) Objects.requireNonNull(sharedQ.take()));
                            Log.i("sharedQ output", "decode " + in);
                            in++;
                            codec.queueInputBuffer(index, 0, byteBuffer.capacity(), 66666, 0);


                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }


            }

            @Override
            public void onOutputBufferAvailable(@NonNull MediaCodec codec, int index, @NonNull MediaCodec.BufferInfo info) {
                codec.releaseOutputBuffer(index,true);
            }

            @Override
            public void onError(@NonNull MediaCodec codec, @NonNull MediaCodec.CodecException e) {

            }

            @Override
            public void onOutputFormatChanged(@NonNull MediaCodec codec, @NonNull MediaFormat format) {

            }
        });*/
        MediaFormat mediaFormat = MediaFormat.createVideoFormat(MediaFormat.MIMETYPE_VIDEO_AVC, 640, 480);
        mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE,4000000);
        mediaFormat.setInteger(MediaFormat.KEY_FRAME_RATE,15);
        mediaFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Flexible);
        mediaFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL,5);
        Log.i("create MediaFormat",mediaFormat.toString());
        mediaCodec.configure(mediaFormat,decodedSurface,null,0);
        mediaCodec.start();
        while (!Thread.interrupted()){
            Log.i("decoding", "start decode : "+ Thread.currentThread());
            int inputBufferId = mediaCodec.dequeueInputBuffer(-1);
            if(inputBufferId >= 0){
                ByteBuffer inputBuffer = mediaCodec.getInputBuffer(inputBufferId);
                assert inputBuffer != null;
                inputBuffer.clear();
                try {
                    Log.i("decoding", "Decode Thread : "+ Thread.currentThread());
                    ByteBuffer bbf = ByteBuffer.wrap(sharedQ.take());
                    //bbf.flip();
                    inputBuffer.put(bbf);
                    Log.i("decoding", "Successful putting the buffer " + inputBuffer);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mediaCodec.queueInputBuffer(inputBufferId,0, inputBuffer.capacity(), 66666, 0);
                Log.i("decoding", "Successful queueing the buffer " + inputBuffer);

            }
            MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
            Log.i("decoding", "Buffer info =" + bufferInfo);
            int outputBufferId = mediaCodec.dequeueOutputBuffer(bufferInfo, 0);
            Log.i("decoding", "output buffer id =" + outputBufferId);
            if(outputBufferId>=0){
                mediaCodec.releaseOutputBuffer(outputBufferId,true);
                Log.i("decoding", "successful in decoding to surface =" + outputBufferId);
            }
        }
    }

    public void releaseAll() {
        mediaCodec.stop();
        mediaCodec.release();
        decodedSurface.release();
    }
}
