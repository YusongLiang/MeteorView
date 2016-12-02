package com.felix.meteorview;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btMeteorShower;
    private Button btRain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initListener();
    }

    private void initView() {
        btMeteorShower = (Button) findViewById(R.id.bt_meteor_shower);
        btRain = (Button) findViewById(R.id.bt_rain);
    }

    private void initListener() {
        btMeteorShower.setOnClickListener(this);
        btRain.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Intent intent = null;
        switch (view.getId()) {
            case R.id.bt_meteor_shower:
                intent = new Intent(this, MeteorActivity.class);
                break;
            case R.id.bt_rain:
                intent = new Intent(this, RainActivity.class);
                break;
        }
        startActivity(intent);
    }
}
