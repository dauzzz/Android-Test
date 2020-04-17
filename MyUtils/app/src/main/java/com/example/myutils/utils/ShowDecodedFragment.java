package com.example.myutils.utils;

import android.graphics.SurfaceTexture;
import android.media.MediaCodec;
import android.media.MediaFormat;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.myutils.AutoFitTextureView;
import com.example.myutils.R;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class ShowDecodedFragment extends Fragment implements TextureView.SurfaceTextureListener {

    private AutoFitTextureView textureView;
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
    private OnInputBufferAvailableFromEncoder onInputBufferAvailableFromEncoder;
    private Decoder decoder;
    private Surface outSurface;
    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        Log.i("inputSurface","input surface created");
        SurfaceTexture surfaceTexture = textureView.getSurfaceTexture();
        surfaceTexture.setDefaultBufferSize(480,640);
        outSurface = new Surface(surfaceTexture);

        decoder = new Decoder() {
            @Override
            protected MediaCodec.Callback setCodecCallback() {
                return new MediaCodec.Callback() {
                    @Override
                    public void onInputBufferAvailable(@NonNull MediaCodec codec, int index) {
                        final ByteBuffer byteBuffer = codec.getInputBuffer(index);
                        assert byteBuffer != null;
                        Log.i("input", byteBuffer.toString());
                        CameraFragment.OutBuffer outBuffer = new CameraFragment.OutBuffer() {
                            @Override
                            public void outputBufferIs(byte[] b) {
                                //Log.i("input", b.toString());
                                //byteBuffer.put(b);
                            }
                        };
                        //if(onInputBufferAvailableFromEncoder.getByteBuffer(in) != null) {
                        //byteBuffer.put(onInputBufferAvailableFromEncoder.getByteBuffer(in));
                        in++;
                        Log.i("infoInInputDecoder",Integer.toString(in));
                        codec.queueInputBuffer(index,0,byteBuffer.capacity(), 40000,0);
                        //}
                    }

                    @Override
                    public void onOutputBufferAvailable(@NonNull MediaCodec codec, int index, @NonNull MediaCodec.BufferInfo info) {

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
        try {
            decoder.setDecoder();
        } catch (IOException e) {
            e.printStackTrace();
        }
        decoder.startDecoderCodec();
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
    public interface OnInputBufferAvailableFromEncoder{
        //@Nullable public ByteBuffer getByteBuffer(int in);//{return cameraFragment.getEncodedOutputBuffer(in)}
        public ArrayList<ByteBuffer> getByteList();
        //public int getOrder(int in);
    }
    //--------------------------------------------------------------------------------
    public int getListIndex(){return in;}
    //--------------------------------------------------------------------------------
    public void startDecode(){
        decoder.startDecoderCodec();
    }
}