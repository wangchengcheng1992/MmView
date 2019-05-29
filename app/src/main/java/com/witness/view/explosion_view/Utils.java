package com.witness.view.explosion_view;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.View;

import java.util.Random;

/*
 * 生成一个view的bitmap图片
 */
public class Utils {

    public static final Random RANDOM = new Random();
    private static final Canvas CANVAS = new Canvas();

    public static Bitmap createBitmapFromView(View view){
        //使view恢复原样
        view.clearFocus();
        Bitmap bitmap = createBitmapSafely(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888, 1);
        if (bitmap != null){
            synchronized (CANVAS){
                CANVAS.setBitmap(bitmap);
                view.draw(CANVAS);
                CANVAS.setBitmap(null);
            }
        }
        return bitmap;
    }

    private static Bitmap createBitmapSafely(int width, int height, Bitmap.Config config, int retryCount){
        try {
            return Bitmap.createBitmap(width, height, config);
        } catch (OutOfMemoryError e){
            e.printStackTrace();
            if (retryCount > 0){
                System.gc();
                return createBitmapSafely(width, height, config, retryCount - 1);
            }
        }
        return null;
    }
}
