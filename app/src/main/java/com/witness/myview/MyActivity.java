package com.witness.myview;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

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
    }
}
