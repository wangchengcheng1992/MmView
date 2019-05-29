package com.witness.view.explosion_view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import java.util.ArrayList;
import java.util.HashMap;

public class ExplosionField extends View {

    private OnClickListener onClickListener;
    private ArrayList<ExplosionAnimator> explosionAnimators;
    private HashMap<View, ExplosionAnimator> explosionAnimatorHashMap;
    private ParticleFactory mParticleFactory;


    public ExplosionField(Context context, ParticleFactory factory) {
        super(context);
        attach2Activity((Activity) context);
        explosionAnimators = new ArrayList<>();
        explosionAnimatorHashMap = new HashMap<>();
        mParticleFactory = factory;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //执行粒子动画绘制
        for (ExplosionAnimator explosionAnimator : explosionAnimators){
            explosionAnimator.draw(canvas);
        }
    }


    /**
     * 传入要分裂的view
     * @param view 当前点击的view
     */
    public void explode(final View view){
        //防止重复
        if (explosionAnimatorHashMap.get(view) != null && explosionAnimatorHashMap.get(view).isStarted()){
            return;
        }
        if (view.getVisibility() != VISIBLE || view.getAlpha() == 0){
            return;
        }
        //得到view相对于整个屏幕的坐标（由于受到状态栏和标题栏的影响， 需要上移）
        final Rect rect = new Rect();
        view.getGlobalVisibleRect(rect);
        //获取标题栏高度
        int currentTop = ((ViewGroup) getParent()).getTop();
        //过去statusBar高度
        Rect frame = new Rect();
        ((Activity)getContext()).getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int statusBarHeight = frame.top;

        //去掉状态栏和标题栏的高度
        rect.offset(0, - currentTop - statusBarHeight);

        if (rect.width() == 0 || rect.height() == 0){
            //宽高如果为0，不做分裂处理
            return;
        }

        //开始震动动画
        ValueAnimator animator = ValueAnimator.ofFloat(0f, 1.0f).setDuration(150);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                view.setTranslationX((Utils.RANDOM.nextFloat()-0.05f) * view.getWidth() * 0.05f);
                view.setTranslationY((Utils.RANDOM.nextFloat()-0.05f) * view.getHeight() * 0.05f);
            }
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                //震动结束
                view.setTranslationX(0);
                view.setTranslationY(0);

                //开始分裂动画
                explode(view, rect);
            }
        });

        animator.start();
    }

    private void explode(final View view, Rect rect){
        //粒子爆炸动画
        final ExplosionAnimator animator = new ExplosionAnimator(this, Utils.createBitmapFromView(view), rect, mParticleFactory);
        explosionAnimators.add(animator);
        explosionAnimatorHashMap.put(view, animator);

        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                view.setClickable(false);
                view.animate().setDuration(150).scaleX(0f).scaleY(0f).alpha(0f).start();

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                view.setClickable(true);
                view.animate().setDuration(150).scaleX(1f).scaleY(1f).alpha(1f).start();

                //移除动画
                explosionAnimators.remove(animator);
                explosionAnimatorHashMap.remove(view);
            }

        });

        animator.start();
    }

    /**
     * 添加全屏的显示动画的场景
     */
    private void attach2Activity(Activity activity){
        ViewGroup rootView = activity.findViewById(Window.ID_ANDROID_CONTENT);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );
        rootView.addView(this, params);
    }

    /**
     * 添加监听
     * @param view (需要有爆破效果的view)
     */
    public void addListener(View view){
        if (view instanceof ViewGroup){
            ViewGroup viewGroup = (ViewGroup)view;
            int count = viewGroup.getChildCount();
            for (int i = 0; i < count; i++) {
                addListener(view);
            }
        } else {
            view.setClickable(true);
            view.setOnClickListener(getOnClickListener());
        }
    }

    private OnClickListener getOnClickListener() {
        if (onClickListener == null){
            onClickListener = new OnClickListener() {
                @Override
                public void onClick(View v) {
                    //开始执行粒子爆破动画
                    ExplosionField.this.explode(v);
                }
            };
        }
        return onClickListener;
    }
}
