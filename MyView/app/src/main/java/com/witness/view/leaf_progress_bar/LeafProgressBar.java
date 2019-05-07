package com.witness.view.leaf_progress_bar;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.witness.myview.R;

public class LeafProgressBar extends View {

    // 淡白色
    private static final int WHITE_COLOR = 0xfffde399;
    // 橙色
    private static final int ORANGE_COLOR = 0xffffa800;

    // 用于控制绘制的进度条距离左／上／下的距离
    private static final int LEFT_MARGIN = 9;
    // 用于控制绘制的进度条距离右的距离
    private static final int RIGHT_MARGIN = 25;

    // 叶子飘动一个周期所花的时间
    private static final long LEAF_FLOAT_TIME = 3000;
    // 叶子旋转一周需要的时间
    private static final long LEAF_ROTATE_TIME = 2000;

    private Resources mResources;
    private int mLeftMargin;
    private int mRightMargin;

    //飘动的树叶及其宽高信息
    private Bitmap mLeafBitmap;
    private int mLeafWidth;
    private int mLeafHeight;

    //背景框图片及其宽高
    private Bitmap mProgressBackgroundBitmap;
    private int mProgressBackgroundBitmapWidth;
    private int mProgressBackgroundBitmapHeight;

    //bitmap画笔
    private Paint mBitmapPaint, mWhitePaint, mOrangePaint;


    public LeafProgressBar(Context context) {
        super(context);
    }

    public LeafProgressBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mResources = getResources();

        mLeftMargin = UiUtils.dipToPx(context, LEFT_MARGIN);
        mRightMargin = UiUtils.dipToPx(context, RIGHT_MARGIN);

        //初始化图片
        initBitmap();

        //初始化画笔
        initPaint();
    }

    private void initPaint() {
        mBitmapPaint = new Paint();
        //抗锯齿
        mBitmapPaint.setAntiAlias(true);
        //防抖动
        mBitmapPaint.setDither(true);
        //对Bitmap进行滤波处
        mBitmapPaint.setFilterBitmap(true);

        mWhitePaint = new Paint();
        mWhitePaint.setAntiAlias(true);
        mWhitePaint.setColor(WHITE_COLOR);

        mOrangePaint = new Paint();
        mOrangePaint.setAntiAlias(true);
        mOrangePaint.setColor(ORANGE_COLOR);
    }

    private void initBitmap() {
        //飘动的树叶及其宽高信息
        mLeafBitmap = ((BitmapDrawable) mResources.getDrawable(R.mipmap.leaf)).getBitmap();
        mLeafWidth = mLeafBitmap.getWidth();
        mLeafHeight = mLeafBitmap.getHeight();
        //背景框图片及其宽高
        mProgressBackgroundBitmap = ((BitmapDrawable) mResources.getDrawable(R.mipmap.leaf_kuang)).getBitmap();
        mProgressBackgroundBitmapWidth = mLeafBitmap.getWidth();
        mProgressBackgroundBitmapHeight = mLeafBitmap.getHeight();
    }
}
