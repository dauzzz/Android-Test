package tw.edu.ndhu.ee.tensorflowmodel.item;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;

import com.google.mlkit.vision.face.FaceContour;

import tw.edu.ndhu.ee.tensorflowmodel.R;

public class Sticker {

    int resId;
    Bitmap bitmap;
    int offset = 0;
    int contourType;
    int[] anchor;

    public Sticker(int resId, Context context) {
        this.resId = resId;
        if(resId!=0) {
            bitmap = BitmapFactory.decodeResource(context.getResources(), resId);
            if (resId == R.drawable.cat_ear)
                offset = -100;
            else if (resId == R.drawable.hat || resId == R.drawable.rabbit_ear)
                offset = -500;
            else if (resId == R.drawable.moustache)
                offset = 50;
            else if (resId == R.drawable.pig_nose)
                bitmap = transparentizeBackground(bitmap);
            contourType = (resId == R.drawable.rabbit_ear || resId == R.drawable.hat || resId == R.drawable.cat_ear) ? FaceContour.FACE : FaceContour.NOSE_BOTTOM;
            anchor = (resId == R.drawable.rabbit_ear || resId == R.drawable.hat || resId == R.drawable.cat_ear) ? new int[]{2, 33} : new int[]{2, 0};
        }
    }

    public int getContourType() {
        return contourType;
    }

    public int[] getAnchor() {
        return anchor;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public int getOffset() {
        return offset;
    }

    public int getResId() {
        return resId;
    }

    private Bitmap transparentizeBackground(Bitmap bitmap){
        int[] bufferBitmap = new int[bitmap.getHeight()* bitmap.getWidth()];
        bitmap.getPixels(bufferBitmap,0, bitmap.getWidth(),0,0, bitmap.getWidth(), bitmap.getHeight());
        int sourceHeight = bitmap.getHeight();
        int sourceWidth = bitmap.getWidth();
        for(int j = 0 ; j < sourceHeight;j++){
            for(int i = 0; i < sourceWidth;i++){
                if(bufferBitmap[i+j*sourceWidth] ==0xFFFFFFFF){
                    bufferBitmap[i+j*sourceWidth] = Color.TRANSPARENT;
                }
            }
        }
        return Bitmap.createBitmap(bufferBitmap,0, bitmap.getWidth(), bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
    }
}
