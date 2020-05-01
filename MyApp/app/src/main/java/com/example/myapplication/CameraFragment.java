package com.example.myapplication;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.myapplication.AutoFitTextureView;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;

// the problem when we use interface is,

public class CameraFragment extends Fragment implements TextureView.SurfaceTextureListener {

    private AutoFitTextureView textureView;

    private final BlockingQueue<byte[]> sharedQ;

    public CameraFragment(BlockingQueue<byte[]> sharedQ) {
        this.sharedQ = sharedQ;
    }

    int i = 0;

    private Button button;

    private MQTT mqtt;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        return inflater.inflate(R.layout.camera_fragment_layout, container, false);
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState){
        textureView = (AutoFitTextureView) view.findViewById(R.id.cameraView);
        textureView.setAspectRatio(480,640);
        textureView.setSurfaceTextureListener(this);
        button = (Button) view.findViewById(R.id.buttonId);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mqtt.start();
            }
        });
        try {
            startCameraHandler();
            setCameraSettings();
            encoder = new Encoder(sharedQ);
            encoder.start();
            mqtt = new MQTT(sharedQ);
        } catch (InterruptedException | CameraAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityCreated(final Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume (){
        super.onResume();
    }

    //--------------------------------------------------------------------------------
    private HandlerThread cameraThread;
    private Handler cameraHandler;
    private void startCameraHandler() throws InterruptedException {
        stopCameraThread();
        cameraThread = new HandlerThread("CameraThread");
        cameraThread.start();
        cameraHandler = new Handler(cameraThread.getLooper());
    }
    private void stopCameraThread() throws InterruptedException {
        if(cameraThread == null)
            return;
        cameraThread.quitSafely();
        cameraThread.join();
        cameraThread = null;
        cameraHandler = null;
    }
    //--------------------------------------------------------------------------------
    private CameraManager cameraManager;
    private CameraCaptureSession cameraCaptureSession;
    private String cameraId;
    private static final String LOGTAG = "CameraFragment";
    private void setCameraSettings() throws CameraAccessException {
        cameraManager = (CameraManager) Objects.requireNonNull(getActivity()).getSystemService(Context.CAMERA_SERVICE);
        assert cameraManager != null;
        String[] cameraIdList = cameraManager.getCameraIdList();
        int cameraIdNum = 0;
        for(String id : cameraIdList){
            Log.i(LOGTAG, "Camera Id Num= " + Integer.toString(cameraIdNum) + ", Id= " + id);
        }
        cameraId = cameraIdList[0];
        CameraCharacteristics cameraCharacteristics = cameraManager.getCameraCharacteristics(cameraId);
        StreamConfigurationMap streamConfigurationMap = cameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
        assert streamConfigurationMap != null;
        Size[] inputCameraSize = streamConfigurationMap.getOutputSizes(ImageFormat.YUV_420_888);
    }
    //--------------------------------------------------------------------------------
    private static final int CAMERA_REQUEST = 200;
    private void openCamera() throws CameraAccessException {
        if (ContextCompat.checkSelfPermission
                (Objects.requireNonNull(getActivity()), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(Objects.requireNonNull(getActivity()), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(Objects.requireNonNull(getActivity()), Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(Objects.requireNonNull(getActivity()),new String[]{
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
            },CAMERA_REQUEST);
            return;
        }
        cameraManager.openCamera(cameraId, stateCallback, cameraHandler);
    }
    //--------------------------------------------------------------------------------
    private CameraDevice cameraDevice;
    private CaptureRequest.Builder previewBuilder;

    private Surface surface;
    private Encoder encoder;

    private CameraDevice.StateCallback stateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            cameraDevice = camera;
            SurfaceTexture surfaceTexture = textureView.getSurfaceTexture();
            assert surfaceTexture != null;
            surfaceTexture.setDefaultBufferSize(480,640);
            surface = new Surface(surfaceTexture);
            try {
                previewBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_RECORD);
                previewBuilder.set(CaptureRequest.CONTROL_AE_MODE, CameraMetadata.CONTROL_MODE_AUTO);
                previewBuilder.addTarget(surface);
                Surface encoderSurface = encoder.getSurface();
                previewBuilder.addTarget(encoderSurface);
                List<Surface> mSurface = new ArrayList<>();
                mSurface.add(surface);
                mSurface.add(encoderSurface);
                cameraDevice.createCaptureSession(mSurface,captureSessionCallback,null);
                mqtt.start();
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }
        final CameraCaptureSession.StateCallback captureSessionCallback = new CameraCaptureSession.StateCallback() {
            @Override
            public void onConfigured(@NonNull CameraCaptureSession session) {
                cameraCaptureSession = session;
                try {
                    cameraCaptureSession.setRepeatingRequest(previewBuilder.build(), null, null);
                } catch (CameraAccessException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onConfigureFailed(@NonNull CameraCaptureSession session) {

            }
        };

        @Override
        public void onDisconnected(@NonNull CameraDevice camera) {
            releaseAll();
        }

        @Override
        public void onError(@NonNull CameraDevice camera, int error) {

        }
    };
    //--------------------------------------------------------------------------------
    private void releaseAll() {
        cameraCaptureSession.close();
        surface.release();
        encoder.releaseAll();
        try {
            stopCameraThread();
            mqtt.release();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    //--------------------------------------------------------------------------------
    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        //SetMatrix matrix = new SetMatrix( new Size(width,height),new Size(640,480));
        //textureView.setTransform(matrix.getMatrix());
        try {
            Log.i("surface camera", "opening camera");
            openCamera();
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        releaseAll();
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

    //--------------------------------------------------------------------------------
}
