package com.example.administrator.pathview;

import android.app.Activity;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {

    PayPathView mPayPathView;

    Button success;

    Button failure;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPayPathView = (PayPathView) findViewById(R.id.serach);
        success = (Button) findViewById(R.id.success);

        success.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPayPathView.setAnimatorType(0);
                mPayPathView.setPlayType(1);
                mPayPathView.setAutoExit(false);
                mPayPathView.start();
            }
        });

        failure = (Button) findViewById(R.id.failure);

        failure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPayPathView.setAnimatorType(1);
                mPayPathView.setAutoExit(true);
                mPayPathView.start();
            }
        });
    }
}
