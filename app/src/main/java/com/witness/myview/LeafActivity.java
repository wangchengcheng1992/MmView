package com.witness.myview;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

import com.witness.view.leaf_progress_bar.AnimationUtils;
import com.witness.view.leaf_progress_bar.LeafProgressBar;

import java.util.Random;

public class LeafActivity extends Activity {
//
    private static final int TAG = 1;
    private ImageView mFanView;
    private LeafProgressBar leafProgressBar;
    private int mProgress = 0;
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case TAG:
                    if (mProgress < 40) {
                        mProgress += 1;
                        // 随机800ms以内刷新一次
                        mHandler.sendEmptyMessageDelayed(TAG,
                                new Random().nextInt(800));
                        leafProgressBar.setProgress(mProgress);
                    } else {
                        mProgress += 1;
                        // 随机1200ms以内刷新一次
                        mHandler.sendEmptyMessageDelayed(TAG,
                                new Random().nextInt(1200));
                        leafProgressBar.setProgress(mProgress);
                    }
                    break;

                default:
                    break;
            }
        };
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_leaf);


        mFanView = findViewById(R.id.fan_pic);
        leafProgressBar = findViewById(R.id.leaf_loading);
        RotateAnimation rotateAnimation = AnimationUtils.initRotateAnimation(false, 1500, true,
                Animation.INFINITE);
        mFanView.startAnimation(rotateAnimation);

//        mProgress++;
//        leafProgressBar.setProgress(mProgress);
        mHandler.sendEmptyMessageDelayed(TAG, 3000);
    }
}
