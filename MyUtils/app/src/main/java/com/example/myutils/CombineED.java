package com.example.myutils;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.example.myutils.utils.CameraFragment;
import com.example.myutils.utils.ShowDecodedFragment;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

@SuppressLint("Registered")
public class CombineED extends AppCompatActivity implements ShowDecodedFragment.OnInputBufferAvailableFromEncoder,CameraFragment.SetOnCameraOk {

    CameraFragment cameraFragment;
    ShowDecodedFragment showDecodedFragment;
    Button button;
    int flag = 1;

    @Override
    public void onCreate(Bundle s) {
        super.onCreate(s);
        setContentView(R.layout.new_layout);

        button = findViewById(R.id.button);
        button.setOnClickListener(onClickListener);

        cameraFragment = new CameraFragment();
        showDecodedFragment = new ShowDecodedFragment();
        FragmentTransaction transaction1 = getSupportFragmentManager().beginTransaction();
        transaction1.replace(R.id.cameraFragment,cameraFragment).addToBackStack(null).commit();
        FragmentTransaction transaction2 = getSupportFragmentManager().beginTransaction();
        transaction2.replace(R.id.playbackFragment,showDecodedFragment).addToBackStack(null).commit();
    }

    @Override
    @Nullable
    //public ByteBuffer getByteBuffer(int in) {
    public ArrayList<ByteBuffer> getByteList(){
        //return cameraFragment.getEncodedOutputBuffer(in);
        return cameraFragment.getEncodedList();
    }

    @Override
    public void setDecoder() {
        showDecodedFragment.startDecode();
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(flag == 1) {
                showDecodedFragment.startDecode();
                flag = 0;
            }
        }
    };
}
