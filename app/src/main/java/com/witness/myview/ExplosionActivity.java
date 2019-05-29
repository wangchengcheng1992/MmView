package com.witness.myview;

import android.app.Activity;
import android.os.Bundle;

import com.witness.view.explosion_view.ExplosionField;
import com.witness.view.explosion_view.FallingParticleFactory;

public class ExplosionActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_explosion);

        ExplosionField explosionField = new ExplosionField(this, new FallingParticleFactory());
        explosionField.addListener(findViewById(R.id.iv));


    }
}
