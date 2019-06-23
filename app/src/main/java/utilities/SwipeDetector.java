package utilities;

import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;

/*
Based on https://en.proft.me/2017/06/25/detecting-gestures-android-gesturedetector/
 */
public class SwipeDetector implements GestureDetector.OnGestureListener {

    String swipe_tag = "SWIPE_LOGGING";
    private boolean leftSwipe = false;
    private boolean rightSwipe = false;

    public boolean getLeftSwipe(){
        return leftSwipe;
    };

    public boolean getRightSwipe(){
        return rightSwipe;
    };

    @Override
    public boolean onDown(MotionEvent e) {
        return true;
    }

    @Override
    public void onShowPress(MotionEvent e) {
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        Log.d(swipe_tag, "onFling Event");

        int MIN_DISTANCE = 150;
        int OFF_PATH = 100;
        int VELOCITY_THRESHOLD = 30;
        int imageIndex = 0;

        if (Math.abs(e1.getY() - e2.getY()) > OFF_PATH)
            return false;

        if (e1.getX() - e2.getX() > MIN_DISTANCE && Math.abs(velocityX) > VELOCITY_THRESHOLD) {
            // Swipe left
           leftSwipe = true;
        } else {
            // Swipe right
            if (e2.getX() - e1.getX() > MIN_DISTANCE && Math.abs(velocityX) > VELOCITY_THRESHOLD) {
                rightSwipe = true;
            }
        }
        return true;
    }

    public void reset(){
        leftSwipe = false;
        rightSwipe = false;
    }
};


