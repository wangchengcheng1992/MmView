package com.witness.view.explosion_view;

import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * 粒子
 */
public abstract class Particle {
    float cx;
    float cy;
    int color;

    public Particle(int color, float x, float y){
        this.color = color;
        cx = x;
        cy = y;
    }

    protected abstract void draw(Canvas canvas, Paint paint);
    protected abstract void calculate(float factor);

    public void advance(Canvas canvas, Paint paint, float factor){

    }
}
