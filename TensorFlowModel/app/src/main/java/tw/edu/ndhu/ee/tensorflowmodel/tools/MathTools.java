package tw.edu.ndhu.ee.tensorflowmodel.tools;

import android.graphics.Bitmap;
import android.graphics.Matrix;

public class MathTools {
    public static float[] calculateVector(float[] anchor1, float[] anchor2){
        return new float[]{anchor2[0]-anchor1[0],anchor2[1]-anchor1[1]};
    }

    public static double calculateDegrees(float[] vector1, float[] vector2){
        double widthOfAnchor = calculateAbsoluteValue(vector1);
        float aDotB = vector1[0]*vector2[0];
        double ab = widthOfAnchor*vector2[0];
        return Math.acos(aDotB/ab)/Math.PI*180*(vector1[1]<0?-1:1);
    }

    public static double calculateAbsoluteValue(float[] vector){
        return Math.sqrt(vector[0]*vector[0] + vector[1]*vector[1]);
    }

    public static double calculateScale(double scaleTo, double scaleFrom){
        return scaleTo/scaleFrom*1.5;
    }


    public static double[] calculateOffset(double offset, double degrees){
        return new double[]{-1*offset*Math.sin(degrees*Math.PI/180),offset*Math.cos(degrees*Math.PI/180)};
    }
}
