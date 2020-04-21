package com.example.myutils.utils;

import android.graphics.SurfaceTexture;
import android.media.MediaCodec;
import android.media.MediaFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.myutils.AutoFitTextureView;
import com.example.myutils.R;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Objects;
import java.util.Queue;

public class ShowDecodedFragment extends Fragment implements TextureView.SurfaceTextureListener{

    private AutoFitTextureView textureView;

    public ShowDecodedFragment(Queue sharedQ) {
        this.sharedQ = sharedQ;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        in=0;
        return inflater.inflate(R.layout.show_decoded_layout, container, false);
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState){
        textureView = (AutoFitTextureView) view.findViewById(R.id.showTexture);
        textureView.setSurfaceTextureListener(this);
    }

    @Override
    public void onActivityCreated(final Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
    }
    //--------------------------------------------------------------------------------
    private int in;
    private Decoder decoder;
    private Surface outSurface;
    private final Queue sharedQ;
    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        Log.i("inputSurface","input surface created");
        final SurfaceTexture surfaceTexture = textureView.getSurfaceTexture();
        surfaceTexture.setDefaultBufferSize(1280,960);
        outSurface = new Surface(surfaceTexture);

        decoder = new Decoder(sharedQ) {
            @Override
            protected MediaCodec.Callback setCodecCallback() {
                return new MediaCodec.Callback() {
                    @Override
                    public void onInputBufferAvailable(@NonNull MediaCodec codec, int index) {
                        Log.i("Async input", "Input Buffer Thread : "+ Thread.currentThread());
                        final ByteBuffer byteBuffer = codec.getInputBuffer(index);
                        assert byteBuffer != null;
                        byteBuffer.clear();
                        synchronized (sharedQ) {
                            if (sharedQ.size() != 0) {
                                Log.i("sharedQ output","decode "+in);
                                byteBuffer.put((ByteBuffer) Objects.requireNonNull(sharedQ.poll()));
                                in++;
                            } /*else {
                                try {
                                    Log.i("sharedQ input","waiting");
                                    sharedQ.wait();
                                    Log.i("sharedQ input","end waiting");
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }*/
                            Log.i("infoInInputDecoder", Integer.toString(in));
                        }
                        codec.queueInputBuffer(index, 0, byteBuffer.capacity(), 40000, 0);
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
                };

            }

            @Override
            protected Surface setDecodedSurface() {
                return outSurface;
            }
        };
        decoder.start();
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        decoder.releaseAll();
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }
    //--------------------------------------------------------------------------------
}