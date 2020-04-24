package com.example.myutils.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.media.MediaCodec;
import android.media.MediaFormat;
import android.os.Bundle;
import android.util.Log;
import android.util.Size;
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
import java.util.concurrent.BlockingQueue;

public class ShowDecodedFragment extends Fragment implements TextureView.SurfaceTextureListener  {

    private AutoFitTextureView textureView;

    public ShowDecodedFragment(BlockingQueue<ByteBuffer> sharedQ) {
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
    private final BlockingQueue<ByteBuffer> sharedQ;
    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        Log.i("inputSurface","input surface created");
        //SetMatrix matrix = new SetMatrix( new Size(width,height),new Size(640,480));
        //textureView.setTransform(matrix.getMatrix());
        final SurfaceTexture surfaceTexture = textureView.getSurfaceTexture();
        surfaceTexture.setDefaultBufferSize(640,480);
        outSurface = new Surface(surfaceTexture);
        decoder = new Decoder(sharedQ, outSurface);
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