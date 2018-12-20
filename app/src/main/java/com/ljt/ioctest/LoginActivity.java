package com.ljt.ioctest;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.ljt.ioc_annotatation.BindView;

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.tv_test1)
    TextView textView4;

    @BindView(R.id.tv_test2)
    TextView textView5;

    @BindView(R.id.tv_test3)
    TextView textView6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
