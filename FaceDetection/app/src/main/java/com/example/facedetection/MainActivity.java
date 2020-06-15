package com.example.facedetection;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Camera;
import android.graphics.ImageFormat;
import android.graphics.Point;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.ImageReader;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Size;
import android.view.Display;

public class MainActivity extends AppCompatActivity {

    private CameraManager cameraManager;
    private String cameraId;
    private ImageReader imageReader;
    private Size inputSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    private void setCamera(){
        cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            assert cameraManager != null;
            cameraId = cameraManager.getCameraIdList()[0];
            CameraCharacteristics cameraCharacteristics = cameraManager.getCameraCharacteristics(cameraId);
            StreamConfigurationMap map = cameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            Size[] inputCameraSize = map.getInputSizes(ImageFormat.YUV_420_888);
            inputSize =
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private Size chooseInputSize(Size[] inputCameraSize){
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        long area = size.x*size.y;
        for(Size s: inputCameraSize){
            long comparedArea = s.getHeight()*s.getWidth();
        }
    }
}
