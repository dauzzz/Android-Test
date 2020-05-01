package com.example.decoder;

import android.graphics.Matrix;
import android.graphics.RectF;
import android.util.Log;
import android.util.Size;

public class SetMatrix {
    private Matrix matrix = new Matrix();

    SetMatrix(Size oldSize, Size newSize) {
        Size size;
        float oldRatio = (float) oldSize.getWidth() / oldSize.getHeight();
        float newRatio = (float) newSize.getWidth() / newSize.getHeight();
        if ((oldRatio < 1 && newRatio > 1) || (oldRatio > 1 && newRatio < 1)) {
            newRatio = 1 / newRatio;
            newSize = new Size(newSize.getHeight(), newSize.getWidth());
        }
        if (newRatio > oldRatio) {
            size = new Size((int) (newSize.getHeight() * oldRatio), newSize.getHeight());
        } else if (newRatio < oldRatio) {
            size = new Size(newSize.getWidth(), (int) (newSize.getWidth() / oldRatio));
        } else {
            size = newSize;
        }
        RectF oldRectF = new RectF(0, 0, oldSize.getWidth(), oldSize.getHeight());
        RectF newRectF = new RectF(0, 0, size.getWidth(), size.getHeight());
        Log.i("TMatrix", oldRectF.toString() + " " + newRectF.toString());
        matrix.setRectToRect(oldRectF, newRectF, Matrix.ScaleToFit.FILL);
        matrix.postRotate(90, oldRectF.width() / 2, oldRectF.height() / 2);
        //matrix.postTranslate((oldRectF.width() -newRectF.width())/2,0);
    }

    public Matrix getMatrix(){return matrix;}
}
