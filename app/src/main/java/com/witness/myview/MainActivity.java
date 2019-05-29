package com.witness.myview;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

import com.witness.view.leaf_progress_bar.AnimationUtils;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn_leaf_progress_bar).setOnClickListener(this);
        findViewById(R.id.btn_explosion_view).setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_leaf_progress_bar:
                Intent intent = new Intent(this, LeafActivity.class);
                startActivity(intent);
                break;

             case R.id.btn_explosion_view:
                Intent intent1 = new Intent(this, ExplosionActivity.class);
                startActivity(intent1);
                break;
        }
    }
}
