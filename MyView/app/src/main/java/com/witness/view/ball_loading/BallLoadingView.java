package com.witness.view.ball_loading;

import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

public class BallLoadingView extends View {


    public BallLoadingView(Context context) {
        super(context);
    }

    public BallLoadingView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public BallLoadingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

    }
}
