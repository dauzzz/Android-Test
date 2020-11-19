package tw.edu.ndhu.ee.tensorflowmodel.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.Image;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.AspectRatio;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceContour;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;
import com.google.mlkit.vision.face.FaceDetectorOptions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import tw.edu.ndhu.ee.tensorflowmodel.R;
import tw.edu.ndhu.ee.tensorflowmodel.item.Sticker;
import tw.edu.ndhu.ee.tensorflowmodel.module.StickerAdapter;
import tw.edu.ndhu.ee.tensorflowmodel.module.StickerRecyclerView;
import tw.edu.ndhu.ee.tensorflowmodel.tools.MathTools;

public class CameraFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "CameraFragment";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private List<Sticker> stickerList = new ArrayList<>();
    private Sticker sticker;
    private Bitmap stickerBitmap;
    private boolean initialize;

    private PreviewView previewView;
    private ProcessCameraProvider cameraProvider;
    private Camera camera;
    private int lensFacing = CameraSelector.LENS_FACING_BACK;
    private int ASPECT_RATIO;

    private ConstraintLayout parentView;
    private SurfaceView surfaceView;
    private FaceDetector faceDetector;

    public CameraFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BlankFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CameraFragment newInstance(String param1, String param2) {
        CameraFragment fragment = new CameraFragment();
        Bundle args = new Bundle();
        //args.putString(ARG_PARAM1, param1);
        //args.putString(ARG_PARAM2, param2);
        //fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_camera, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        parentView = (ConstraintLayout) view;
        FaceDetectorOptions options = new FaceDetectorOptions.Builder().enableTracking().setContourMode(FaceDetectorOptions.CONTOUR_MODE_ALL).build();
        faceDetector = FaceDetection.getClient(options);
        previewView = view.findViewById(R.id.camera_preview);
        previewView.post(()->{
            addSticker();
            sticker = stickerList.get(0);
            setCameraUI();
            setCamera();
        });
        surfaceView = view.findViewById(R.id.drawing_surface);
        surfaceView.setZOrderOnTop(true);
        SurfaceHolder holder = surfaceView.getHolder();
        holder.setFormat(PixelFormat.TRANSPARENT);
        initialize = true;
    }

    private void addSticker(){
        Context context = requireContext();
        stickerList.add(new Sticker(0,context));
        stickerList.add(new Sticker(0,context));
        stickerList.add(new Sticker(R.drawable.cat_ear,context));
        stickerList.add(new Sticker(R.drawable.hat,context));
        stickerList.add(new Sticker(R.drawable.moustache,context));
        stickerList.add(new Sticker(R.drawable.pig_nose,context));
        stickerList.add(new Sticker(R.drawable.rabbit_ear,context));
        stickerList.add(new Sticker(0,context));
        stickerList.add(new Sticker(0,context));
    }

    private void setCameraUI(){
        ConstraintLayout container = parentView.findViewById(R.id.container);
        if(container != null)
            parentView.removeView(container);
        container = (ConstraintLayout) View.inflate(requireContext(),R.layout.camera_ui,parentView);
        StickerRecyclerView stickerView = container.findViewById(R.id.sticker_view);
        StickerAdapter stickerAdapter = new StickerAdapter(requireContext(),stickerList, (resId,position) -> {
            stickerBitmap = BitmapFactory.decodeResource(getContext().getResources(),resId);
            sticker = stickerList.get(position);
            ViewGroup.LayoutParams params = stickerView.getLayoutParams();
            params.height /= 5;
            stickerView.setLayoutParams(params);
            stickerView.setChildViewable(false);
        });
        stickerView.setAdapter(stickerAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        stickerView.setLayoutManager(layoutManager);
        stickerView.setOnClickListener(v -> {
            if(!stickerAdapter.isViewable()){
                if(initialize){
                    initialize = false;
                    stickerAdapter.notifyItemChanged(0);
                }
                ViewGroup.LayoutParams params = stickerView.getLayoutParams();
                params.height *= 5;
                stickerView.setLayoutParams(params);
                stickerView.setChildViewable(true);
                stickerAdapter.setViewable(true);
                stickerAdapter.addChildOtherThan(stickerAdapter.getChosenOne());
                stickerView.scrollToPosition(stickerAdapter.getChosenOne()-2);
                //stickerAdapter.notifyDataSetChanged();
            }
        });
        SnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(stickerView);
        Button changeLensSide = container.findViewById(R.id.change_side);
        changeLensSide.setOnClickListener(v->{
            if(lensFacing == CameraSelector.LENS_FACING_BACK)
                lensFacing = CameraSelector.LENS_FACING_FRONT;
            else lensFacing = CameraSelector.LENS_FACING_BACK;
            cameraBuild();
        });
    }

    private void setCamera() {
        ListenableFuture<ProcessCameraProvider> providerListenable = ProcessCameraProvider.getInstance(requireContext());
        providerListenable.addListener(()->{
            try {
                cameraProvider = providerListenable.get();
                DisplayMetrics displayMetrics = new DisplayMetrics();
                previewView.getDisplay().getRealMetrics(displayMetrics);
                ASPECT_RATIO = aspectRatio(displayMetrics.widthPixels, displayMetrics.heightPixels);
                cameraBuild();
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(requireContext()));
    }

    private void cameraBuild() {
        ExecutorService executors = Executors.newFixedThreadPool(5);
        CameraSelector cameraSelector = new CameraSelector.Builder().requireLensFacing(lensFacing).build();
        ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .setTargetRotation(previewView.getDisplay().getRotation())
                .setTargetAspectRatio(ASPECT_RATIO)
                .build();
        imageAnalysis.setAnalyzer(executors,analyzer);
        Preview preview = new Preview.Builder()
                .setTargetRotation(previewView.getDisplay().getRotation())
                .setTargetAspectRatio(ASPECT_RATIO)
                .build();
        ImageCapture imageCapture = new ImageCapture.Builder()
                .setTargetRotation(previewView.getDisplay().getRotation())
                .setTargetAspectRatio(ASPECT_RATIO)
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .build();

        cameraProvider.unbindAll();

        camera = cameraProvider.bindToLifecycle(this,cameraSelector,imageAnalysis,preview,imageCapture);
        preview.setSurfaceProvider(previewView.getSurfaceProvider());
    }

    // 33 2 for head -> upper oval
    /*
    * first of all get the value of upper oval and calculate the size of the head,
    * then resize the image to 1.5 of the head size
    * and then calculate the center of the head to be the bottom center of hat ie. y(pic) = y(center of head) - height,
    * then x(pic) = x(center of head) - width/2;
    *
    * for cat ear -> y(pic) = y(center of head) - height/2;
    * moustache -> y(pic) = y(center of head)
    * */
    // 128 130 for nose -> bottom nose


    ImageAnalysis.Analyzer analyzer1 = image -> {
        @SuppressLint("UnsafeExperimentalUsageError") Image image1 = image.getImage();
        if(image1 != null){
            InputImage input = InputImage.fromMediaImage(image1,image.getImageInfo().getRotationDegrees());
            float xProp = (float)previewView.getWidth()/image.getHeight();
            float yProp = (float)previewView.getHeight()/image.getWidth();
            faceDetector.process(input).addOnSuccessListener(faces -> {
                SurfaceHolder holder = surfaceView.getHolder();
                Canvas canvas = holder.lockCanvas();
                if(canvas != null){
                    for(Face face:faces){
                        Paint paint = new Paint();
                        paint.setStyle(Paint.Style.STROKE);
                        paint.setColor(Color.RED);
                        paint.setStrokeWidth(3);
                        canvas.drawColor(0, PorterDuff.Mode.CLEAR);
                        List<FaceContour> faceContours = face.getAllContours();
                        float[][] points = new float[faceContours.size()][];
                        for(int j = 0; j< faceContours.size();j++){
                            FaceContour contours = face.getContour(j);
                            int i = 0;
                            for(PointF point : contours.getPoints()){
                                if(lensFacing == CameraSelector.LENS_FACING_FRONT) {
                                    points[j][i] = (float) ((image.getHeight() - point.x)*xProp);
                                } else {
                                    points[j][i] = (float) (point.x*xProp);
                                }
                                i++;
                                points[j][i] = (float) (point.y*yProp);
                                i++;
                            }
                            canvas.drawPoints(points[j],paint);
                        }
                        Rect oriRect = face.getBoundingBox();
                        int left;
                        int right;
                        if(lensFacing == CameraSelector.LENS_FACING_FRONT){
                            left = image.getHeight() - oriRect.left;
                            right = image.getHeight() - oriRect.right;
                        } else {
                            left = oriRect.left;
                            right = oriRect.right;
                        }
                        RectF changedRect = new RectF((float)(left*xProp)
                                ,(float)(oriRect.top*yProp)
                                ,(float)(right*xProp)
                                ,(float)(oriRect.bottom*yProp));
                        canvas.drawRect(changedRect,paint);
                        if(stickerBitmap != null) canvas.drawBitmap(stickerBitmap,changedRect.left,changedRect.top,null);
                        //Log.i(TAG,"get face: "+face.toString());
                    }
                    holder.unlockCanvasAndPost(canvas);
                }
                image.close();
            })//.addOnCompleteListener(task -> Log.i(TAG,"successful or not" + task.isSuccessful()))
            .addOnFailureListener(fail-> Log.i(TAG,fail.getCause().getMessage()));
        }
    };

    ImageAnalysis.Analyzer analyzer = image -> {
        @SuppressLint("UnsafeExperimentalUsageError") Image image1 = image.getImage();
        if(image1 != null){
            InputImage input = InputImage.fromMediaImage(image1,image.getImageInfo().getRotationDegrees());
            float xProp = (float)previewView.getWidth()/image.getHeight();
            float yProp = (float)previewView.getHeight()/image.getWidth();
            faceDetector.process(input).addOnSuccessListener(faces -> {
                if(sticker.getResId()!= 0){
                    SurfaceHolder holder = surfaceView.getHolder();
                    Canvas canvas = holder.lockCanvas();
                    if(canvas != null){
                        List<Pair<Bitmap,Pair<double[],float[]>>> bitmapList = new ArrayList<>();
                        for(Face face:faces){
                            FaceContour faceContour = face.getContour(sticker.getContourType());
                            int[] anchor = sticker.getAnchor();
                            List<PointF> pointFS = faceContour.getPoints();
                            float[] firstAnchor = new float[]{(lensFacing == CameraSelector.LENS_FACING_BACK?
                                    pointFS.get(anchor[1]).x : image.getHeight()-pointFS.get(anchor[0]).x)*xProp
                                    ,(lensFacing == CameraSelector.LENS_FACING_BACK?
                                    pointFS.get(anchor[1]).y : pointFS.get(anchor[0]).y)*yProp};
                            float[] secondAnchor = new float[]{(lensFacing == CameraSelector.LENS_FACING_BACK?
                                    pointFS.get(anchor[0]).x : image.getHeight()-pointFS.get(anchor[1]).x)*xProp
                                    , (lensFacing == CameraSelector.LENS_FACING_BACK?
                                    pointFS.get(anchor[0]).y : pointFS.get(anchor[1]).y)*yProp};
                            float[] leftToRightVector = MathTools.calculateVector(firstAnchor,secondAnchor);
                            float[] middleAnchor = new float[]{firstAnchor[0]+leftToRightVector[0]/2, firstAnchor[1]+leftToRightVector[1]/2};
                            float[] horizontalVector = MathTools.calculateVector(firstAnchor,new float[]{secondAnchor[0],firstAnchor[1]});
                            double degrees = MathTools.calculateDegrees(leftToRightVector,horizontalVector);
                            double scaleTo = MathTools.calculateAbsoluteValue(leftToRightVector);
                            Bitmap bitmap = sticker.getBitmap();
                            double scale = MathTools.calculateScale(scaleTo,bitmap.getWidth());
                            double realOffset = (double)sticker.getOffset()*scale;
                            double[] offset = MathTools.calculateOffset(realOffset,degrees);
                            bitmapList.add(new Pair<>(scaleAndRotateBitmap(bitmap,(float)scale,(float)degrees), new Pair<>(offset,middleAnchor)));
                        }
                        canvas.drawColor(0, PorterDuff.Mode.CLEAR);
                        for(Pair<Bitmap,Pair<double[],float[]>> pairPair : bitmapList){
                            Bitmap bitmap = pairPair.first;
                            Pair<double[],float[]> pair = pairPair.second;
                            double[] offset = pair.first;
                            float[] middleAnchor = pair.second;
                            float left = middleAnchor[0]-(float)bitmap.getWidth()/2 + (float)offset[0];
                            float top = middleAnchor[1]-(float)bitmap.getHeight()/2 + (float)offset[1];
                            canvas.drawBitmap(bitmap,left,top,null);
                        }
                        holder.unlockCanvasAndPost(canvas);
                        bitmapList.clear();
                    }
                }
                image.close();
            })//.addOnCompleteListener(task -> Log.i(TAG,"successful or not" + task.isSuccessful()))
            .addOnFailureListener(fail-> Log.i(TAG,fail.getCause().getMessage()));
        }
    };

    private int aspectRatio(int width, int height) {
        double matrixRatio = (double)Math.max(width,height)
                /Math.min(width,height);
        double RATIO_16_9 = (double)16/9;
        double RATIO_4_3 = (double) 4/3;
        Log.i(TAG,"ratio: "+matrixRatio);
        if(Math.abs(matrixRatio-RATIO_16_9)<Math.abs(matrixRatio-RATIO_4_3)) {
            Log.i(TAG,"difference ratio: "+ Math.abs(matrixRatio-RATIO_16_9)+" other: "+Math.abs(matrixRatio-RATIO_4_3));
            return AspectRatio.RATIO_16_9;
        }
        return AspectRatio.RATIO_4_3;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(!PermissionsFragment.checkPermissions(requireContext()))
            Navigation.findNavController(requireActivity(),R.id.nav_frame).navigate(
                    CameraFragmentDirections.actionCameraToPermissions()
            );
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        faceDetector.close();
    }

    public  Bitmap scaleAndRotateBitmap(Bitmap bitmap, float scale, float degrees){
        Matrix matrix = new Matrix();
        matrix.preScale(scale,scale);
        matrix.postRotate(degrees);
        return Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,false);
    }
}