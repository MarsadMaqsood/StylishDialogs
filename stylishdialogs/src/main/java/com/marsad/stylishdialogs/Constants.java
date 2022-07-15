package com.marsad.stylishdialogs;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.View;

public class Constants {
    public static final View.OnTouchListener FOCUS_TOUCH_LISTENER = (view, event) -> {
        view.performClick();
        Drawable drawable = view.getBackground();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_BUTTON_PRESS:
                drawable.setColorFilter(0x20000000, PorterDuff.Mode.SRC_ATOP);
                view.invalidate();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                drawable.clearColorFilter();
                view.invalidate();
                break;
        }
        view.onTouchEvent(event);


        return false;
    };


}