package com.witness.view.leaf_progress_bar;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.witness.myview.R;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

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

    // 叶子飘动一个周期所花的时间
    private long mLeafFloatTime = LEAF_FLOAT_TIME;
    // 叶子旋转一周需要的时间
    private long mLeafRotateTime = LEAF_ROTATE_TIME;

    // 用于控制随机增加的时间不抱团
    private int mAddTime;

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
    private Rect mProgressBackgroundSrcRect, mProgressBackgroundDestRect;
    private int mTotalWidth, mTotalHeight;

    // 弧形的半径
    private int mArcRadius;
    private RectF mWhiteRectF, mOrangeRectF, mArcRectF;
    // arc的右上角的x坐标，也是矩形x坐标的起始点
    private int mArcRightLocation;

    // 当前所在的绘制的进度条的位置
    private int mCurrentProgressPosition;

    // 所绘制的进度条部分的宽度
    private int mProgressWidth;

    //bitmap画笔
    private Paint mBitmapPaint, mWhitePaint, mOrangePaint;

    // 用于产生叶子信息
    private LeafFactory mLeafFactory;
    // 产生出的叶子信息
    private List<Leaf> mLeafInfos;


    public LeafProgressBar(Context context) {
        super(context);
    }

    public LeafProgressBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mResources = getResources();

        mLeftMargin = UiUtils.dipToPx(context, LEFT_MARGIN);
        mRightMargin = UiUtils.dipToPx(context, RIGHT_MARGIN);

        mLeafFloatTime = LEAF_FLOAT_TIME;
        mLeafRotateTime = LEAF_ROTATE_TIME;

        //初始化图片
        initBitmap();

        //初始化画笔
        initPaint();

        //创建小树叶
        mLeafFactory = new LeafFactory();
        mLeafInfos = mLeafFactory.generateLeafs();
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
        mProgressBackgroundBitmapWidth = mProgressBackgroundBitmap.getWidth();
        mProgressBackgroundBitmapHeight = mProgressBackgroundBitmap.getHeight();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //绘制图片
        canvas.drawBitmap(mProgressBackgroundBitmap, mProgressBackgroundSrcRect, mProgressBackgroundDestRect, mBitmapPaint);

        canvas.drawRect(mWhiteRectF, mWhitePaint);
        postInvalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mTotalWidth = w;
        mTotalHeight = h;

        //要绘制的bitmap的矩形区域
        mProgressBackgroundSrcRect = new Rect(0, 0, mProgressBackgroundBitmapWidth, mProgressBackgroundBitmapHeight);
        //要将bitmap绘制在屏幕的什么地方
        mProgressBackgroundDestRect = new Rect(0, 0, mTotalWidth, mTotalHeight);

        //进度条的width = 总宽 - 左右两边的margin
        mProgressWidth = mTotalWidth - mLeftMargin - mRightMargin;
        //进度条最左侧的圆角半径 = （总高 - 上下margin）/2
        mArcRadius = (mTotalHeight - 2 * mLeftMargin) / 2;


        mWhiteRectF = new RectF(mLeftMargin + mCurrentProgressPosition, mLeftMargin, mTotalWidth
                - mRightMargin,
                mTotalHeight - mLeftMargin);
        mOrangeRectF = new RectF(mLeftMargin + mArcRadius, mLeftMargin,
                mCurrentProgressPosition
                , mTotalHeight - mLeftMargin);

        mArcRectF = new RectF(mLeftMargin, mLeftMargin, mLeftMargin + 2 * mArcRadius,
                mTotalHeight - mLeftMargin);
        mArcRightLocation = mLeftMargin + mArcRadius;

    }

    /**
     * 小树叶对象，记录小树叶的相关信息数据
     *
     * @author witness
     */

    private class Leaf{
        //小树叶的绘制位置
        int x, y;
        //小树叶飘动时候的振幅
        StartType type;
        //小树叶的旋转角度
        int rotateAngle;
        //小树叶的旋转方向  0----表示顺时针     1-----逆时针
        int rotateDirection;
        //开始时间
        long startTime;

    }

    /**
     * 小树叶生成器，用来产生小树叶
     */
    private class LeafFactory{
        //最大树叶量
        private static final int MAX_LEAFS = 8;

        //创建随机数，用于产生树叶
        Random random = new Random();

        //生成一个小树叶
        public Leaf generateLeaf(){
            Leaf leaf = new Leaf();

            //生成（0，1，2）中的一个随机类型
            int randomType = random.nextInt(3);

            StartType type = StartType.MIDDLE;

            switch (randomType){
                case 0://小树叶飘动时候，中等振幅
                    break;

                case 1://小树叶飘动时候，低振幅
                    type = StartType.LITTLE;
                    break;

                case 2://小树叶飘动时候，高振幅
                    type = StartType.BIG;
                    break;
            }

            //给小树叶leaf对象设置属性
            leaf.type = type;
            // 随机起始的旋转角度
            leaf.rotateAngle = random.nextInt(360);
            // 随机旋转方向（顺时针或逆时针）
            leaf.rotateDirection = random.nextInt(2);
            // 为了产生交错的感觉，让开始的时间有一定的随机性
            mLeafFloatTime = mLeafFloatTime <= 0 ? LEAF_FLOAT_TIME : mLeafFloatTime;
            mAddTime += random.nextInt((int) (mLeafFloatTime * 2));
            leaf.startTime = System.currentTimeMillis() + mAddTime;

            return leaf;
        }

        // 根据最大叶子数产生叶子信息
        public List<Leaf> generateLeafs() {
            return generateLeafs(MAX_LEAFS);
        }

        // 根据传入的叶子数量产生叶子信息
        public List<Leaf> generateLeafs(int leafSize) {
            List<Leaf> leafs = new LinkedList<Leaf>();
            for (int i = 0; i < leafSize; i++) {
                leafs.add(generateLeaf());
            }
            return leafs;
        }
    }

    private enum StartType {
        LITTLE, MIDDLE, BIG
    }
}
