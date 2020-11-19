package tw.edu.ndhu.ee.tensorflowmodel.module;

import android.content.Context;
import android.graphics.Point;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MotionEventCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Calendar;

public class StickerRecyclerView extends RecyclerView {

    private static final String TAG = "stickerRecyclerView";

    boolean isChildViewable;
    long startTime;
    PointF startPoint;
    float movedDistance;
    int maxDur = ViewConfiguration.getLongPressTimeout();
    int touchSlop;
    private boolean isScrolling;



    public void setChildViewable(boolean childViewable) {
        isChildViewable = childViewable;
    }

    public StickerRecyclerView(@NonNull Context context) {
        super(context);
        ViewConfiguration viewConfiguration = ViewConfiguration.get(context);
        touchSlop = viewConfiguration.getScaledTouchSlop();
        isChildViewable = false;
        startPoint = new PointF();
    }

    public StickerRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        ViewConfiguration viewConfiguration = ViewConfiguration.get(context);
        touchSlop = viewConfiguration.getScaledTouchSlop();
        isChildViewable = false;
        startPoint = new PointF();
    }

    public StickerRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        ViewConfiguration viewConfiguration = ViewConfiguration.get(context);
        touchSlop = viewConfiguration.getScaledTouchSlop();
        isChildViewable = false;
        startPoint = new PointF();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
        int action = e.getAction();
        if (!isChildViewable) {
            if(MotionEvent.ACTION_DOWN == action) {
                startTime = Calendar.getInstance().getTimeInMillis();
                startPoint.set(e.getX(), e.getY());
            }
            return true;
        }
        /*if(MotionEvent.ACTION_CANCEL == action || MotionEvent.ACTION_UP == action){
            isScrolling = false;
            return false;
        }else if(action == MotionEvent.ACTION_MOVE){
            if(isScrolling)
                return true;
            return isScrolling = isSlided(startPoint,e.getX(),e.getY());
        }else if(action == MotionEvent.ACTION_DOWN){
            startTime = Calendar.getInstance().getTimeInMillis();
            startPoint.set(e.getX(), e.getY());
            isScrolling = false;
        }
        return false;*/
        return super.onInterceptTouchEvent(e);
    }

    private boolean isSlided(PointF startPoint,float endX, float endY){
        float dx = endX - startPoint.x;
        float dy = endY - startPoint.y;
        float d = (float) Math.sqrt(dx*dx + dy*dy);
        return d > touchSlop;
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        int action = e.getAction();
        if(!isChildViewable){
            if(MotionEvent.ACTION_MOVE == action){
                float moveX = e.getX() - startPoint.x;
                float moveY = e.getY() - startPoint.y;
                movedDistance += (float) Math.sqrt(moveX*moveX + moveY*moveY);
                startPoint.set(e.getX(),e.getY());
                return super.onTouchEvent(e);
            } else if(MotionEvent.ACTION_UP == action){
                long endTime = Calendar.getInstance().getTimeInMillis();
                if (endTime - startTime < maxDur && movedDistance <=70) {
                    performClick();
                }
                movedDistance = 0;
                return true;
            }
        }
        return super.onTouchEvent(e);
    }
}
