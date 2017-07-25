package com.maciejwozny.firefighter;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by maciek on 23.07.17.
 */
public class FlashingView extends View {
    private static final String TAG = "FlashingView";
    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable invalidating = new Runnable(){
        public void run(){
            invalidate();
            handler.postDelayed(this, 1000);
        }
    };
    private Paint RED = new Paint();
    private Paint BLUE = new Paint();
    private boolean isRed = false;


    public FlashingView(Context context) {
        super(context);
    }
    public FlashingView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }
    public FlashingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    public void initFlashing() {
        RED.setColor(Color.RED);
        BLUE.setColor(Color.BLUE);
        invalidating.run();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = canvas.getWidth();
        int height = canvas.getHeight();
        if (isRed) {
            canvas.drawRect(0, 0, width, height, BLUE);
            isRed = false;
        }
        else {
            canvas.drawRect(0, 0, width, height, RED);
            isRed = true;
        }
    }

}
