package com.witness.myview;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.ImageView;

/**
 * @author: witness
 * created: 2020-01-05
 * desc:
 */
public class MyActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explosion);

        for (int i = 0; i < 10; i++) {
            Log.d("1111111111","11111");
        }

        ImageView iv = findViewById(R.id.iv);

        Log.d("111","本地分支修改");
        Log.d("111","本地分支修改");
        Log.d("111","本地分支修改");

    }
}
