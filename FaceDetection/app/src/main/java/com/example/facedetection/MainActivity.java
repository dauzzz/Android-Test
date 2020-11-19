package com.example.facedetection;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Camera;
import android.graphics.ImageFormat;
import android.graphics.Point;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Size;
import android.view.Display;
import android.view.Surface;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private CameraManager cameraManager;
    private String cameraId;
    private CameraDevice cameraDevice;
    private ImageReader imageReader;
    private Surface surface;
    private Size inputSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setCamera();
        setImageReader();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        try {
            cameraManager.openCamera(cameraId, cameraCallback, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void setCamera(){
        cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            assert cameraManager != null;
            cameraId = cameraManager.getCameraIdList()[0];
            CameraCharacteristics cameraCharacteristics = cameraManager.getCameraCharacteristics(cameraId);
            StreamConfigurationMap map = cameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            Size[] inputCameraSize = map.getInputSizes(ImageFormat.YUV_420_888);
            inputSize = chooseInputSize(inputCameraSize);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void setImageReader(){
        imageReader = ImageReader.newInstance(inputSize.getWidth(),inputSize.getHeight(), ImageFormat.YUV_420_888, 3);
        imageReader.setOnImageAvailableListener(imageAvailableListener,null);
        surface = imageReader.getSurface();
    }

    private void setCameraCaptureSession(){
        try {
            final CaptureRequest.Builder captureRequest = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            captureRequest.set(CaptureRequest.CONTROL_MODE, CaptureRequest.CONTROL_MODE_AUTO);
            captureRequest.addTarget(surface);
            cameraDevice.createCaptureSession(Arrays.asList(surface), new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession session) {
                    try {
                        session.setRepeatingRequest(captureRequest.build(),null,null);
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession session) {

                }
            },null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private CameraDevice.StateCallback cameraCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            cameraDevice = camera;
            setCameraCaptureSession();
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice camera) {

        }

        @Override
        public void onError(@NonNull CameraDevice camera, int error) {

        }
    };

    private ImageReader.OnImageAvailableListener imageAvailableListener = new ImageReader.OnImageAvailableListener() {
        @Override
        public void onImageAvailable(ImageReader reader) {
            Image image = reader.acquireLatestImage();
            if(image == null){
                return;
            }
            Image.Plane[] planes = image.getPlanes();
            Log.i("imageReader", planes.length+" "+ image.getFormat()+ planes[0].getPixelStride());
        }
    };

    private Size chooseInputSize(Size[] inputCameraSize){
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        long screenArea = size.x*size.y;
        long compare = 0;
        long cornerstone = screenArea;
        Size mySize = null;
        for(Size s: inputCameraSize){
            long comparedBy_Area = s.getHeight()*s.getWidth();
            compare = Math.abs(comparedBy_Area - screenArea);
            if(compare <= cornerstone){
                mySize = s;
                cornerstone = compare;
            }
        }
        return mySize;
    }
}
