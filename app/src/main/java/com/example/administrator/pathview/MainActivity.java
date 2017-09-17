package com.example.administrator.pathview;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

import static java.security.AccessController.getContext;

public class MainActivity extends Activity {

    PayPathView mPayPathView;
    RadioButton rbSuccess;
    RadioButton rbFailure;
    RadioButton rbTogether;
    RadioButton rbApart;
    RadioButton rbAutoExit;
    RadioButton rbNoExit;

    ImageView ivRed;
    ImageView ivYellow;
    ImageView ivBlack;
    ImageView ivGray;
    ImageView ivGreen;

    Button success;
    int selectedColor = Color.WHITE;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPayPathView = (PayPathView) findViewById(R.id.serach);
        success = (Button) findViewById(R.id.success);
        rbSuccess = (RadioButton) findViewById(R.id.rb_success);
        rbFailure = (RadioButton) findViewById(R.id.rb_failure);
        rbTogether = (RadioButton) findViewById(R.id.rb_together);
        rbApart = (RadioButton) findViewById(R.id.rb_apart);
        rbAutoExit = (RadioButton) findViewById(R.id.rb_auto_exit);
        rbNoExit = (RadioButton) findViewById(R.id.rb_no_exit);
        ivBlack = (ImageView) findViewById(R.id.iv_black);
        ivYellow = (ImageView) findViewById(R.id.iv_yellow);
        ivRed = (ImageView) findViewById(R.id.iv_red);
        ivGray = (ImageView) findViewById(R.id.iv_gray);
        ivGreen = (ImageView) findViewById(R.id.iv_green);
        initClickListener();

        success.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rbSuccess.isChecked()) {
                    mPayPathView.setAnimatorType(0);
                    mPayPathView.setSuccessColor(selectedColor);
                } else if (rbFailure.isChecked()) {
                    mPayPathView.setAnimatorType(1);
                    mPayPathView.setFailureColor(selectedColor);
                }

                if (rbTogether.isChecked()) {
                    mPayPathView.setPlayingTogether(true);
                } else if (rbApart.isChecked()) {
                    mPayPathView.setPlayingTogether(false);
                }

                if (rbAutoExit.isChecked()) {
                    mPayPathView.setAutoExit(true);
                } else if (rbNoExit.isChecked()) {
                    mPayPathView.setAutoExit(false);
                }
                mPayPathView.start();
            }
        });
        mPayPathView.setOnFinishListener(new PayPathView.FinishListener() {
            @Override
            public void finish(int state) {
                if (0 == state) {
                    Toast.makeText(MainActivity.this, "支付成功~", Toast.LENGTH_SHORT).show();
                } else if (1 == state) {
                    Toast.makeText(MainActivity.this, "支付失败~", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void initClickListener() {
        setViewClick(ivRed);
        setViewClick(ivGray);
        setViewClick(ivBlack);
        setViewClick(ivYellow);
        setViewClick(ivGreen);
    }

    private void setViewClick(final ImageView imageView) {

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Drawable bacground = imageView.getBackground();
                ColorDrawable colorDrawable = (ColorDrawable) bacground;
                selectedColor = colorDrawable.getColor();
            }
        });
    }

}
