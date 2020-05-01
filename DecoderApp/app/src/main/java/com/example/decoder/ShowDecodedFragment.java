package com.example.decoder;

import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import java.nio.ByteBuffer;
import java.util.concurrent.BlockingQueue;

public class ShowDecodedFragment extends Fragment implements TextureView.SurfaceTextureListener  {

    private AutoFitTextureView textureView;

    public ShowDecodedFragment(BlockingQueue<byte[]> sharedQ) {
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
    private final BlockingQueue<byte[]> sharedQ;
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
        Log.i("MQTT","start subscribe");
        MQTT mqtt = new MQTT(sharedQ);
        mqtt.startSub();
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