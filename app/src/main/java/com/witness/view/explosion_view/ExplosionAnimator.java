package com.witness.view.explosion_view;

import android.animation.ValueAnimator;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

public class ExplosionAnimator extends ValueAnimator {

    private static final int DEFAULT_DURATION = 1500;
    private Particle[][] mParticles;
    private Paint mPaint;
    private View mContainer;
    private ParticleFactory mParticleFactory;


    /**
     *
     * @param field 爆炸效果场地
     * @param bitmap
     * @param rect
     * @param particleFactory
     */
    public ExplosionAnimator(View field, Bitmap bitmap, Rect rect, ParticleFactory particleFactory){
        mParticleFactory = particleFactory;
        mPaint = new Paint();
        mContainer = field;
        setFloatValues(0f, 1.0f);
        setDuration(DEFAULT_DURATION);
        mParticles = mParticleFactory.generateParticles(bitmap, rect);
    }

    public void draw(Canvas canvas){
        if (!isStarted()){
            //动画结束即停止
            return;
        }

        for (Particle[] mParticle : mParticles){
            for (Particle particle : mParticle){
                particle.advance(canvas, mPaint, (float)getAnimatedValue());
            }
        }

        mContainer.invalidate();
    }

    @Override
    public void start() {
        super.start();
        mContainer.invalidate();
    }
}
