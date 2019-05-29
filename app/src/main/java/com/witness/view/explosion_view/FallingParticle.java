package com.witness.view.explosion_view;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

public class FallingParticle extends Particle {

    float radius = FallingParticleFactory.PART_WH;
    float alpha = 1.0f;
    Rect mRect;

    public FallingParticle(int color, float x, float y, Rect rect) {
        super(color, x, y);
        mRect = rect;
    }

    @Override
    protected void draw(Canvas canvas, Paint paint) {
        Log.d("asdfasdfasdf", "draw");
        paint.setColor(color);
        paint.setAlpha((int)(Color.alpha(color) * alpha));
        canvas.drawCircle(cx, cy, radius, paint);
    }

    @Override
    protected void calculate(float factor) {
        cx += factor * Utils.RANDOM.nextInt(mRect.width()) * (Utils.RANDOM.nextFloat() - 0.05f);
        cy +=factor * Utils.RANDOM.nextInt(mRect.height() / 2);
        radius -= factor * Utils.RANDOM.nextInt(2);
        alpha = (1f - factor) * (Utils.RANDOM.nextFloat() + 1);
    }
}
