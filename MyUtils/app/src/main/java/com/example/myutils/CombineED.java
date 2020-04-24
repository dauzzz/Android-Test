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
import com.example.myutils.utils.Decoder;
import com.example.myutils.utils.ShowDecodedFragment;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@SuppressLint("Registered")
public class CombineED extends AppCompatActivity {

    public CameraFragment cameraFragment;
    public ShowDecodedFragment showDecodedFragment;
    private final BlockingQueue<ByteBuffer> sharedQ = new LinkedBlockingQueue<>();

    @Override
    public void onCreate(Bundle s) {
        super.onCreate(s);
        setContentView(R.layout.new_layout);

        cameraFragment = new CameraFragment(sharedQ);
        showDecodedFragment = new ShowDecodedFragment(sharedQ);
        FragmentTransaction transaction1 = getSupportFragmentManager().beginTransaction();
        transaction1.replace(R.id.cameraFragment, cameraFragment).addToBackStack(null).commit();
        FragmentTransaction transaction2 = getSupportFragmentManager().beginTransaction();
        transaction2.replace(R.id.playbackFragment, showDecodedFragment).addToBackStack(null).commit();
    }

}
