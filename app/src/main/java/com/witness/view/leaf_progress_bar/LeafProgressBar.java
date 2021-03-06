package com.witness.view.leaf_progress_bar;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.witness.myview.R;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class LeafProgressBar extends View {

    private static final String TAG = "LeafProgressBar";

    // 浅色 ---进度条的背景色
    private static final int WHITE_COLOR = 0xfffde399;
    // 橙色 ---实际进度条颜色
    private static final int ORANGE_COLOR = 0xffffa800;

    // 总进度
    private static final int TOTAL_PROGRESS = 100;
    // 当前进度
    private int mProgress;

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

    // 中等振幅大小
    private static final int MIDDLE_AMPLITUDE = 13;
    // 不同类型之间的振幅差距
    private static final int AMPLITUDE_DISPARITY = 5;

    // 中等振幅大小
    private int mMiddleAmplitude = MIDDLE_AMPLITUDE;
    // 振幅差
    private int mAmplitudeDisparity = AMPLITUDE_DISPARITY;

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

//        canvas.drawRect(mWhiteRectF, mWhitePaint);
        //绘制进度条 （同时绘制小树叶）
        drawProgress(canvas);

        //绘制图片
        canvas.drawBitmap(mProgressBackgroundBitmap, mProgressBackgroundSrcRect, mProgressBackgroundDestRect, mBitmapPaint);


        postInvalidate();
    }

    /**
     * 绘制进度条
     * @param canvas canvas
     */
    private void drawProgress(Canvas canvas) {
        //如果当前进度大于100， 设置为100
        if (mProgress >= TOTAL_PROGRESS){
            mProgress = 100;
        }

        //当前所在的绘制的进度条的位置 = 进度条总长 * 当前进度所占比例
        mCurrentProgressPosition = mProgressWidth * mProgress / TOTAL_PROGRESS;

        //如果当前进度很小的时候（即：在进度条最左侧的圆角范围之内），这个时候我们需要绘制的部分应该是一段圆弧
        if (mCurrentProgressPosition < mArcRadius){
            Log.i(TAG, "mProgress = " + mProgress + "\nmCurrentProgressPosition = "
                    + mCurrentProgressPosition
                    + "\nmArcProgressWidth" + mArcRadius);

            ////////////////////////////////////////////////////////////////////////////////////////
            //这块是绘制进度条的底色 （圆弧 + 矩形）
            // 绘制前一段进度条圆弧
            canvas.drawArc(mArcRectF, 90, 180, false, mWhitePaint);
            // 绘制白色矩形
            mWhiteRectF.left = mArcRightLocation;
            canvas.drawRect(mWhiteRectF, mWhitePaint);


            ////////////////////////////////////////////////////////////////////////////////////////
            // 绘制叶子
            drawLeafs(canvas);


            ////////////////////////////////////////////////////////////////////////////////////////
            // 绘制 实际当前进度条   深色
            // 单边角度 cos angle = （mArcRadius - mCurrentProgressPosition）/ mArcRadius
            int angle = (int) Math.toDegrees(Math.acos((mArcRadius - mCurrentProgressPosition)
                    / (float) mArcRadius));
            // 起始角度
            int startAngle = 180 - angle;
            // 扫过角度
            int sweepAngle = 2 * angle;
            Log.i(TAG, "startAngle = " + startAngle);
            canvas.drawArc(mArcRectF, startAngle, sweepAngle, false, mOrangePaint);
        } else {
            //如果当前进度大于最左侧的弧形区域时， 右边的剩余进度区域就是一个矩形， 我们可以先绘制剩余的底色部分  再绘制左侧
            // 绘制底色 RECT
            mWhiteRectF.left = mCurrentProgressPosition;
            canvas.drawRect(mWhiteRectF, mWhitePaint);

            // 绘制叶子
            drawLeafs(canvas);

            //绘制实际进度 （两个部分：弧形 + 矩形）
            //绘制弧形
            canvas.drawArc(mArcRectF, 90, 180, false, mOrangePaint);
            //绘制矩形
            mOrangeRectF.left = mArcRightLocation;
            mOrangeRectF.right = mCurrentProgressPosition;
            canvas.drawRect(mOrangeRectF, mOrangePaint);
        }
    }

    /**
     * 绘制小树叶
     * @param canvas canvas
     */
    private void drawLeafs(Canvas canvas) {
        //设置小树叶旋转一周所需要的时间
        mLeafRotateTime = mLeafRotateTime <= 0 ? LEAF_ROTATE_TIME : mLeafRotateTime;

        long currentTime = System.currentTimeMillis();

        for (int i = 0; i < mLeafInfos.size(); i++) {
            //取出一片树叶
            Leaf leaf = mLeafInfos.get(i);
            if (currentTime > leaf.startTime && leaf.startTime != 0){
                // 绘制叶子－－根据叶子的类型和当前时间得出叶子的坐标（x，y）
                getLeafLocation(leaf, currentTime);
                // 根据时间计算旋转角度
                canvas.save();
                // 通过Matrix控制叶子旋转
                Matrix matrix = new Matrix();
                float transX = mLeftMargin + leaf.x;
                float transY = mLeftMargin + leaf.y;
                Log.i(TAG, "left.x = " + leaf.x + "--leaf.y=" + leaf.y);
                matrix.postTranslate(transX, transY);


                // 通过时间关联旋转角度，则可以直接通过修改LEAF_ROTATE_TIME调节叶子旋转快慢
                float rotateFraction = ((currentTime - leaf.startTime) % mLeafRotateTime)
                        / (float) mLeafRotateTime;
                int angle = (int) (rotateFraction * 360);
                // 根据叶子旋转方向确定叶子旋转角度
                int rotate = leaf.rotateDirection == 0 ? angle + leaf.rotateAngle : -angle
                        + leaf.rotateAngle;
                matrix.postRotate(rotate, transX
                        + mLeafWidth / 2, transY + mLeafHeight / 2);
                canvas.drawBitmap(mLeafBitmap, matrix, mBitmapPaint);
                canvas.restore();
            }
        }
    }

    private void getLeafLocation(Leaf leaf, long currentTime) {
        long intervalTime = currentTime - leaf.startTime;//小树叶的飘动时长
        mLeafFloatTime = mLeafFloatTime <= 0 ? LEAF_FLOAT_TIME : mLeafFloatTime;
        if (intervalTime < 0) {
            return;
        } else if (intervalTime > mLeafFloatTime) {     //小树叶的年龄（产生至今的时间） > 飘动一个周期的时间（从右飘到左边的时间）
            leaf.startTime = System.currentTimeMillis() //树叶startTime = 当前时间 + 加一个随机值    -----产生每个小树叶的时间差
                    + new Random().nextInt((int) mLeafFloatTime);
        }

        float fraction = (float) intervalTime / mLeafFloatTime;


        //树叶飘动走过的路程 = mProgressWidth * fraction， 从而可以算出树叶的坐标
        //x左边
        leaf.x = getLocationX(leaf, fraction);
        //y坐标，需要使用震动幅度
        leaf.y = getLocationY(leaf);
    }

    /**
     * 计算树叶某时刻的横坐标值---x
     * @param leaf          树叶对象
     * @param fraction      时间系数
     * @return              x坐标
     */
    private int getLocationX(Leaf leaf, float fraction){
        return (int) (mProgressWidth - mProgressWidth * fraction);
    }

    /**
     * 计算树叶某时刻的纵坐标值---y
     * @param leaf      树叶对象
     * @return          y坐标
     */
    private int getLocationY(Leaf leaf) {
        // y = A(wx+Q)+h
        float w = (float) ((float) 2 * Math.PI / mProgressWidth);
        float a = mMiddleAmplitude;
        switch (leaf.type) {
            case LITTLE:
                // 小振幅 ＝ 中等振幅 － 振幅差
                a = mMiddleAmplitude - mAmplitudeDisparity;
                break;
            case MIDDLE:
                a = mMiddleAmplitude;
                break;
            case BIG:
                // 小振幅 ＝ 中等振幅 + 振幅差
                a = mMiddleAmplitude + mAmplitudeDisparity;
                break;
            default:
                break;
        }
        Log.i(TAG, "---a = " + a + "---w = " + w + "--leaf.x = " + leaf.x);
        return (int) (a * Math.sin(w * leaf.x)) + mArcRadius * 2 / 3;
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
        Leaf generateLeaf(){
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
        List<Leaf> generateLeafs() {
            return generateLeafs(MAX_LEAFS);
        }

        // 根据传入的叶子数量产生叶子信息
        List<Leaf> generateLeafs(int leafSize) {
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

    /**
     * 设置中等振幅
     */
    public void setMiddleAmplitude(int amplitude) {
        this.mMiddleAmplitude = amplitude;
    }

    /**
     * 设置振幅差
     */
    public void setMplitudeDisparity(int disparity) {
        this.mAmplitudeDisparity = disparity;
    }

    /**
     * 获取中等振幅
     */
    public int getMiddleAmplitude() {
        return mMiddleAmplitude;
    }

    /**
     * 获取振幅差
     */
    public int getMplitudeDisparity() {
        return mAmplitudeDisparity;
    }

    /**
     * 设置进度
     *
     * @param progress 当前进度
     */
    public void setProgress(int progress) {
        this.mProgress = progress;
        postInvalidate();
    }

    /**
     * 设置叶子飘完一个周期所花的时间
     */
    public void setLeafFloatTime(long time) {
        this.mLeafFloatTime = time;
    }

    /**
     * 设置叶子旋转一周所花的时间
     */
    public void setLeafRotateTime(long time) {
        this.mLeafRotateTime = time;
    }

    /**
     * 获取叶子飘完一个周期所花的时间
     */
    public long getLeafFloatTime() {
        mLeafFloatTime = mLeafFloatTime == 0 ? LEAF_FLOAT_TIME : mLeafFloatTime;
        return mLeafFloatTime;
    }

    /**
     * 获取叶子旋转一周所花的时间
     */
    public long getLeafRotateTime() {
        mLeafRotateTime = mLeafRotateTime == 0 ? LEAF_ROTATE_TIME : mLeafRotateTime;
        return mLeafRotateTime;
    }
}
