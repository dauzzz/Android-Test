package com.example.decoder;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import java.nio.ByteBuffer;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@SuppressLint("Registered")
public class CombineED extends AppCompatActivity {


    public ShowDecodedFragment showDecodedFragment;
    private final BlockingQueue<byte[]> sharedQ = new LinkedBlockingQueue<>();

    @Override
    public void onCreate(Bundle s) {
        super.onCreate(s);
        setContentView(R.layout.activity_main);


        showDecodedFragment = new ShowDecodedFragment(sharedQ);
        FragmentTransaction transaction2 = getSupportFragmentManager().beginTransaction();
        transaction2.replace(R.id.playbackFragment, showDecodedFragment).addToBackStack(null).commit();
    }

}
