package controllers.mainFragments.generatorFragments.playlistFragment;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by TheDaAndy on 13.01.2015.
 */
public abstract class OnSwipeListener implements View.OnTouchListener {

    protected float startX, startY, endX, endY;
    private final int MIN_DISTANCE = 100;

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                startX = event.getX();
                startY = event.getY();
                return false;
            }
            case MotionEvent.ACTION_UP: {
                onUp();
                return false;
            }
            case MotionEvent.ACTION_MOVE: {
                endX = event.getX();
                endY = event.getY();

                float distanceX = startX - endX;
                float distanceY = startY - endY;
                if (Math.abs(distanceX) > MIN_DISTANCE) {
                    if (distanceX < 0) {
                        onRightSwipe();
                        return false;
                    } else {
                        onLeftSwipe();
                        return false;
                    }
                }
                if (Math.abs(distanceY) > MIN_DISTANCE) {
                    if (distanceY < 0) {
                        onBottomSwipe();
                        return false;
                    } else {
                        onTopSwipe();
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }

    public void onLeftSwipe() {}
    public void onRightSwipe() {}
    public void onTopSwipe() {}
    public void onBottomSwipe() {}
    public void onUp() {}
}
