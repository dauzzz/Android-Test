package com.example.myutils.utils;

import android.graphics.Matrix;
import android.graphics.RectF;
import android.util.Log;
import android.util.Size;

public class SetMatrix {
    private Matrix matrix = new Matrix();

    private SetMatrix(float cameraRatio, float screenRatio, Size cameraSize, Size screenSize){
        Size size;
        if(cameraRatio > screenRatio){
            size = new Size((int) (screenSize.getHeight()*cameraRatio),screenSize.getHeight());
        } else if (cameraRatio < screenRatio){
            size = new Size (screenSize.getWidth(), (int) (screenSize.getWidth()/cameraRatio));
        } else {
            size = screenSize;
        }
        RectF oldRectF = new RectF(0 ,0 ,screenSize.getWidth(),screenSize.getHeight());
        RectF newRectF = new RectF(0 ,0 ,size.getWidth(), size.getHeight());
        matrix.setRectToRect(oldRectF, newRectF, Matrix.ScaleToFit.FILL);
    }

    public Matrix getMatrix(){return matrix;}
}
