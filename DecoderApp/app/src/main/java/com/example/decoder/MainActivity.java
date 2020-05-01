package com.example.decoder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class MainActivity extends AppCompatActivity {

    public ShowDecodedFragment showDecodedFragment;
    private final BlockingQueue<byte[]> sharedQ = new LinkedBlockingQueue<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        showDecodedFragment = new ShowDecodedFragment(sharedQ);
        FragmentTransaction transaction2 = getSupportFragmentManager().beginTransaction();
        transaction2.replace(R.id.playbackFragment, showDecodedFragment).addToBackStack(null).commit();
    }
}
