package com.example.rehealthy;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private SensorManager mSensorManager;
    private Sensor mSensor;
    private float timestamp = 0;
    private float angle[] = new float[3];
    private static final float NS2S = 1.0f / 1000000000.0f;
    private float gx = 0, gy = 0, gz = 0;
    private ImageView imageView;
    private int flag = 0;
    int num;
    private boolean fla=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = (ImageView) findViewById(R.id.showView);
        imageView.setVisibility(View.INVISIBLE);
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        mSensorManager.registerListener(mSensorEventListener, mSensor, SensorManager.SENSOR_DELAY_GAME);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide(); //隐藏标题栏
        }
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);//隐藏状态栏
        // getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN) ;//显示状态栏
        FrameLayout f = (FrameLayout) findViewById(R.id.fragment);
        f.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, Main2Activity.class);
                startActivity(intent);
            }
        });
        Button[] button = new Button[]{
                findViewById(R.id.bt1),
                findViewById(R.id.bt2),
                findViewById(R.id.bt3),
                findViewById(R.id.bt4),
        };
//        for (int i = 0 ; i < 4 ; i++){
//            button[i].setOnClickListener(this);
//        }
        for (Button n : button) {
            n.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt1:
                replaceFragment(new BlankFragment_1());
                fla=false;
                return;
            case R.id.bt2:
                replaceFragment(new BlankFragment_2());
                fla=true;
                return;
            case R.id.bt3:
                replaceFragment(new BlankFragment_3());
                fla=false;
                return;
            case R.id.bt4:
                replaceFragment(new BlankFragment_4());
                fla=true;
                return;
        }
    }

    private void replaceFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment, fragment).commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(mSensorEventListener, mSensor, SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(mSensorEventListener);

    }

    private SensorEventListener mSensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            if (sensorEvent.accuracy != 0) {
                int type = sensorEvent.sensor.getType();
                switch (type) {
                    case Sensor.TYPE_GYROSCOPE:
                        if (fla&&timestamp != 0) {
                            double tmpx = gx, tmpy = gy, tmpz = gx;
                            final float dT = (sensorEvent.timestamp - timestamp) * NS2S;
                            angle[0] += sensorEvent.values[0] * dT;
                            angle[1] += sensorEvent.values[1] * dT;
                            angle[2] += sensorEvent.values[2] * dT;
                            float anglex = (float) Math.toDegrees(angle[0]);
                            float angley = (float) Math.toDegrees(angle[1]);
                            float anglez = (float) Math.toDegrees(angle[2]);
                            if (gx != 0) {
                                float c = gx - anglex;
                                if (Math.abs(c) >= 0.5) {
                                    Log.d("================", "anglex------------>" + (gx - anglex));
                                    gx = anglex;
                                }
                            } else {
                                gx = anglex;
                            }
                            if (Math.abs(tmpx - gx) >= 10.0) {
                                Log.d("++++++++++++++++++++", "tmpx------------>" + tmpx + "gx------------>" + gx);

                                num++;
                                flag++;
                            }
                        }
                        if (fla&&flag > 4) {
                            Toast.makeText(getApplicationContext(), "钓鱼成功", Toast.LENGTH_SHORT).show();
                            imageView.setVisibility(View.VISIBLE);
                            imageView.bringToFront();
                            AnimationSet animationSet=new AnimationSet(true);
                            Animation alphaAnimation=  AnimationUtils.loadAnimation(MainActivity.this, R.anim.alpha);//加载Xml文件中的动画
                            Animation rotateAnimation= AnimationUtils.loadAnimation(MainActivity.this, R.anim.rotate);//加载Xml文件中的动画
                            animationSet.addAnimation(rotateAnimation);
                            animationSet.addAnimation(alphaAnimation);
                            animationSet.setInterpolator(MainActivity.this, android.R.anim.anticipate_interpolator);
                            imageView.startAnimation(animationSet);
                            imageView.setVisibility(View.INVISIBLE);
                            flag = 0;
                        }
                        timestamp = sensorEvent.timestamp;
                        break;
                }
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    };
}
